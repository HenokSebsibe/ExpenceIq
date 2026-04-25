package com.example.smartexpensetracker.models;

public class Transaction {
    private int id;
    private String type;
    private double amount;
    private String date;

    public Transaction(int id, String type, double amount, String date) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
}
