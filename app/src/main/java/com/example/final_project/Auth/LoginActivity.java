package com.example.final_project.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.final_project.SQL.UserDatabase;
import com.example.final_project.MainScreen.MainScreenActivity;
import com.example.final_project.R;
import com.example.final_project.Security.CryptoUtil;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private MaterialButton btnSignin, btnSignup, btnGoogle;
    private TextView tvForgotPassword;
    private UserDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        btnGoogle = findViewById(R.id.btnGoogle);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignin = findViewById(R.id.btnSignin);
        btnSignup = findViewById(R.id.btnSignup);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        dbHelper = new UserDatabase(this);

            btnGoogle.setOnClickListener(v -> {
                Toast.makeText(this, "Google Sign-In clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, GoogleLoginActivity.class));
            });

        btnSignin.setOnClickListener(v -> handleLogin());
        btnSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
        tvForgotPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            intent.putExtra("email_from_login", email);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim().toLowerCase();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = CryptoUtil.hashPassword(password);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM users WHERE LOWER(email)=? AND password=?",
                new String[]{email, hashedPassword});

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);

            SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("user_id", userId);
            editor.putString("user_email", email);
            editor.apply();

            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, MainScreenActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
    }

}
