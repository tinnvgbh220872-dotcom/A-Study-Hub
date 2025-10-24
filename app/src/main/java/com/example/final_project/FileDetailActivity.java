package com.example.final_project;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileDetailActivity extends AppCompatActivity {

    private TextView tvFileName, tvFileSize, tvComments;
    private ImageView ivPreview;
    private EditText etComment;
    private Button btnPreview, btnDownload, btnReport, btnComment;
    private ArrayList<String> commentList = new ArrayList<>();
    private String fileName, fileUri, userEmail;
    private int fileSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_detail);

        tvFileName = findViewById(R.id.tvFileName);
        tvFileSize = findViewById(R.id.tvFileSize);
        ivPreview = findViewById(R.id.ivPreview);
        etComment = findViewById(R.id.etComment);
        tvComments = findViewById(R.id.tvComments);
        btnPreview = findViewById(R.id.btnPreview);
        btnDownload = findViewById(R.id.btnDownload);
        btnReport = findViewById(R.id.btnReport);
        btnComment = findViewById(R.id.btnSendComment);

        Intent intent = getIntent();
        fileName = intent.getStringExtra("filename");
        fileSize = intent.getIntExtra("filesize", 0);
        fileUri = intent.getStringExtra("fileuri");
        userEmail = intent.getStringExtra("email");

        tvFileName.setText(fileName);
        tvFileSize.setText("Size: " + fileSize + " bytes");

        btnPreview.setOnClickListener(v -> previewFile());
        btnDownload.setOnClickListener(v -> downloadFile());
        btnReport.setOnClickListener(v -> openReportPage());
        btnComment.setOnClickListener(v -> addComment());

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void previewFile() {
        try {
            if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                ivPreview.setVisibility(ImageView.VISIBLE);
                try (InputStream is = getContentResolver().openInputStream(Uri.parse(fileUri))) {
                    ivPreview.setImageBitmap(BitmapFactory.decodeStream(is));
                }
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(fileUri), "*/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Preview File"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadFile() {
        try {
            Uri uri = Uri.parse(fileUri);
            String fileName = tvFileName.getText().toString();
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "*/*");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            Uri externalUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (externalUri == null) {
                Toast.makeText(this, "Cannot create file in Downloads", Toast.LENGTH_LONG).show();
                return;
            }
            OutputStream outputStream = getContentResolver().openOutputStream(externalUri);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();
            Toast.makeText(this, "File saved to Downloads", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error copying file: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void openReportPage() {
        Intent reportIntent = new Intent(FileDetailActivity.this, ReportActivity.class);
        reportIntent.putExtra("email", userEmail);
        reportIntent.putExtra("filename", fileName);
        startActivity(reportIntent);
    }

    private void addComment() {
        String comment = etComment.getText().toString().trim();
        if (!comment.isEmpty()) {
            commentList.add(userEmail + ": " + comment);
            etComment.setText("");
            updateComments();
        }
    }

    private void updateComments() {
        StringBuilder allComments = new StringBuilder();
        for (String c : commentList) {
            allComments.append("- ").append(c).append("\n");
        }
        tvComments.setText(allComments.toString());
    }
}
