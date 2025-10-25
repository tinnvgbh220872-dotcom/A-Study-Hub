package com.example.final_project;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainScreenActivity extends AppCompatActivity {

    private TextView tvWelcome, tvHelloUser;
    private ImageView imgPremiumStar;
    private Button btnPremiumTitle, btnUpFile;
    private RecyclerView rvFiles;
    private FileAdapter fileAdapter;
    private List<FileItem> fileList;
    private UserDatabase userDatabase;
    private String currentUserEmail;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvHelloUser = findViewById(R.id.tvHelloUser);
        imgPremiumStar = findViewById(R.id.imgPremiumStar);
        btnPremiumTitle = findViewById(R.id.btnPremiumTitle);
        btnUpFile = findViewById(R.id.btnUpFile);
        rvFiles = findViewById(R.id.rvFiles);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        currentUserEmail = getIntent().getStringExtra("email");
        userDatabase = new UserDatabase(this);

        fileList = new ArrayList<>();
        fileAdapter = new FileAdapter(this, fileList);
        rvFiles.setLayoutManager(new LinearLayoutManager(this));
        rvFiles.setAdapter(fileAdapter);

        loadUserInfo();
        loadFiles();

        btnUpFile.setOnClickListener(v -> {
            Intent intent = new Intent(MainScreenActivity.this, UploadFileActivity.class);
            intent.putExtra("email", currentUserEmail);
            startActivity(intent);
        });

        btnPremiumTitle.setOnClickListener(v -> {
            Intent intent = new Intent(MainScreenActivity.this, PremiumActivity.class);
            intent.putExtra("email", currentUserEmail);
            startActivity(intent);
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
        loadFiles();
    }

    private void loadUserInfo() {
        if (currentUserEmail == null) return;
        Cursor cursor = userDatabase.getUserByEmail(currentUserEmail);
        if (cursor != null && cursor.moveToFirst()) {
            String fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
            int isPremium = cursor.getInt(cursor.getColumnIndexOrThrow("isPremium"));
            tvWelcome.setText("Welcome, " + fullname + "!");
            tvHelloUser.setText("Hello, " + fullname);
            imgPremiumStar.setVisibility(isPremium == 1 ? ImageView.VISIBLE : ImageView.GONE);
            Log.d("MainScreen", "User: " + fullname + ", isPremium: " + isPremium);
            cursor.close();
        }
    }

    private void loadFiles() {
        Cursor cursor = null;
        try {
            cursor = userDatabase.getAllFiles();
            fileList.clear();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("filename"));
                    int size = cursor.getInt(cursor.getColumnIndexOrThrow("filesize"));
                    String uri = cursor.getString(cursor.getColumnIndexOrThrow("fileuri"));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("publishedDate"));
                    fileList.add(new FileItem(name, size, uri, email, date));
                } while (cursor.moveToNext());
            }
            fileAdapter.notifyDataSetChanged();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            else if (id == R.id.nav_orders) {
                Intent intent = new Intent(this, PremiumActivity.class);
                intent.putExtra("email", currentUserEmail);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("email", currentUserEmail);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_logout) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}
