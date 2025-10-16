package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MonthlyActivity extends AppCompatActivity {

    private Button btnMonthlySubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monthly_detail);

        btnMonthlySubscribe = findViewById(R.id.btnMonthlySubscribe);

        btnMonthlySubscribe.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderActivity.class);
            intent.putExtra("selectedPlan", "Monthly Plan");
            startActivity(intent);
        });
    }
}
