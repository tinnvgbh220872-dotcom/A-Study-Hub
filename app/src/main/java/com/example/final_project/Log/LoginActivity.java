package com.example.final_project.Log;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.SQL.UserDatabase;
import com.example.final_project.MainScreen.MainScreenActivity;
import com.example.final_project.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnSignin, btnSignup;
    private TextView tvForgotPassword;
    private ImageView btnGoogle, btnFacebook;
    private UserDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignin = findViewById(R.id.btnSignin);
        btnSignup = findViewById(R.id.btnSignup);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);

        dbHelper = new UserDatabase(this);

        btnSignin.setOnClickListener(v -> handleLogin());
        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        tvForgotPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            intent.putExtra("email_from_login", email);
            startActivity(intent);
        });


        btnGoogle.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, GoogleLoginActivity.class);
            startActivity(intent);
        });

        btnFacebook.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainScreenActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM users WHERE email=? AND password=?", new String[]{email, password});

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
