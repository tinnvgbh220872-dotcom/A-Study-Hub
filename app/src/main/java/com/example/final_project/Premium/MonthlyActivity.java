package com.example.final_project.Premium;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.Payment.PaymentMethodActivity;
import com.example.final_project.R;

public class MonthlyActivity extends AppCompatActivity {

    private Button btnMonthlySubscribe;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthly_detail);

        btnMonthlySubscribe = findViewById(R.id.btnMonthlySubscribe);
        userEmail = getIntent().getStringExtra("email");

        btnMonthlySubscribe.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentMethodActivity.class);
            intent.putExtra("selectedPlan", "Monthly Plan");
            intent.putExtra("email", userEmail);
            startActivity(intent);
        });
    }
}
