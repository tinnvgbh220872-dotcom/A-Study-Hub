package com.example.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private TextView tvOrdersHeader;
    private RecyclerView recyclerOrders;
    private BottomNavigationView bottomNavigationView;
    private OrderAdapter orderAdapter;
    private List<String> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        tvOrdersHeader = findViewById(R.id.tvOrdersHeader);
        recyclerOrders = findViewById(R.id.recyclerOrders);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Gắn dữ liệu demo cho RecyclerView
        orderList = new ArrayList<>();
        orderList.add("Premium Document Pack - 01/10/2025");
        orderList.add("Advanced Study Guide - 15/09/2025");
        orderList.add("AI Research Bundle - 20/08/2025");

        orderAdapter = new OrderAdapter(orderList);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrders.setAdapter(orderAdapter);

        // chọn mặc định Orders tab
        bottomNavigationView.setSelectedItemId(R.id.nav_orders);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    Intent intent = new Intent(OrderActivity.this, MainScreenActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_orders) {
                    return true; // đang ở Orders thì không làm gì
                } else if (id == R.id.nav_profile) {
                    Intent intent = new Intent(OrderActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_logout) {
                    Intent intent = new Intent(OrderActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
}
