package com.example.final_project.Payment;

public class Order {
    private int id;
    private double amount;
    private String date;
    private String status;

    public Order(int id, double amount, String date, String status) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.status = status;
    }

    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getStatus() { return status; }

    public void setDate(String date) { this.date = date; }
    public void setStatus(String status) { this.status = status; }
}

