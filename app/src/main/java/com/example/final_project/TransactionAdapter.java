package com.example.final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private ArrayList<Transaction> list;

    public TransactionAdapter(ArrayList<Transaction> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        Transaction t = list.get(position);
        h.tvTitle.setText(t.getType());
        h.tvSub.setText(t.getDate());
        String prefix = t.getAmount() >= 0 ? "+" : "-";
        h.tvAmount.setText(prefix + "₫" + String.format(Locale.getDefault(), "%.0f", Math.abs(t.getAmount())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSub, tvAmount;
        public ViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tv_tx_title);
            tvSub = v.findViewById(R.id.tv_tx_sub);
            tvAmount = v.findViewById(R.id.tv_tx_amount);
        }
    }
}
