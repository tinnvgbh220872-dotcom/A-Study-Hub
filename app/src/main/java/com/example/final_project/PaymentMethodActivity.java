package com.example.final_project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentMethodActivity extends AppCompatActivity {

    private Button btnCreditCard, btnPayPal, btnGooglePay, btnMoMo, btnVNBankQR, btnConfirmPayment;
    private LinearLayout layoutMoMoQR, layoutVNBankQR;
    private String userEmail;
    private UserDatabase userDatabase;
    private String selectedMethod = "";

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

        userDatabase = new UserDatabase(this);
        userEmail = getIntent().getStringExtra("email");

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

        boolean updated = userDatabase.updatePremiumStatus(userEmail, 1);
        if (updated) {
            String fullname = "";
            Cursor cursor = null;
            try {
                cursor = userDatabase.getUserByEmail(userEmail);
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex("fullname");
                    if (idx >= 0) fullname = cursor.getString(idx);
                }
            } catch (Exception ignored) {
            } finally {
                if (cursor != null && !cursor.isClosed()) cursor.close();
            }

            Toast.makeText(this, "Payment successful! Thank you, " + fullname, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, PaymentConfirmationActivity.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error updating account status.", Toast.LENGTH_SHORT).show();
        }
    }
}
