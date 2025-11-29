package com.example.final_project.Auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.SQL.UserDatabase;
import com.example.final_project.MainScreen.MainScreenActivity;
import com.example.final_project.R;

public class GoogleLoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnLogin, btnCancel;
    private UserDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_google_activity);

        edtEmail = findViewById(R.id.edtEmail);
        btnLogin = findViewById(R.id.btnLogin);
        btnCancel = findViewById(R.id.btnCancel);
        dbHelper = new UserDatabase(this);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim().toLowerCase();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your Gmail address", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!email.endsWith("@gmail.com")) {
                Toast.makeText(this, "Please use a Gmail address (@gmail.com)", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isEmailRegistered(email)) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT id FROM users WHERE LOWER(email)=?", new String[]{email});
                if (cursor.moveToFirst()) {
                    int userId = cursor.getInt(0);
                    SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("user_id", userId);
                    editor.putString("user_email", email);
                    editor.apply();
                    Toast.makeText(this, "Google Login successful: " + email, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(GoogleLoginActivity.this, MainScreenActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();
                }
                cursor.close();
                db.close();
            } else {
                Toast.makeText(this, "This Gmail is not registered. Please sign up first.", Toast.LENGTH_LONG).show();
            }
        });

        btnCancel.setOnClickListener(v -> {
            Toast.makeText(this, "Login cancelled", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private boolean isEmailRegistered(String email) {
        if (email == null || email.isEmpty()) return false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;
        try {
            cursor = db.rawQuery("SELECT 1 FROM users WHERE LOWER(email)=?", new String[]{email.toLowerCase()});
            exists = cursor.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return exists;
    }
}
