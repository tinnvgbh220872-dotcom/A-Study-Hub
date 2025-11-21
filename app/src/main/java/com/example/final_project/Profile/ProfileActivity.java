package com.example.final_project.Profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.Auth.LoginActivity;
import com.example.final_project.MainScreen.MainScreenActivity;
import com.example.final_project.Payment.MyOrdersActivity;
import com.example.final_project.Payment.WalletActivity;
import com.example.final_project.Premium.PremiumActivity;
import com.example.final_project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView btnEditProfile, btnMyDocuments, btnMyWallet, btnPremium, btnSettings, btnLogout;
    private TextView tvUsername, tvEmail;
    private int userId;
    private String username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        tvUsername = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnMyDocuments = findViewById(R.id.btnMyDocuments);
        btnMyWallet = findViewById(R.id.btnMyWallet);
        btnPremium = findViewById(R.id.btnPremium);
        btnSettings = findViewById(R.id.btnSettings);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);
        if (userId == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        com.example.final_project.SQL.UserDatabase userDb = new com.example.final_project.SQL.UserDatabase(this);
        Cursor c = userDb.getReadableDatabase().rawQuery(
                "SELECT fullname, email FROM users WHERE id=?",
                new String[]{String.valueOf(userId)}
        );

        if (c.moveToFirst()) {
            username = c.getString(0);
            email = c.getString(1);
        } else {
            username = "User";
            email = "";
        }
        c.close();

        tvUsername.setText(username);
        tvEmail.setText(email);

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        btnMyDocuments.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MyOrdersActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        btnMyWallet.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, WalletActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        btnPremium.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, PremiumActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(ProfileActivity.this, MainScreenActivity.class);
                intent.putExtra("user_id", userId);
                intent.putExtra("email", email);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_orders) {
                Intent intent = new Intent(ProfileActivity.this, PremiumActivity.class);
                intent.putExtra("user_id", userId);
                intent.putExtra("email", email);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            } else if (itemId == R.id.nav_logout) {
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}
