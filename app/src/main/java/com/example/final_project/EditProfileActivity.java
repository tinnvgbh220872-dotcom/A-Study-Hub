package com.example.final_project;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class EditProfileActivity extends AppCompatActivity {

    EditText etFullName, etEmail, etEmailCode, etPhone;
    Button btnSendCode, btnChangePassword, btnSaveProfile;
    SQLiteDatabase db;
    int generatedCode = 0;
    boolean isVerified = false;
    String currentUserEmail = "user@example.com"; // giả lập tài khoản hiện tại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etEmailCode = findViewById(R.id.etEmailCode);
        etPhone = findViewById(R.id.etPhone);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        db = openOrCreateDatabase("UserDB.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT, fullname TEXT, email TEXT, phone TEXT, password TEXT)");

        loadUserInfo();

        btnSendCode.setOnClickListener(v -> sendVerificationCode());
        btnSaveProfile.setOnClickListener(v -> saveProfile());
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(EditProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserInfo() {
        Cursor c = db.rawQuery("SELECT fullname, email, phone FROM users WHERE email=?", new String[]{currentUserEmail});
        if (c.moveToFirst()) {
            etFullName.setText(c.getString(0));
            etEmail.setText(c.getString(1));
            etPhone.setText(c.getString(2));
        }
        c.close();
    }

    private void sendVerificationCode() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter your email first", Toast.LENGTH_SHORT).show();
            return;
        }
        generatedCode = new Random().nextInt(900000) + 100000;
        etEmailCode.setVisibility(View.VISIBLE);
        new AlertDialog.Builder(this)
                .setTitle("Verification Code Sent")
                .setMessage("Verification code: " + generatedCode + "\n(This is a simulation)")
                .setPositiveButton("OK", null)
                .show();

        btnSendCode.setText("Verify");
        btnSendCode.setOnClickListener(v -> verifyCode());
    }

    private void verifyCode() {
        String codeInput = etEmailCode.getText().toString().trim();
        if (codeInput.isEmpty()) {
            Toast.makeText(this, "Enter verification code", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Integer.parseInt(codeInput) == generatedCode) {
            isVerified = true;
            Toast.makeText(this, "Email verified", Toast.LENGTH_SHORT).show();
            etEmailCode.setVisibility(View.GONE);
            btnSendCode.setText("Send Code");
            btnSendCode.setOnClickListener(v -> sendVerificationCode());
        } else {
            Toast.makeText(this, "Wrong code", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfile() {
        String fullname = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (fullname.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isVerified && !email.equals(currentUserEmail)) {
            Toast.makeText(this, "Please verify your new email", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("fullname", fullname);
        cv.put("email", email);
        cv.put("phone", phone);

        int rows = db.update("users", cv, "email=?", new String[]{currentUserEmail});
        if (rows > 0) {
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            currentUserEmail = email;
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }
}
