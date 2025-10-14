package com.example.final_project;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etEmailCode;
    private Button btnSendCode, btnChangePassword, btnSaveProfile;
    private UserDatabase userDatabase;
    private SQLiteDatabase db;
    private int userId;
    private int generatedCode = 0;
    private boolean isVerified = false;

    private final String SENDER_EMAIL = "tinnvgbh220872@fpt.edu.vn";
    private final String SENDER_PASSWORD = "ybko bfxf ajfh node";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etEmailCode = findViewById(R.id.etEmailCode);
        btnSendCode = findViewById(R.id.btnSendCode);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        userDatabase = new UserDatabase(this);
        db = userDatabase.getWritableDatabase();

        SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        loadUserInfo();

        btnSendCode.setOnClickListener(v -> sendVerificationCode());
        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });
        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void loadUserInfo() {
        Cursor c = db.rawQuery("SELECT fullname, email, phone FROM users WHERE id=?", new String[]{String.valueOf(userId)});
        if (c.moveToFirst()) {
            etFullName.setText(c.getString(0));
            etEmail.setText(c.getString(1));
            etPhone.setText(c.getString(2));
        } else {
            Toast.makeText(this, "User not found in database", Toast.LENGTH_SHORT).show();
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
        new SendEmailTask().execute(email, String.valueOf(generatedCode));
        etEmailCode.setVisibility(View.VISIBLE);
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
            Toast.makeText(this, "Email verified successfully", Toast.LENGTH_SHORT).show();
            etEmailCode.setVisibility(View.GONE);
            btnSendCode.setText("Send Code");
            btnSendCode.setOnClickListener(v -> sendVerificationCode());
        } else {
            Toast.makeText(this, "Wrong verification code", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfile() {
        String fullname = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (fullname.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isVerified) {
            Toast.makeText(this, "Please verify your email before saving", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("fullname", fullname);
        cv.put("email", email);
        cv.put("phone", phone);

        int rows = db.update("users", cv, "id=?", new String[]{String.valueOf(userId)});
        if (rows > 0) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainScreenActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    private class SendEmailTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String recipient = params[0];
            String code = params[1];

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject("Your Verification Code");
                message.setText("Your verification code is: " + code);
                Transport.send(message);
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                new AlertDialog.Builder(EditProfileActivity.this)
                        .setTitle("Verification Code Sent")
                        .setMessage("A verification code has been sent to your email.")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                Toast.makeText(EditProfileActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
