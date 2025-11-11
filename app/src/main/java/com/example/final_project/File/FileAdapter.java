package com.example.final_project.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;

import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private Context context;
    private List<FileItem> fileList;
    private String currentUserEmail;

    public FileAdapter(Context context, List<FileItem> fileList, String currentUserEmail) {
        this.context = context;
        this.fileList = fileList;
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.file_item, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        FileItem file = fileList.get(position);
        holder.tvName.setText(file.getName());
        holder.tvSize.setText(file.getSize() + " bytes");
        holder.tvEmail.setText("Uploaded by: " + file.getEmail());
        holder.tvDate.setText("Published: " + file.getPublishedDate());

        String statusText = file.getStatus().equalsIgnoreCase("pending") ? "Pending" : "Global";
        holder.tvStatus.setText(statusText);

        Uri uri = Uri.parse(file.getUri());
        String mimeType = context.getContentResolver().getType(uri);
        if (mimeType != null && mimeType.startsWith("image/")) {
            holder.ivFileIcon.setImageURI(uri);
        } else {
            holder.ivFileIcon.setImageResource(R.drawable.ic_file);
        }

        // Ẩn file pending của người khác
        if (file.getStatus().equalsIgnoreCase("pending") &&
                !file.getEmail().equalsIgnoreCase(currentUserEmail)) {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            return;
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FileDetailActivity.class);
            intent.putExtra("fileId", file.getFileId());
            intent.putExtra("filename", file.getName());
            intent.putExtra("filesize", file.getSize());
            intent.putExtra("fileuri", file.getUri());
            intent.putExtra("email", file.getEmail());
            intent.putExtra("publishedDate", file.getPublishedDate());
            intent.putExtra("status", file.getStatus());
            intent.putExtra("firebaseKey", file.getFirebaseKey());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFileIcon;
        TextView tvName, tvSize, tvEmail, tvDate, tvStatus;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFileIcon = itemView.findViewById(R.id.ivFileIcon);
            tvName = itemView.findViewById(R.id.tvName);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
