package com.example.final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private ArrayList<Order> list;

    public OrderAdapter(ArrayList<Order> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        Order o = list.get(position);
        h.tvOrderId.setText("Order #" + o.getId());
        h.tvAmount.setText("₫" + String.format(Locale.getDefault(), "%.0f", o.getAmount()));
        h.tvDate.setText(o.getDate());
        h.tvStatus.setText("Status: " + o.getStatus());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvAmount, tvDate, tvStatus;
        public ViewHolder(View v) {
            super(v);
            tvOrderId = v.findViewById(R.id.tv_order_id);
            tvAmount = v.findViewById(R.id.tv_order_amount);
            tvDate = v.findViewById(R.id.tv_order_date);
            tvStatus = v.findViewById(R.id.tv_order_status);
        }
    }
}
