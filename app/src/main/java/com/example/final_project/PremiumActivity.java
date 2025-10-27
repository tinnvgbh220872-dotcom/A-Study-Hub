package com.example.final_project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PremiumActivity extends AppCompatActivity {

    private Button btnTrialDetails, btnTrialSubscribe;
    private Button btnMonthlyDetails, btnMonthlySubscribe;
    private Button btnYearlyDetails, btnYearlySubscribe;
    private String userEmail;
    private UserDatabase db;

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
        db = new UserDatabase(this);

        btnTrialDetails.setOnClickListener(v -> startActivity(new Intent(this, TrialActivity.class)));
        btnMonthlyDetails.setOnClickListener(v -> startActivity(new Intent(this, MonthlyActivity.class)));
        btnYearlyDetails.setOnClickListener(v -> startActivity(new Intent(this, YearlyActivity.class)));

        btnTrialSubscribe.setOnClickListener(v -> subscribeTrial());
        btnMonthlySubscribe.setOnClickListener(v -> subscribePaid("Monthly Plan", 4.99));
        btnYearlySubscribe.setOnClickListener(v -> subscribePaid("Yearly Plan", 39.99));
    }

    private void subscribeTrial() {
        if (userEmail == null || userEmail.isEmpty()) return;

        Cursor c = db.getUserByEmail(userEmail);
        if (c != null && c.moveToFirst()) {
            int before = c.getInt(c.getColumnIndexOrThrow("isPremium"));
            Log.d("PremiumActivity", "Before update, isPremium = " + before);
            db.updatePremiumStatus(userEmail, 1);
            c.close();
        }

        db.insertOrder(userEmail, "Trial Plan", 0.0);
        db.insertTransaction(userEmail, "subscription", 0.0,
                new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));

        Toast.makeText(this, "Trial Activated!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, ThankYouActivity.class);
        intent.putExtra("email", userEmail);
        startActivity(intent);
        finish();
    }

    private void subscribePaid(String planName, double price) {
        if (userEmail == null || userEmail.isEmpty()) return;

        Cursor c = db.getUserByEmail(userEmail);
        if (c != null && c.moveToFirst()) {
            int before = c.getInt(c.getColumnIndexOrThrow("isPremium"));
            Log.d("PremiumActivity", "Before update, isPremium = " + before);
            c.close();
        }

        db.insertOrder(userEmail, planName, price);

        Intent intent = new Intent(this, PaymentMethodActivity.class);
        intent.putExtra("userEmail", userEmail);
        intent.putExtra("isPremium", true);
        intent.putExtra("paymentAmount", price);
        intent.putExtra("selectedPlan", planName);
        startActivity(intent);
        finish();
    }
}
