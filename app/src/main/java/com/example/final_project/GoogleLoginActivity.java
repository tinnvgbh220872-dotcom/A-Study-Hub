package com.example.final_project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class GoogleLoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnLogin, btnCancel;
    private UserDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_login_activity);

        edtEmail = findViewById(R.id.edtEmail);
        btnLogin = findViewById(R.id.btnLogin);
        btnCancel = findViewById(R.id.btnCancel);
        dbHelper = new UserDatabase(this);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your Gmail address", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!email.endsWith("@gmail.com")) {
                Toast.makeText(this, "Please use a Gmail address (@gmail.com)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEmailRegistered(email)) {
                Toast.makeText(this, "Login successful: " + email, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(GoogleLoginActivity.this, MainScreenActivity.class));
                finish();
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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
}
