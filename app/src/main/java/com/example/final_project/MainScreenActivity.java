package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainScreenActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnProfile;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnProfile = findViewById(R.id.btnProfile);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        String username = getIntent().getStringExtra("username");
        if (username != null && !username.isEmpty()) {
            tvWelcome.setText("Welcome, " + username + "!");
        } else {
            tvWelcome.setText("Welcome to A-Study-Hub!");
        }
    }
}

//        btnProfile.setOnClickListener(v -> {
//            Intent intent = new Intent(MainScreenActivity.this, ProfileActivity.class);
//            startActivity(intent);
//        });
//
//        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                int id = item.getItemId();
//
//                if (id == R.id.nav_home) {
//                    Toast.makeText(MainScreenActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
//                    return true;
//                } else if (id == R.id.nav_library) {
//                    Intent intent = new Intent(MainScreenActivity.this, LibraryActivity.class);
//                    startActivity(intent);
//                    return true;
//                } else if (id == R.id.nav_settings) {
//                    Intent intent = new Intent(MainScreenActivity.this, SettingsActivity.class);
//                    startActivity(intent);
//                    return true;
//                }
//
//                return false;
//            }
//        });
//    }
//}
