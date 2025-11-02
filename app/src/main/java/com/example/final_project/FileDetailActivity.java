package com.example.final_project;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import com.google.firebase.database.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FileDetailActivity extends AppCompatActivity {

    private TextView tvFileName, tvFileSize, tvComments;
    private ImageView ivPreview;
    private EditText etComment;
    private Button btnPreview, btnDownload, btnReport, btnSendComment, btnEditFile, btnDeleteFile;
    private View blurOverlay;

    private int fileId;
    private String fileName, fileUri, userEmail, status;
    private int fileSize;
    private boolean isPremium = false;

    private DatabaseReference commentRef;

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
        btnSendComment = findViewById(R.id.btnSendComment);
        btnEditFile = findViewById(R.id.btnEditFile);
        btnDeleteFile = findViewById(R.id.btnDeleteFile);
        blurOverlay = findViewById(R.id.blurOverlay);

        Intent intent = getIntent();
        fileId = intent.getIntExtra("fileId", -1);
        fileName = intent.getStringExtra("filename");
        fileSize = intent.getIntExtra("filesize", 0);
        fileUri = intent.getStringExtra("fileuri");
        userEmail = intent.getStringExtra("email");
        status = intent.getStringExtra("status");

        UserDatabase db = new UserDatabase(this);
        try (Cursor c = db.getUserByEmail(userEmail)) {
            if (c != null && c.moveToFirst()) {
                int idx = c.getColumnIndex("isPremium");
                if (idx >= 0) isPremium = c.getInt(idx) == 1;
            }
        } catch (Exception e) { e.printStackTrace(); }
        db.close();

        tvFileName.setText(fileName);
        tvFileSize.setText("Size: " + fileSize + " bytes");

        if (fileUri != null && !fileUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(fileUri);
                final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(uri, takeFlags);
            } catch (Exception e) { e.printStackTrace(); }
        }

        updateUIByStatus();
        previewFile();

        btnPreview.setOnClickListener(v -> previewFile());
        btnDownload.setOnClickListener(v -> downloadFile());
        btnReport.setOnClickListener(v -> openReportPage());
        btnSendComment.setOnClickListener(v -> addComment());
        btnEditFile.setOnClickListener(v -> selectNewFile());
        btnDeleteFile.setOnClickListener(v -> deleteFile());

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }

        commentRef = FirebaseDatabase.getInstance().getReference("comments").child(String.valueOf(fileId));
        loadCommentsFromFirebase();
    }

    private void updateUIByStatus() {
        boolean isPending = status != null && status.equalsIgnoreCase("pending");
        btnPreview.setVisibility(Button.VISIBLE);
        btnEditFile.setVisibility(isPending ? Button.VISIBLE : Button.GONE);
        btnDeleteFile.setVisibility(isPending ? Button.VISIBLE : Button.GONE);
        btnDownload.setVisibility(isPending ? Button.GONE : Button.VISIBLE);
        btnSendComment.setVisibility(isPending ? Button.GONE : Button.VISIBLE);
        etComment.setVisibility(isPending ? EditText.GONE : EditText.VISIBLE);
        tvComments.setVisibility(isPending ? TextView.GONE : TextView.VISIBLE);
    }

    private void previewFile() {
        if (fileUri == null || fileUri.isEmpty()) return;
        ivPreview.setVisibility(ImageView.GONE);
        blurOverlay.setVisibility(View.GONE);
        tvComments.setVisibility(TextView.GONE);
        String lowerName = fileName.trim().toLowerCase();
        Uri uri = Uri.parse(fileUri);
        try {
            if (lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
                ivPreview.setVisibility(ImageView.VISIBLE);
                try (InputStream is = getContentResolver().openInputStream(uri)) {
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    if (!isPremium) {
                        int cutHeight = bmp.getHeight() / 2;
                        Bitmap topHalf = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), cutHeight);
                        ivPreview.setImageBitmap(topHalf);
                        blurOverlay.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Upgrade to Premium to view full image", Toast.LENGTH_SHORT).show();
                    } else {
                        ivPreview.setImageBitmap(bmp);
                        blurOverlay.setVisibility(View.GONE);
                    }
                }
            } else if (lowerName.endsWith(".pdf")) {
                tvComments.setVisibility(TextView.VISIBLE);
                try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r")) {
                    if (pfd != null) {
                        PdfRenderer renderer = new PdfRenderer(pfd);
                        if (renderer.getPageCount() > 0) {
                            int pageLimit = isPremium ? renderer.getPageCount() : 1;
                            PdfRenderer.Page page = renderer.openPage(0);
                            Bitmap bmp = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                            page.close();
                            renderer.close();
                            ivPreview.setVisibility(ImageView.VISIBLE);
                            ivPreview.setImageBitmap(bmp);
                            if (!isPremium)
                                Toast.makeText(this, "Upgrade to Premium to view full PDF", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else if (lowerName.endsWith(".txt")) {
                tvComments.setVisibility(TextView.VISIBLE);
                StringBuilder sb = new StringBuilder();
                try (InputStream is = getContentResolver().openInputStream(uri)) {
                    int ch;
                    while ((ch = is.read()) != -1) sb.append((char) ch);
                }
                String text = sb.toString();
                if (!isPremium && text.length() > 200) {
                    text = text.substring(0, 200) + "\n\n[Upgrade to Premium to view the rest]";
                }
                tvComments.setText(text);
            } else {
                Toast.makeText(this, "Preview not supported for this file type", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Cannot preview file", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectNewFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri newUri = data.getData();
            if (newUri != null) {
                try {
                    getContentResolver().takePersistableUriPermission(
                            newUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    );
                } catch (Exception e) { e.printStackTrace(); }
                fileUri = newUri.toString();
                fileName = getFileName(newUri);
                try (InputStream is = getContentResolver().openInputStream(newUri)) {
                    if (is != null) fileSize = is.available();
                } catch (Exception e) { e.printStackTrace(); }
                tvFileName.setText(fileName);
                tvFileSize.setText("Size: " + fileSize + " bytes");
                UserDatabase db = new UserDatabase(this);
                db.updateFile(fileId, fileName, fileUri, fileSize, status);
                db.close();
                previewFile();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = uri.getLastPathSegment();
        return result != null ? result : "Unnamed File";
    }

    private void downloadFile() {
        try {
            Uri uri = Uri.parse(fileUri);
            InputStream is = getContentResolver().openInputStream(uri);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "*/*");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            Uri saveUri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (saveUri == null) return;
            try (OutputStream os = getContentResolver().openOutputStream(saveUri)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = is.read(buffer)) != -1) os.write(buffer, 0, read);
            }
            is.close();
            Toast.makeText(this, "File saved to Downloads", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show();
        }
    }

    private void openReportPage() {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("email", userEmail);
        intent.putExtra("filename", fileName);
        startActivity(intent);
    }

    private void addComment() {
        String commentText = etComment.getText().toString().trim();
        if (commentText.isEmpty()) return;

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String commentId = commentRef.push().getKey();

        if (commentId != null) {
            CommentItem comment = new CommentItem(userEmail, commentText, timestamp);
            commentRef.child(commentId).setValue(comment);
        }
        etComment.setText("");
    }

    private void loadCommentsFromFirebase() {
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder sb = new StringBuilder();
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    CommentItem comment = commentSnapshot.getValue(CommentItem.class);
                    if (comment != null) {
                        sb.append("📧 ").append(comment.email)
                                .append("\n🕒 ").append(comment.time)
                                .append("\n💬 ").append(comment.text)
                                .append("\n\n");
                    }
                }
                tvComments.setText(sb.toString());
                tvComments.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FileDetailActivity.this, "Failed to load comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteFile() {
        if (fileId == -1) return;
        try {
            Uri uri = Uri.parse(fileUri);
            if ("content".equals(uri.getScheme())) {
                getContentResolver().delete(uri, null, null);
            } else if ("file".equals(uri.getScheme())) {
                java.io.File file = new java.io.File(uri.getPath());
                if (file.exists()) file.delete();
            }
        } catch (Exception e) { e.printStackTrace(); }
        UserDatabase db = new UserDatabase(this);
        db.deleteFile(fileId);
        db.close();
        finish();
    }

    public static class CommentItem {
        public String email;
        public String text;
        public String time;

        public CommentItem() {}

        public CommentItem(String email, String text, String time) {
            this.email = email;
            this.text = text;
            this.time = time;
        }
    }
}
