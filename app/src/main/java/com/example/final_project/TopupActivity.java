package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TopupActivity extends AppCompatActivity {

    private Button btnTopup10, btnTopup50, btnTopup100, btnTopup500, btnConfirmTopup;
    private EditText etCustomTopup;
    private double selectedAmount = 0;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        btnTopup10 = findViewById(R.id.btnTopup10);
        btnTopup50 = findViewById(R.id.btnTopup50);
        btnTopup100 = findViewById(R.id.btnTopup100);
        btnTopup500 = findViewById(R.id.btnTopup500);
        btnConfirmTopup = findViewById(R.id.btnConfirmTopup);
        etCustomTopup = findViewById(R.id.etCustomTopup);

        userEmail = getIntent().getStringExtra("userEmail");

        btnTopup10.setOnClickListener(v -> goToPayment(10));
        btnTopup50.setOnClickListener(v -> goToPayment(50));
        btnTopup100.setOnClickListener(v -> goToPayment(100));
        btnTopup500.setOnClickListener(v -> goToPayment(500));

        btnConfirmTopup.setOnClickListener(v -> {
            String customAmountStr = etCustomTopup.getText().toString().trim();
            if (!TextUtils.isEmpty(customAmountStr)) {
                try {
                    selectedAmount = Double.parseDouble(customAmountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (selectedAmount <= 0) {
                Toast.makeText(this, "Please select or enter an amount", Toast.LENGTH_SHORT).show();
                return;
            }

            goToPayment(selectedAmount);
        });
    }

    private void goToPayment(double amount) {
        Intent intent = new Intent(TopupActivity.this, PaymentMethodActivity.class);
        intent.putExtra("userEmail", userEmail);
        intent.putExtra("paymentAmount", amount);
        intent.putExtra("isPremium", false);
        startActivity(intent);
    }
}
