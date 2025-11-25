package com.example.final_project.Profile;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.Profile.ChangePasswordActivity;
import com.example.final_project.SQL.UserDatabase;
import com.example.final_project.Auth.LoginActivity;
import com.example.final_project.MainScreen.MainScreenActivity;
import com.example.final_project.R;

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

        String passedEmail = getIntent().getStringExtra("email");
        if (passedEmail != null && !passedEmail.isEmpty()) {
            etEmail.setText(passedEmail);
        }

        loadUserInfo();

        btnSendCode.setOnClickListener(v -> sendVerificationCode());

        btnChangePassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            Intent intent = new Intent(this, ChangePasswordActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("email", email);
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
        }
        c.close();
    }

    private boolean isEmailExistsForOtherUser(String email) {
        Cursor c = db.rawQuery("SELECT id FROM users WHERE email = ?", new String[]{email});
        boolean exists = false;

        while (c.moveToNext()) {
            int id = c.getInt(0);
            if (id != userId) {
                exists = true;
                break;
            }
        }
        c.close();
        return exists;
    }

    private void sendVerificationCode() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Enter your email first", Toast.LENGTH_SHORT).show();
            return;
        }
        generatedCode = new Random().nextInt(900000) + 100000;
        sendEmailDirect(email, String.valueOf(generatedCode));
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

        if (isEmailExistsForOtherUser(email)) {
            Toast.makeText(this, "This email is already used by another account!", Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("fullname", fullname);
        cv.put("email", email);
        cv.put("phone", phone);

        int rows = db.update("users", cv, "id=?", new String[]{String.valueOf(userId)});
        if (rows > 0) {

            SharedPreferences.Editor editor1 = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
            editor1.putString("email", email);
            editor1.apply();

            SharedPreferences.Editor editor2 = getSharedPreferences("app_prefs", MODE_PRIVATE).edit();
            editor2.putString("user_email", email);
            editor2.apply();

            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainScreenActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmailDirect(String recipientEmail, String code) {
        new Thread(() -> {
            try {
                final String senderEmail = "tinnvgbh220872@fpt.edu.vn";
                final String senderPassword = "ybko bfxf ajfh node";

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(senderEmail, senderPassword);
                            }
                        });

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Your Verification Code");
                message.setText("Your verification code is: " + code);

                Transport.send(message);

                runOnUiThread(() -> new AlertDialog.Builder(EditProfileActivity.this)
                        .setTitle("Verification Code Sent")
                        .setMessage("A verification code has been sent to your email.")
                        .setPositiveButton("OK", null)
                        .show());
            } catch (MessagingException e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(EditProfileActivity.this, "Failed to send email", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
