package com.example.final_project;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MyOrdersActivity extends AppCompatActivity {

    private TextView tvEmpty;
    private RecyclerView rvOrders;
    private UserDatabase db;
    private OrderAdapter adapter;
    private String userEmail;
    private ArrayList<Order> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        tvEmpty = findViewById(R.id.tv_orders_empty);
        rvOrders = findViewById(R.id.rv_orders);
        db = new UserDatabase(this);
        userEmail = getIntent().getStringExtra("email");

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(list);
        rvOrders.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        list.clear();
        Cursor c = db.getOrdersByEmail(userEmail);
        if (c != null && c.moveToFirst()) {
            do {
                double amount = c.getDouble(c.getColumnIndexOrThrow("amount"));
                String date = c.getString(c.getColumnIndexOrThrow("date"));
                String status = c.getString(c.getColumnIndexOrThrow("status"));
                int id = c.getInt(c.getColumnIndexOrThrow("order_id"));
                list.add(new Order(id, amount, date, status));
            } while (c.moveToNext());
            c.close();
            tvEmpty.setVisibility(android.view.View.GONE);
        } else {
            tvEmpty.setVisibility(android.view.View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }
}
