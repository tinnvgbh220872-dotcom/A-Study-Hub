package com.example.final_project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainScreenActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnUpfile;
    private BottomNavigationView bottomNavigationView;
    private RecyclerView rvFiles;
    private FileAdapter fileAdapter;
    private List<FileItem> fileList;
    private UserDatabase fileDatabase;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnUpfile = findViewById(R.id.btnUpFile);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        rvFiles = findViewById(R.id.rvFiles);

        String username = getIntent().getStringExtra("username");
        tvWelcome.setText((username != null && !username.isEmpty()) ?
                "Welcome, " + username + "!" : "Welcome to A-Study-Hub!");

        fileDatabase = new UserDatabase(this);
        fileList = new ArrayList<>();
        fileAdapter = new FileAdapter(this, fileList);
        rvFiles.setLayoutManager(new LinearLayoutManager(this));
        rvFiles.setAdapter(fileAdapter);

        loadFiles();

        btnUpfile.setOnClickListener(v ->
                startActivity(new Intent(MainScreenActivity.this, UploadFileActivity.class))
        );

        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            else if (id == R.id.nav_orders) {
                startActivity(new Intent(this, OrderActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
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

    @Override
    protected void onResume() {
        super.onResume();
        loadFiles();
    }

    private void loadFiles() {
        Cursor cursor = null;
        List<FileItem> tempList = new ArrayList<>();
        try {
            cursor = fileDatabase.getAllFiles();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("filename"));
                    int size = cursor.getInt(cursor.getColumnIndexOrThrow("filesize"));
                    String uri = cursor.getString(cursor.getColumnIndexOrThrow("fileuri"));
                    tempList.add(new FileItem(name, size, uri));
                } while (cursor.moveToNext());
            }
            fileAdapter.setFileList(tempList);
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error loading files: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
    }
}
