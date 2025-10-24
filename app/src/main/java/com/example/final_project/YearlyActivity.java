package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class YearlyActivity extends AppCompatActivity {

    private Button btnYearlySubscribe;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yearly_detail);

        btnYearlySubscribe = findViewById(R.id.btnYearlySubscribe);
        userEmail = getIntent().getStringExtra("email");

        btnYearlySubscribe.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentMethodActivity.class);
            intent.putExtra("email", userEmail);
            intent.putExtra("selectedPlan", "Yearly Plan");
            startActivity(intent);
        });
    }
}
