package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

    private Button btnBackToMain;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paymen_confirmation);

        btnBackToMain = findViewById(R.id.btnBackToHome);
        userEmail = getIntent().getStringExtra("email");

        sendInvoiceEmail(userEmail);

        btnBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentConfirmationActivity.this, MainScreenActivity.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
            finish();
        });
    }

    private void sendInvoiceEmail(String toEmail) {
        if (toEmail == null || toEmail.isEmpty()) {
            Toast.makeText(this, "Invalid email address.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                final String senderEmail = "tinnvgbh220872@fpt.edu.vn";
                final String senderPassword = "ybko bfxf ajfh node";

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

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Payment Confirmation - Premium Subscription");
                message.setText(
                        "Dear User,\n\n" +
                                "Thank you for your payment! Your premium subscription is now active.\n\n" +
                                "If you have any questions, feel free to contact our support.\n\n" +
                                "Best regards,\nA-Study-Hub Team"
                );

                Transport.send(message);

                runOnUiThread(() ->
                        Toast.makeText(PaymentConfirmationActivity.this,
                                "Invoice sent to " + toEmail, Toast.LENGTH_SHORT).show()
                );

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(PaymentConfirmationActivity.this,
                                "Failed to send email: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
}
