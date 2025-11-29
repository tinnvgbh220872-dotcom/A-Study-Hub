package com.example.final_project.Auth;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.SQL.UserDatabase;
import com.example.final_project.R;

import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmailRequest;
    private Button btnGetCode, btnBackToLogin;
    private UserDatabase dbHelper;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_request);

        etEmailRequest = findViewById(R.id.etEmailRequest);
        btnGetCode = findViewById(R.id.btnGetCode);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        dbHelper = new UserDatabase(this);

        String emailFromLogin = getIntent().getStringExtra("email_from_login");
        if (emailFromLogin != null && !emailFromLogin.isEmpty()) {
            etEmailRequest.setText(emailFromLogin);
        }

        btnGetCode.setOnClickListener(v -> handleGetCode());
        btnBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void handleGetCode() {
        String email = etEmailRequest.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.getUserByEmail(email);
        boolean exists = cursor != null && cursor.moveToFirst();
        if (cursor != null) cursor.close();

        if (!exists) {
            Toast.makeText(this, "Email not found in database", Toast.LENGTH_SHORT).show();
            return;
        }

        verificationCode = generateCode();
        sendEmailDirect(email, verificationCode);
    }

    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
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
                            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                                return new javax.mail.PasswordAuthentication(senderEmail, senderPassword);
                            }
                        });

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Your Password Reset Code");
                message.setText("Your verification code is: " + code);

                Transport.send(message);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Verification code sent", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, ResetPasswordActivity.class);
                    intent.putExtra("email", recipientEmail);
                    intent.putExtra("code", code);
                    startActivity(intent);
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
