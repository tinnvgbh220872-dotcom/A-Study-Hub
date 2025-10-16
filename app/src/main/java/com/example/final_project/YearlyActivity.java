package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class YearlyActivity extends AppCompatActivity {

    private Button btnYearlySubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yearly_detail);

        btnYearlySubscribe = findViewById(R.id.btnYearlySubscribe);

        btnYearlySubscribe.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderActivity.class);
            intent.putExtra("selectedPlan", "Yearly Plan");
            startActivity(intent);
        });
    }
}
