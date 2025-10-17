package com.example.final_project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class PremiumActivity extends AppCompatActivity {

    private Button btnTrialDetails, btnTrialSubscribe;
    private Button btnMonthlyDetails, btnMonthlySubscribe;
    private Button btnYearlyDetails, btnYearlySubscribe;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.premium);

        btnTrialDetails = findViewById(R.id.premium_btn_trial_details);
        btnTrialSubscribe = findViewById(R.id.premium_btn_trial_subscribe);
        btnMonthlyDetails = findViewById(R.id.premium_btn_monthly_details);
        btnMonthlySubscribe = findViewById(R.id.premium_btn_monthly_subscribe);
        btnYearlyDetails = findViewById(R.id.premium_btn_yearly_details);
        btnYearlySubscribe = findViewById(R.id.premium_btn_yearly_subscribe);

        userEmail = getIntent().getStringExtra("email");

        btnTrialDetails.setOnClickListener(v -> startActivity(new Intent(this, TrialActivity.class)));
        btnMonthlyDetails.setOnClickListener(v -> startActivity(new Intent(this, MonthlyActivity.class)));
        btnYearlyDetails.setOnClickListener(v -> startActivity(new Intent(this, YearlyActivity.class)));

        btnTrialSubscribe.setOnClickListener(v -> subscribe("Trial Plan"));
        btnMonthlySubscribe.setOnClickListener(v -> subscribe("Monthly Plan"));
        btnYearlySubscribe.setOnClickListener(v -> subscribe("Yearly Plan"));
    }

    private void subscribe(String planName) {
        if (userEmail == null || userEmail.isEmpty()) {
            startActivity(new Intent(this, ThankYouActivity.class));
            return;
        }

        UserDatabase db = new UserDatabase(this);
        Cursor c = db.getUserByEmail(userEmail);
        if (c != null && c.moveToFirst()) {
            int before = c.getInt(c.getColumnIndexOrThrow("isPremium"));
            Log.d("PremiumActivity", "Before update, isPremium = " + before);
            db.updatePremiumStatus(userEmail, 1);
            Cursor c2 = db.getUserByEmail(userEmail);
            if (c2 != null && c2.moveToFirst()) {
                int after = c2.getInt(c2.getColumnIndexOrThrow("isPremium"));
                Log.d("PremiumActivity", "After update, isPremium = " + after);
                c2.close();
            }
            c.close();
        }

        Intent intent = new Intent(this, MainScreenActivity.class);
        intent.putExtra("email", userEmail);
        intent.putExtra("selectedPlan", planName);
        startActivity(intent);
        finish();
    }
}
