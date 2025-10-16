package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PremiumActivity extends AppCompatActivity {

    private Button btnTrialDetails;
    private Button btnTrialSubscribe;
    private Button btnMonthlyDetails;
    private Button btnMonthlySubscribe;
    private Button btnYearlyDetails;
    private Button btnYearlySubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.premium);

        btnTrialDetails = findViewById(R.id.btnTrialDetails);
        btnTrialSubscribe = findViewById(R.id.btnTrialSubscribe);
        btnMonthlyDetails = findViewById(R.id.btnMonthlyDetails);
        btnMonthlySubscribe = findViewById(R.id.btnMonthlySubscribe);
        btnYearlyDetails = findViewById(R.id.btnYearlyDetails);
        btnYearlySubscribe = findViewById(R.id.btnYearlySubscribe);

        btnTrialDetails.setOnClickListener(v -> startActivity(new Intent(this, TrialActivity.class)));
        btnMonthlyDetails.setOnClickListener(v -> startActivity(new Intent(this, MonthlyActivity.class)));
        btnYearlyDetails.setOnClickListener(v -> startActivity(new Intent(this, YearlyActivity.class)));

        btnTrialSubscribe.setOnClickListener(v -> subscribe("Trial Plan"));
        btnMonthlySubscribe.setOnClickListener(v -> subscribe("Monthly Plan"));
        btnYearlySubscribe.setOnClickListener(v -> subscribe("Yearly Plan"));
    }

    private void subscribe(String planName) {
        Intent intent = new Intent(this, OrderActivity.class);
        intent.putExtra("selectedPlan", planName);
        startActivity(intent);
    }
}
