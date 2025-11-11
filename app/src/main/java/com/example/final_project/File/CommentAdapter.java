package com.example.final_project.File;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentVH> {

    private List<FileDetailActivity.CommentItem> list;

    public CommentAdapter(List<FileDetailActivity.CommentItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public CommentVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentVH holder, int position) {
        FileDetailActivity.CommentItem c = list.get(position);
        holder.email.setText("📧 " + c.email);
        holder.time.setText("🕒 " + c.time);
        holder.text.setText("💬 " + c.text);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class CommentVH extends RecyclerView.ViewHolder {
        TextView email, time, text;
        public CommentVH(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.rowEmail);
            time = itemView.findViewById(R.id.rowTime);
            text = itemView.findViewById(R.id.rowText);
        }
    }
}
