package com.example.final_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ReportActivity extends AppCompatActivity {

    private EditText etReport;
    private Button btnSubmitReport;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        etReport = findViewById(R.id.etReport);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);

        userEmail = getIntent().getStringExtra("email");

        btnSubmitReport.setOnClickListener(v -> {
            String reportText = etReport.getText().toString().trim();
            if (!reportText.isEmpty()) {
                Toast.makeText(this, "Report sent successfully by " + userEmail, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Please enter a report message!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
