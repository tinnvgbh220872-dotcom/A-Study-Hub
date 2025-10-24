package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ThankYouActivity extends AppCompatActivity {

    private TextView tvThankYouMessage;
    private Button btnBackHome;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thank_you);

        tvThankYouMessage = findViewById(R.id.tvThankYouMessage);
        btnBackHome = findViewById(R.id.btnBackHome);

        String planName = getIntent().getStringExtra("selectedPlan");
        userEmail = getIntent().getStringExtra("email");
        if (planName == null) planName = "your subscription";

        String message = "We sincerely thank you for choosing the " + planName + " 🎉\n\n"
                + "Your support means a lot to us! 🌟\n"
                + "With your subscription, you'll enjoy exclusive study materials, smarter planning tools, and a smoother learning experience.\n\n"
                + "Happy learning and keep achieving your goals! 📚💡";

        tvThankYouMessage.setText(message);

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(ThankYouActivity.this, MainScreenActivity.class);
            intent.putExtra("email", userEmail);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}
