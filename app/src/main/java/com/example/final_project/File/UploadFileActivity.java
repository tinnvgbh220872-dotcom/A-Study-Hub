package com.example.final_project.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.MainScreen.MainScreenActivity;
import com.example.final_project.SQL.UserDatabase;
import com.example.final_project.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadFileActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri fileUri;
    private String fileName;
    private String userEmail;
    private Button btnSelectFile, btnUpload;
    private TextView tvSelectedFile, tvStatus;
    private ProgressBar progressBar;
    private DatabaseReference databaseRef;
    private UserDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.up_file_activity);

        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnUpload = findViewById(R.id.btnUpload);
        tvSelectedFile = findViewById(R.id.tvSelectedFile);
        tvStatus = findViewById(R.id.tvStatus);
        progressBar = findViewById(R.id.progressBar);
        dbHelper = new UserDatabase(this);

        userEmail = getIntent().getStringExtra("email");
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        btnSelectFile.setOnClickListener(v -> openFilePicker());
        btnUpload.setOnClickListener(v -> uploadFile());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            getContentResolver().takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileName = getFileName(fileUri);
            tvSelectedFile.setText("Selected: " + fileName);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) result = cursor.getString(nameIndex);
            }
        }
        if (result == null) result = uri.getLastPathSegment();
        return result;
    }

    private int getFileSize(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            return inputStream != null ? inputStream.available() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private void uploadFile() {
        if (fileUri == null) {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(ProgressBar.VISIBLE);
        tvStatus.setText("Saving file info to Firebase...");

        int fileSize = getFileSize(fileUri);
        String publishedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        String firebaseKey = databaseRef.push().getKey();
        if (firebaseKey != null) {
            FileMetadata fileData = new FileMetadata(
                    fileName,
                    fileUri.toString(),
                    fileSize,
                    userEmail,
                    publishedDate,
                    "pending"
            );

            databaseRef.child(firebaseKey).setValue(fileData)
                    .addOnSuccessListener(unused -> {
                        boolean success = dbHelper.insertFile(fileName, fileUri.toString(), fileSize, userEmail, publishedDate, firebaseKey);

                        progressBar.setProgress(100);
                        if (success) {
                            tvStatus.setText("File saved to Firebase & local DB!");
                            Toast.makeText(this, "Upload complete!", Toast.LENGTH_SHORT).show();
                        } else {
                            tvStatus.setText("Firebase OK, local DB failed!");
                        }

                        new AlertDialog.Builder(UploadFileActivity.this)
                                .setTitle("Upload Complete")
                                .setMessage("File uploaded successfully. Back to main screen?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    Intent intent = new Intent(UploadFileActivity.this, MainScreenActivity.class);
                                    intent.putExtra("email", userEmail);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                })
                                .setCancelable(false)
                                .show();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        tvStatus.setText("Upload failed: " + e.getMessage());
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        }
    }



    public static class FileMetadata {
        public String filename, fileUri, email, publishedDate, status;
        public int filesize;

        public FileMetadata() {}

        public FileMetadata(String filename, String fileUri, int filesize, String email, String publishedDate, String status) {
            this.filename = filename;
            this.fileUri = fileUri;
            this.filesize = filesize;
            this.email = email;
            this.publishedDate = publishedDate;
            this.status = status;
        }
    }
}
