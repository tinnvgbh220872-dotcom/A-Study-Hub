package com.example.final_project.Payment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.SQL.UserDatabase;
import com.example.final_project.R;

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
    private static final int TOPUP_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet_activity);

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
            Intent intent = new Intent(WalletActivity.this, TopupActivity.class);
            intent.putExtra("userEmail", userEmail);
            startActivityForResult(intent, TOPUP_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TOPUP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            double topupAmount = data.getDoubleExtra("topupAmount", 0);
            if (topupAmount > 0) {
                String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
                db.insertTransaction(userEmail, "Top Up", topupAmount, now);
                loadData();
            }
        }
    }

    private void loadData() {
        list.clear();
        Cursor c = db.getTransactionsByEmail(userEmail);
        double balance = 0;
        if (c != null && c.moveToFirst()) {
            do {
                String type = c.getString(c.getColumnIndexOrThrow("type"));
                double amount = c.getDouble(c.getColumnIndexOrThrow("amount"));
                String date = c.getString(c.getColumnIndexOrThrow("date"));
                list.add(new Transaction(type, Math.abs(amount), date));
                balance += amount;
            } while (c.moveToNext());
            c.close();
            tvEmpty.setVisibility(android.view.View.GONE);
        } else {
            tvEmpty.setVisibility(android.view.View.VISIBLE);
        }
        adapter.notifyDataSetChanged();

        if (balance == (long) balance) {
            tvBalance.setText((long) balance + "$");
        } else {
            tvBalance.setText(balance + "$");
        }
    }
}
