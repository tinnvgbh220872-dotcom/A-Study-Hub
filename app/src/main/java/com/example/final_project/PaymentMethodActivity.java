package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentMethodActivity extends AppCompatActivity {

    private Button btnCreditCard, btnPayPal, btnGooglePay, btnMoMo, btnVNBankQR, btnConfirmPayment;
    private LinearLayout layoutMoMoQR, layoutVNBankQR;
    private TextView tvAmount;

    private String userEmail;
    private UserDatabase userDatabase;
    private String selectedMethod = "";
    private double paymentAmount = 0;
    private boolean isPremium = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_method);

        btnCreditCard = findViewById(R.id.btnCreditCard);
        btnPayPal = findViewById(R.id.btnPayPal);
        btnGooglePay = findViewById(R.id.btnGooglePay);
        btnMoMo = findViewById(R.id.btnMoMo);
        btnVNBankQR = findViewById(R.id.btnVNBankQR);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        layoutMoMoQR = findViewById(R.id.layoutMoMoQR);
        layoutVNBankQR = findViewById(R.id.layoutVNBankQR);
        tvAmount = findViewById(R.id.tvAmount);

        userDatabase = new UserDatabase(this);

        // Nhận dữ liệu từ Activity trước
        userEmail = getIntent().getStringExtra("userEmail");
        paymentAmount = getIntent().getDoubleExtra("paymentAmount", 0);
        isPremium = getIntent().getBooleanExtra("isPremium", false);

        tvAmount.setText("$" + String.format(Locale.getDefault(), "%.2f", paymentAmount));

        btnCreditCard.setOnClickListener(v -> selectMethod("Credit Card"));
        btnPayPal.setOnClickListener(v -> selectMethod("PayPal"));
        btnGooglePay.setOnClickListener(v -> selectMethod("Google Pay"));
        btnMoMo.setOnClickListener(v -> {
            selectMethod("MoMo");
            layoutMoMoQR.setVisibility(View.VISIBLE);
            layoutVNBankQR.setVisibility(View.GONE);
        });
        btnVNBankQR.setOnClickListener(v -> {
            selectMethod("VN Bank");
            layoutVNBankQR.setVisibility(View.VISIBLE);
            layoutMoMoQR.setVisibility(View.GONE);
        });

        btnConfirmPayment.setOnClickListener(v -> confirmPayment());
    }

    private void selectMethod(String method) {
        selectedMethod = method;
        if (!method.equals("MoMo")) layoutMoMoQR.setVisibility(View.GONE);
        if (!method.equals("VN Bank")) layoutVNBankQR.setVisibility(View.GONE);
    }

    private void confirmPayment() {
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Invalid email address.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedMethod.isEmpty()) {
            Toast.makeText(this, "Please select a payment method.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (paymentAmount <= 0) {
            Toast.makeText(this, "Invalid amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        if (isPremium) {
            boolean added = userDatabase.insertTransaction(userEmail, "Premium Subscription", paymentAmount, now);
            if (!added) {
                Toast.makeText(this, "Error processing premium subscription.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            boolean added = userDatabase.insertTransaction(userEmail, "Top Up", paymentAmount, now);
            if (!added) {
                Toast.makeText(this, "Error processing top-up.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Intent intent = new Intent(PaymentMethodActivity.this, PaymentConfirmationActivity.class);
        intent.putExtra("email", userEmail);
        intent.putExtra("isPremium", isPremium);
        intent.putExtra("paymentAmount", paymentAmount);
        startActivity(intent);
        finish();
    }
}
