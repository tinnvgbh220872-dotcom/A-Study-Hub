package com.example.final_project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class PaymentConfirmationActivity extends AppCompatActivity {

    private ImageView imgSuccess;
    private TextView tvPaymentSuccess, tvInvoiceMessage;
    private Button btnBackToHome;
    private UserDatabase userDatabase;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paymen_confirmation);

        imgSuccess = findViewById(R.id.imgSuccess);
        tvPaymentSuccess = findViewById(R.id.tvPaymentSuccess);
        tvInvoiceMessage = findViewById(R.id.tvInvoiceMessage);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        userDatabase = new UserDatabase(this);
        userEmail = getIntent().getStringExtra("email");

        sendInvoiceEmail(userEmail);

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentConfirmationActivity.this, MainScreenActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void sendInvoiceEmail(String email) {
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Invalid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = userDatabase.getUserByEmail(email);
        if (cursor != null && cursor.moveToFirst()) {
            String fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
            cursor.close();

            final String senderEmail = "tinnvgbh220872@fpt.edu.vn";
            final String senderPassword = "ybko bfxf ajfh node";
            String subject = "Invoice Confirmation - Premium Subscription";
            String body = "Dear " + fullname + ",\n\n" +
                    "Your payment was successful! Thank you for subscribing to Premium.\n\n" +
                    "Subscription Details:\n" +
                    "- Account: " + email + "\n" +
                    "- Plan: Premium\n" +
                    "- Status: Active\n\n" +
                    "Best regards,\nTrack App Team";

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject(subject);
                message.setText(body);
                Transport.send(message);
                Toast.makeText(this, "Invoice sent to " + email, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to send invoice.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User not found in database.", Toast.LENGTH_SHORT).show();
        }
    }
}
