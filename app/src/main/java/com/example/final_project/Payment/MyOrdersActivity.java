package com.example.final_project.Payment;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;
import com.example.final_project.SQL.UserDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
        setContentView(R.layout.orders_activity);

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
                int id = c.getInt(c.getColumnIndexOrThrow("order_id"));
                double amount = c.getDouble(c.getColumnIndexOrThrow("order_price"));
                String status = c.getString(c.getColumnIndexOrThrow("order_name"));
                String date = null;
                int dateIndex = c.getColumnIndex("order_date");
                if (dateIndex != -1) {
                    date = c.getString(dateIndex);
                }
                if (date == null || date.isEmpty()) {
                    date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                }

                list.add(new Order(id, amount, date, status));
            } while (c.moveToNext());
            c.close();
            tvEmpty.setVisibility(android.view.View.GONE);
        } else {
            tvEmpty.setVisibility(android.view.View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    public Order getOrderById(int orderId) {
        for (Order o : list) {
            if (o.getId() == orderId) return o;
        }
        return null;
    }

    public void confirmPayment(int orderId, double userBalance) {
        Order order = getOrderById(orderId);
        if (order == null) return;

        String newStatus;
        if (userBalance >= order.getAmount()) {
            newStatus = "Payment Successful";
        } else {
            newStatus = "Payment Failed";
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        db.updateOrderStatusAndDate(orderId, newStatus, currentDate);

        order.setStatus(newStatus);
        order.setDate(currentDate);
        adapter.notifyDataSetChanged();
    }

}
