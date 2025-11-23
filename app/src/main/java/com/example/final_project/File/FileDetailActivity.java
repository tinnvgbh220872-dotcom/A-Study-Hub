package com.example.final_project.File;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.MainScreen.MainScreenActivity;
import com.example.final_project.SQL.UserDatabase;
import com.example.final_project.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FileDetailActivity extends AppCompatActivity {

    private TextView tvFileName, tvFileSize, tvFileContent;
    private RecyclerView rvComments;
    private ImageView ivPreview;
    private EditText etComment;
    private Button btnPreview, btnDownload, btnSendComment, btnEditFile, btnDeleteFile;
    private View blurOverlay;

    private CommentAdapter adapter;
    private ArrayList<CommentItem> commentList;

    private int fileId;
    private String fileName, fileUri, userEmail, status;
    private int fileSize;
    private boolean isPremium = false;
    private static final String NOTIF_CHANNEL_ID = "notif_channel";


    private DatabaseReference commentRef;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_detail);


        tvFileName = findViewById(R.id.tvFileName);
        tvFileSize = findViewById(R.id.tvFileSize);
        tvFileContent = findViewById(R.id.tvFileContent);
        ivPreview = findViewById(R.id.ivPreview);
        etComment = findViewById(R.id.etComment);
        rvComments = findViewById(R.id.commentRecycler);
        blurOverlay = findViewById(R.id.blurOverlay);

        btnPreview = findViewById(R.id.btnPreview);
        btnDownload = findViewById(R.id.btnDownload);
        btnSendComment = findViewById(R.id.btnSendComment);
        btnEditFile = findViewById(R.id.btnEditFile);
        btnDeleteFile = findViewById(R.id.btnDeleteFile);

        UserDatabase db = new UserDatabase(this);
        String email = getLoggedInEmail();
        if (email != null) {
            Cursor c = db.getUserByEmail(email);
            if (c != null && c.moveToFirst()) {
                userEmail = c.getString(c.getColumnIndex("email"));
                isPremium = c.getInt(c.getColumnIndex("isPremium")) == 1;
            }
            if (c != null) c.close();
        }
        db.close();

        Intent intent = getIntent();
        fileId = intent.getIntExtra("fileId", -1);
        fileName = intent.getStringExtra("filename");
        fileSize = intent.getIntExtra("filesize", 0);
        fileUri = intent.getStringExtra("fileuri");
        status = intent.getStringExtra("status");

        tvFileName.setText(fileName);
        tvFileSize.setText("Size: " + fileSize + " bytes");

        if (fileUri != null && !fileUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(fileUri);
                final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(uri, takeFlags);
            } catch (Exception ignored) {}
        }

        commentList = new ArrayList<>();
        adapter = new CommentAdapter(commentList);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(adapter);

        commentRef = FirebaseDatabase.getInstance().getReference("comments").child(String.valueOf(fileId));
        loadCommentsFromFirebase();

        btnPreview.setOnClickListener(v -> previewFile());
        btnDownload.setOnClickListener(v -> downloadFile());
        btnSendComment.setOnClickListener(v -> addComment());
        btnEditFile.setOnClickListener(v -> selectNewFile());
        btnDeleteFile.setOnClickListener(v -> deleteFile());

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }

        updateUIByStatus();
        previewFile();

        if (userEmail != null) listenForNotifications(userEmail);
    }


    private String getLoggedInEmail() {
        SharedPreferences sp = getSharedPreferences("app_prefs", MODE_PRIVATE);
        return sp.getString("user_email", null);
    }

    private void updateUIByStatus() {
        boolean isPending = status != null && status.equalsIgnoreCase("pending");
        btnPreview.setVisibility(Button.VISIBLE);
        btnEditFile.setVisibility(isPending ? Button.VISIBLE : Button.GONE);
        btnDeleteFile.setVisibility(isPending ? Button.VISIBLE : Button.GONE);
        btnDownload.setVisibility(isPending ? Button.GONE : Button.VISIBLE);
        btnSendComment.setVisibility(isPending ? Button.GONE : Button.VISIBLE);
        etComment.setVisibility(isPending ? EditText.GONE : EditText.VISIBLE);
        rvComments.setVisibility(isPending ? View.GONE : View.VISIBLE);
    }

    private void previewFile() {
        if (fileUri == null || fileUri.isEmpty()) return;

        ivPreview.setVisibility(View.GONE);
        blurOverlay.setVisibility(View.GONE);
        tvFileContent.setVisibility(View.GONE);

        String lowerName = fileName.trim().toLowerCase();
        Uri uri = Uri.parse(fileUri);

        try {
            if (lowerName.endsWith(".png") || lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
                ivPreview.setVisibility(View.VISIBLE);
                try (InputStream is = getContentResolver().openInputStream(uri)) {
                    if (is != null) {
                        Bitmap bmp = BitmapFactory.decodeStream(is);
                        ivPreview.setImageBitmap(bmp);
                    }
                }

            } else if (lowerName.endsWith(".pdf")) {
                ivPreview.setVisibility(View.VISIBLE);
                try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "r")) {
                    if (pfd != null) {
                        PdfRenderer renderer = new PdfRenderer(pfd);
                        if (renderer.getPageCount() > 0) {
                            PdfRenderer.Page page = renderer.openPage(0);
                            Bitmap bmp = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                            page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                            if (!isPremium) {
                                int halfHeight = bmp.getHeight() / 2;
                                Bitmap topHalf = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), halfHeight);
                                ivPreview.setImageBitmap(topHalf);
                                Toast.makeText(this, "Upgrade to Premium to view full PDF", Toast.LENGTH_SHORT).show();
                            } else {
                                ivPreview.setImageBitmap(bmp);
                            }

                            page.close();
                        }
                        renderer.close();
                    }
                }

            } else if (lowerName.endsWith(".txt")) {
                tvFileContent.setVisibility(View.VISIBLE);
                try (InputStream is = getContentResolver().openInputStream(uri)) {
                    if (is != null) {
                        StringBuilder sb = new StringBuilder();
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = is.read(buffer)) != -1) {
                            sb.append(new String(buffer, 0, len));
                            if (!isPremium && sb.length() > 1000) break;
                        }

                        String text = sb.toString();
                        if (!isPremium && text.length() > 500) {
                            text = text.substring(0, 500) + "\n\n[Upgrade to Premium to view the rest]";
                        }
                        tvFileContent.setText(text);
                    }
                }

            } else {
                Toast.makeText(this, "Preview not supported for this file type", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to preview file", Toast.LENGTH_SHORT).show();
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
                } catch (Exception ignored) {}

                String newFileUri = newUri.toString();
                String newFileName = getFileName(newUri);

                final long[] newFileSize = {0};
                try (InputStream is = getContentResolver().openInputStream(newUri)) {
                    if (is != null) newFileSize[0] = is.available();
                } catch (Exception ignored) {}

                tvFileName.setText(newFileName);
                tvFileSize.setText("Size: " + newFileSize[0] + " bytes");

                String userEmail = getIntent().getStringExtra("email");
                if (userEmail == null || userEmail.isEmpty()) {
                    Toast.makeText(this, "User email not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                String firebaseKey = getIntent().getStringExtra("firebaseKey");
                if (firebaseKey == null || firebaseKey.isEmpty()) {
                    Toast.makeText(this, "Firebase key not found. Cannot update file.", Toast.LENGTH_LONG).show();
                    return;
                }

                new AlertDialog.Builder(this)
                        .setTitle("Confirm Update")
                        .setMessage("Are you sure you want to update this file?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            UserDatabase db = new UserDatabase(this);
                            db.updateFile(fileId, newFileName, newFileUri, (int)newFileSize[0], status); // Ép kiểu int
                            db.close();

                            DatabaseReference fileRef = FirebaseDatabase.getInstance()
                                    .getReference("uploads")
                                    .child(firebaseKey);

                            Map<String, Object> updateMap = new HashMap<>();
                            updateMap.put("filename", newFileName);
                            updateMap.put("fileUri", newFileUri);
                            updateMap.put("filesize", newFileSize[0]);
                            updateMap.put("status", status);
                            updateMap.put("publishedDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

                            fileRef.updateChildren(updateMap)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(FileDetailActivity.this, "File updated", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(FileDetailActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show());

                            Intent intentMain = new Intent(FileDetailActivity.this, MainScreenActivity.class);
                            intentMain.putExtra("email", userEmail);
                            intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentMain);
                            finish();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

                previewFile();
            }
        }
    }





    private String getFileName(Uri uri) {
        String result = uri.getLastPathSegment();
        return result != null ? result : "Unnamed File";
    }

    private void downloadFile() {
        UserDatabase db = new UserDatabase(this);
        boolean isPremium = db.isPremiumUser(userEmail);
        db.close();

        if (!isPremium) {
            Toast.makeText(this, "Only Premium users can download files", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Uri uri = Uri.parse(fileUri);
            InputStream is = getContentResolver().openInputStream(uri);
            ContentValues values = new ContentValues();
            values.put(android.provider.MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(android.provider.MediaStore.Downloads.MIME_TYPE, "*/*");
            values.put(android.provider.MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri saveUri = getContentResolver().insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (saveUri == null) return;

            OutputStream os = getContentResolver().openOutputStream(saveUri);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) != -1) os.write(buffer, 0, read);
            is.close();
            os.close();

            Toast.makeText(this, "Saved to Downloads", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void addComment() {
        String text = etComment.getText().toString().trim();
        if (text.isEmpty()) return;

        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        String commentId = commentRef.push().getKey();
        if (commentId != null) {
            CommentItem comment = new CommentItem(userEmail, text, timestamp);
            commentRef.child(commentId).setValue(comment).addOnSuccessListener(aVoid -> etComment.setText(""));
        }
    }

    private void loadCommentsFromFirebase() {
        commentRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    CommentItem comment = ds.getValue(CommentItem.class);
                    if (comment != null) commentList.add(comment);
                }
                adapter.notifyDataSetChanged();
                if (!commentList.isEmpty()) rvComments.scrollToPosition(commentList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void deleteFile() {
        if (fileId == -1 || fileUri == null || fileUri.isEmpty()) return;

        new AlertDialog.Builder(this)
                .setTitle("Delete File")
                .setMessage("Are you sure you want to delete this file?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    DatabaseReference filesRef = FirebaseDatabase.getInstance().getReference("uploads");
                    filesRef.orderByChild("fileUri").equalTo(fileUri)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot child : snapshot.getChildren()) {
                                            String key = child.getKey();
                                            if (key != null) {
                                                filesRef.child(key).removeValue()
                                                        .addOnFailureListener(e ->
                                                                Log.e("DeleteFile", "Failed to delete file: " + e.getMessage())
                                                        );
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("DeleteFile", "DB delete cancelled: " + error.getMessage());
                                }
                            });

                    UserDatabase db = new UserDatabase(this);
                    db.deleteFile(fileId);
                    db.close();

                    Toast.makeText(this, "File deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainScreenActivity.class);
                    intent.putExtra("email", userEmail);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }




    private void listenForNotifications(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
                return;
            }
        }

        String emailKey = userEmail.replace(".", "_");
        DatabaseReference notifRef = FirebaseDatabase.getInstance()
                .getReference("notifications")
                .child(emailKey);

        notifRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                NotificationItem notif = snapshot.getValue(NotificationItem.class);
                if (notif != null && notif.title != null && notif.message != null) {
                    runOnUiThread(() -> showNotification(notif.title, notif.message));

                    snapshot.getRef().removeValue();
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotifDebug", "Failed to load notifications: " + error.getMessage());
            }
        });
    }

    private void showNotification(String title, String message) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel existingChannel = manager.getNotificationChannel(NOTIF_CHANNEL_ID);
            if (existingChannel == null) {
                NotificationChannel channel = new NotificationChannel(
                        NOTIF_CHANNEL_ID,
                        "App Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Notifications from app");
                channel.enableLights(true);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{0, 500, 250, 500});
                manager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
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

    public static class NotificationItem {
        public String notifID;
        public String title;
        public String message;

        public NotificationItem() {}
        public NotificationItem(String notifID, String title, String message) {
            this.notifID = notifID;
            this.title = title;
            this.message = message;
        }
    }
}
