package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TrialActivity extends AppCompatActivity {

    private Button btnTrialSubscribe;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_detail);

        btnTrialSubscribe = findViewById(R.id.btnTrialSubscribe);
        userEmail = getIntent().getStringExtra("email");

        btnTrialSubscribe.setOnClickListener(v -> {
            Intent intent = new Intent(this, ThankYouActivity.class);
            intent.putExtra("selectedPlan", "Trial Plan");
            intent.putExtra("email", userEmail);
            startActivity(intent);
        });
    }
}
