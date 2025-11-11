package com.example.final_project.Payment;

public class Transaction {
    private String type, date;
    private double amount;

    public Transaction(String type, double amount, String date) {
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
}
