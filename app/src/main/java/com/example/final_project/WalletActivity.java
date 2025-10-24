package com.example.final_project;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WalletActivity extends AppCompatActivity {

    private TextView tvBalance, tvEmpty;
    private RecyclerView rvTransactions;
    private Button btnTopUp;
    private UserDatabase db;
    private TransactionAdapter adapter;
    private String userEmail;
    private ArrayList<Transaction> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        tvBalance = findViewById(R.id.tv_balance_amount);
        tvEmpty = findViewById(R.id.tv_transactions_empty);
        rvTransactions = findViewById(R.id.rv_transactions);
        btnTopUp = findViewById(R.id.btn_top_up);

        db = new UserDatabase(this);

        userEmail = getIntent().getStringExtra("email");
        if (userEmail != null && !userEmail.isEmpty()) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            prefs.edit().putString("email", userEmail).apply();
        } else {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            userEmail = prefs.getString("email", null);
        }

        if (userEmail == null || userEmail.isEmpty()) {
            tvBalance.setText("Error: No user email found");
            tvEmpty.setText("Please log in again");
            tvEmpty.setVisibility(android.view.View.VISIBLE);
            return;
        }

        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(list);
        rvTransactions.setAdapter(adapter);

        loadData();

        btnTopUp.setOnClickListener(v -> {
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            db.insertTransaction(userEmail, "Top Up", 100000, now);
            loadData();
        });
    }

    private void loadData() {
        list.clear();
        Cursor c = db.getTransactionsByEmail(userEmail);
        if (c != null && c.moveToFirst()) {
            do {
                String type = c.getString(c.getColumnIndexOrThrow("type"));
                double amount = c.getDouble(c.getColumnIndexOrThrow("amount"));
                String date = c.getString(c.getColumnIndexOrThrow("date"));
                list.add(new Transaction(type, amount, date));
            } while (c.moveToNext());
            c.close();
            tvEmpty.setVisibility(android.view.View.GONE);
        } else {
            tvEmpty.setVisibility(android.view.View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
        double balance = db.getTotalBalance(userEmail);
        tvBalance.setText("₫" + String.format(Locale.getDefault(), "%.0f", balance));
    }
}
