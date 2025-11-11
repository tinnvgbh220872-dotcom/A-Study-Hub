package com.example.final_project.Payment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.SQL.UserDatabase;
import com.example.final_project.R;

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

        userEmail = getIntent().getStringExtra("userEmail");
        paymentAmount = getIntent().getDoubleExtra("paymentAmount", 0);
        isPremium = getIntent().getBooleanExtra("isPremium", false);

        tvAmount.setText("$" + String.format(Locale.getDefault(), "%.2f", paymentAmount));

        btnCreditCard.setOnClickListener(v -> selectMethod("Credit Card"));
        btnPayPal.setOnClickListener(v -> selectMethod("PayPal"));
        btnGooglePay.setOnClickListener(v -> selectMethod("Google Pay"));
        btnMoMo.setOnClickListener(v -> {
            selectMethod("MoMo");
            layoutMoMoQR.setVisibility(android.view.View.VISIBLE);
            layoutVNBankQR.setVisibility(android.view.View.GONE);
        });
        btnVNBankQR.setOnClickListener(v -> {
            selectMethod("VN Bank");
            layoutVNBankQR.setVisibility(android.view.View.VISIBLE);
            layoutMoMoQR.setVisibility(android.view.View.GONE);
        });

        btnConfirmPayment.setOnClickListener(v -> confirmPayment());
    }

    private void selectMethod(String method) {
        selectedMethod = method;
        if (!method.equals("MoMo")) layoutMoMoQR.setVisibility(android.view.View.GONE);
        if (!method.equals("VN Bank")) layoutVNBankQR.setVisibility(android.view.View.GONE);
    }

    private void confirmPayment() {
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Invalid email.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedMethod.isEmpty()) {
            Toast.makeText(this, "Select a payment method.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (paymentAmount <= 0) {
            Toast.makeText(this, "Invalid amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        double transactionAmount = isPremium ? -paymentAmount : paymentAmount;

        boolean added = userDatabase.insertTransaction(userEmail,
                isPremium ? "Premium Subscription" : "Top Up",
                transactionAmount, now);

        if (!added) {
            Toast.makeText(this, "Payment failed.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isPremium) {
            userDatabase.updatePremiumStatus(userEmail, 1);
        }

        Intent intent = new Intent(this, PaymentConfirmationActivity.class);
        intent.putExtra("email", userEmail);
        intent.putExtra("isPremium", isPremium);
        intent.putExtra("paymentAmount", paymentAmount);
        startActivity(intent);
        finish();
    }
}

