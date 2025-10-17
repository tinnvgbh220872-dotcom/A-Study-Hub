package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TrialActivity extends AppCompatActivity {

    private Button btnTrialSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trial_detail);

        btnTrialSubscribe = findViewById(R.id.btnTrialSubscribe);

        btnTrialSubscribe.setOnClickListener(v -> {
            Intent intent = new Intent(this, ThankYouActivity.class);
            intent.putExtra("subscribedPlan", "Trial Plan");
            startActivity(intent);
        });
    }
}
