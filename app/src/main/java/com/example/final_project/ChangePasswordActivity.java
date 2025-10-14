package com.example.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etOldPassword, etNewPassword, etConfirmPassword;
    private Button btnCancel, btnSavePassword;
    private UserDatabase userDatabase;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCancel = findViewById(R.id.btnCancel);
        btnSavePassword = findViewById(R.id.btnSavePassword);

        userDatabase = new UserDatabase(this);

        Intent intent = getIntent();
        userId = intent != null ? intent.getIntExtra("user_id", -1) : -1;
        if (userId == -1) {
            SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
            userId = sp.getInt("user_id", -1);
        }

        btnSavePassword.setOnClickListener(v -> {
            String oldPass = etOldPassword.getText().toString().trim();
            String newPass = etNewPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userId == -1) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentPass = userDatabase.getPasswordById(userId);
            if (currentPass == null || !currentPass.equals(oldPass)) {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean updated = userDatabase.updatePasswordById(userId, newPass);
            if (updated) {
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to change password", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
