package com.example.final_project.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.MainScreen.MainScreenActivity;
import com.example.final_project.R;
import com.example.final_project.SQL.UserDatabase;
import com.example.final_project.Security.CryptoUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText etOldPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnSavePassword, btnCancel;
    private UserDatabase dbHelper;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);
        btnCancel = findViewById(R.id.btnCancel);

        dbHelper = new UserDatabase(this);

        userEmail = getIntent().getStringExtra("email");
        if (userEmail == null) {
            Toast.makeText(this, "Error: Missing user email", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSavePassword.setOnClickListener(v -> changePassword());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void changePassword() {
        String oldPass = etOldPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!checkOldPassword(userEmail, oldPass)) {
            Toast.makeText(this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        if (updatePassword(userEmail, newPass)) {
            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainScreenActivity.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkOldPassword(String email, String oldPass) {
        String hashedOldPass = CryptoUtil.hashPassword(oldPass);
        return dbHelper.updatePassword(email, hashedOldPass);
    }

    private boolean updatePassword(String email, String newPass) {
        String hashedNewPass = CryptoUtil.hashPassword(newPass);
        return dbHelper.updatePassword(email, hashedNewPass);
    }
}
