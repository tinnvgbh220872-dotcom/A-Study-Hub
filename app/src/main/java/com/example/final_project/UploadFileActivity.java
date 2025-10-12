package com.example.final_project;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.InputStream;

public class UploadFileActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri fileUri;
    private String fileName;
    private Button btnSelectFile, btnUpload;
    private TextView tvSelectedFile, tvStatus;
    private ProgressBar progressBar;
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

        btnSelectFile.setOnClickListener(v -> openFilePicker());
        btnUpload.setOnClickListener(v -> uploadFile());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            if (fileUri != null) {
                getContentResolver().takePersistableUriPermission(
                        fileUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                );
                fileName = getFileName(fileUri);
                tvSelectedFile.setText("Selected: " + fileName);
            }
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
        } catch (IOException e) {
            return 0;
        }
    }

    private void uploadFile() {
        if (fileUri == null) {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(ProgressBar.VISIBLE);
        tvStatus.setText("Uploading...");
        int fileSize = getFileSize(fileUri);
        boolean success = insertFileToDatabase(fileName, fileUri.toString(), fileSize);
        progressBar.setProgress(100);

        if (success) {
            tvStatus.setText("Upload successful: " + fileName + " (" + fileSize + " bytes)");
            Toast.makeText(this, "File saved to database!", Toast.LENGTH_SHORT).show();
        } else {
            tvStatus.setText("Failed to save to database!");
        }

        startActivity(new Intent(UploadFileActivity.this, MainScreenActivity.class));
        finish();
    }

    private boolean insertFileToDatabase(String name, String uri, int size) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("filename", name);
            values.put("fileuri", uri);
            values.put("filesize", size);
            long id = db.insert("uploaded_files", null, values);
            db.close();
            return id != -1;
        } catch (Exception e) {
            return false;
        }
    }
}
