package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etVerificationCode, etNewPassword, etConfirmPassword;
    private Button btnResetPassword, btnBackToLogin;

    private UserDatabase userDatabase;
    private String emailFromIntent;
    private String verificationCodeFromIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);

        etVerificationCode = findViewById(R.id.etVerificationCode);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        userDatabase = new UserDatabase(this);

        // ✅ Nhận email & code từ màn trước
        emailFromIntent = getIntent().getStringExtra("email");
        verificationCodeFromIntent = getIntent().getStringExtra("code");

        btnResetPassword.setOnClickListener(v -> handlePasswordReset());
        btnBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void handlePasswordReset() {
        String enteredCode = etVerificationCode.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(enteredCode)) {
            etVerificationCode.setError("Please enter the verification code");
            return;
        }

        if (!enteredCode.equals(verificationCodeFromIntent)) {
            etVerificationCode.setError("Incorrect verification code");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("Please enter a new password");
            return;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        boolean updated = userDatabase.updatePassword(emailFromIntent, newPassword);
        if (updated) {
            Toast.makeText(this, "Password reset successfully!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Error updating password. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
