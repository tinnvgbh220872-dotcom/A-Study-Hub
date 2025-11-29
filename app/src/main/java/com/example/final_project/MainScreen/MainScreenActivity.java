package com.example.final_project.MainScreen;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.File.FileAdapter;
import com.example.final_project.File.FileItem;
import com.example.final_project.File.UploadFileActivity;
import com.example.final_project.Firebase.FirebaseFileModel;
import com.example.final_project.Auth.LoginActivity;
import com.example.final_project.Premium.PremiumActivity;
import com.example.final_project.Profile.ProfileActivity;
import com.example.final_project.Quiz.QuizActivity;
import com.example.final_project.R;
import com.example.final_project.SQL.UserDatabase;
import com.example.final_project.Security.CryptoUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainScreenActivity extends AppCompatActivity {

    private TextView tvWelcome, tvHelloUser, premiumTitle, premiumSubtitle;
    private ImageView imgPremiumStar;
    private MaterialButton btnExplorePremium, btnUpFile;
    private RecyclerView rvFiles;
    private FileAdapter fileAdapter;
    private List<FileItem> fileList;
    private UserDatabase userDatabase;
    private String currentUserEmail;
    private BottomNavigationView bottomNavigationView;
    private DatabaseReference postsRef;
    private ValueEventListener postsListener;
    private final Map<String, FirebaseFileModel> allFilesMap = new ConcurrentHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_activity);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvHelloUser = findViewById(R.id.tvHelloUser);
        imgPremiumStar = findViewById(R.id.imgPremiumStar);
        premiumTitle = findViewById(R.id.premiumTitle);
        premiumSubtitle = findViewById(R.id.premiumSubtitle);
        btnExplorePremium = findViewById(R.id.btnExplorePremium);
        btnUpFile = findViewById(R.id.btnUpFile);
        rvFiles = findViewById(R.id.rvFiles);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        FloatingActionButton btnChatBot = findViewById(R.id.btnChatBot);

        currentUserEmail = getIntent().getStringExtra("email");
        userDatabase = new UserDatabase(this);

        fileList = new ArrayList<>();
        fileAdapter = new FileAdapter(this, fileList, currentUserEmail);
        rvFiles.setLayoutManager(new LinearLayoutManager(this));
        rvFiles.setAdapter(fileAdapter);

        postsRef = FirebaseDatabase.getInstance().getReference("uploads");

        loadUserInfo();
        attachFirebaseListener();

        btnUpFile.setOnClickListener(v -> {
            Intent intent = new Intent(MainScreenActivity.this, UploadFileActivity.class);
            intent.putExtra("email", currentUserEmail);
            startActivity(intent);
        });

        btnExplorePremium.setOnClickListener(v -> {
            Intent intent = new Intent(MainScreenActivity.this, PremiumActivity.class);
            intent.putExtra("email", currentUserEmail);
            startActivity(intent);
        });

        btnChatBot.setOnClickListener(v -> {
            Intent intent = new Intent(MainScreenActivity.this, QuizActivity.class);
            intent.putExtra("email", currentUserEmail);
            startActivity(intent);
        });

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
    }

    private void loadUserInfo() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) {
            tvWelcome.setText("Welcome!");
            tvHelloUser.setText("Hello, Guest");
            imgPremiumStar.setVisibility(View.GONE);
            return;
        }

        try (Cursor cursor = userDatabase.getUserByEmail(currentUserEmail)) {
            if (cursor != null && cursor.moveToFirst()) {
                String encryptedFullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
                String fullname = encryptedFullname;

                try {
                    fullname = CryptoUtil.decrypt(encryptedFullname);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                tvWelcome.setText("Welcome, " + fullname + "!");
                tvHelloUser.setText("Hello, " + fullname);

                int isPremium = cursor.getInt(cursor.getColumnIndexOrThrow("isPremium"));
                imgPremiumStar.setVisibility(isPremium == 1 ? View.VISIBLE : View.GONE);
            }
        }
    }



    private void attachFirebaseListener() {
        if (currentUserEmail == null || currentUserEmail.isEmpty()) return;

        postsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allFilesMap.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    FirebaseFileModel file = postSnapshot.getValue(FirebaseFileModel.class);
                    if (file != null) {
                        file.postId = postSnapshot.getKey();
                        if ("global".equalsIgnoreCase(file.status) || "approved".equalsIgnoreCase(file.status)
                                || (currentUserEmail.equalsIgnoreCase(file.email) && "pending".equalsIgnoreCase(file.status))) {
                            allFilesMap.put(file.postId, file);
                        }
                    }
                }
                refreshFileList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "postsListener cancelled: " + error.getMessage());
            }
        };

        postsRef.addValueEventListener(postsListener);
    }

    private void refreshFileList() {
        fileList.clear();
        for (FirebaseFileModel file : allFilesMap.values()) {
            fileList.add(new FileItem(
                    0,
                    file.filename,
                    (int) file.filesize,
                    file.fileUri,
                    file.email,
                    file.publishedDate,
                    file.status,
                    file.postId
            ));
        }

        Collections.sort(fileList, (o1, o2) -> {
            if (o1.getPublishedDate() == null || o2.getPublishedDate() == null) return 0;
            return o2.getPublishedDate().compareTo(o1.getPublishedDate());
        });

        fileAdapter.notifyDataSetChanged();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (postsRef != null && postsListener != null) {
            postsRef.removeEventListener(postsListener);
        }
    }
}
