package com.example.final_project.Payment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.MainScreen.MainScreenActivity;
import com.example.final_project.R;

import java.util.Locale;
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
    private boolean isPremium;
    private double paymentAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_confirmation);

        btnBackToMain = findViewById(R.id.btnBackToHome);

        userEmail = getIntent().getStringExtra("email");
        isPremium = getIntent().getBooleanExtra("isPremium", false);
        paymentAmount = getIntent().getDoubleExtra("paymentAmount", 0);

        sendInvoiceEmail(userEmail, paymentAmount, isPremium);

        btnBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentConfirmationActivity.this, MainScreenActivity.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
            finish();
        });
    }

    private void sendInvoiceEmail(String toEmail, double amount, boolean isPremium) {
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

                if (isPremium) {
                    message.setSubject("Payment Confirmation - Premium Subscription");
                    message.setText(
                            "Dear User,\n\n" +
                                    "Thank you for your payment! Your premium subscription is now active.\n\n" +
                                    "If you have any questions, feel free to contact our support.\n\n" +
                                    "Best regards,\nA-Study-Hub Team"
                    );
                } else {
                    message.setSubject("Payment Confirmation - Top-up Successful");
                    message.setText(String.format(Locale.getDefault(),
                            "Dear User,\n\nThank you for trusting us! Your top-up of $%.0f has been successfully processed.\n\n" +
                                    "Enjoy using our services, and if you have any questions, feel free to contact support.\n\n" +
                                    "Best regards,\nA-Study-Hub Team", amount));
                }

                Transport.send(message);

                runOnUiThread(() -> Toast.makeText(
                        PaymentConfirmationActivity.this,
                        isPremium ? "Premium subscription activated!" :
                                String.format(Locale.getDefault(), "Top-up successful! $%.0f added.", amount),
                        Toast.LENGTH_SHORT
                ).show());

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(
                        PaymentConfirmationActivity.this,
                        "Failed to send email: " + e.getMessage(),
                        Toast.LENGTH_LONG
                ).show());
            }
        }).start();
    }
}
