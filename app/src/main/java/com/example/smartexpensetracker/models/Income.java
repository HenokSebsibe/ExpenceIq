package com.example.smartexpensetracker.models;

public class Income {
    private int id;
    private String source;
    private double amount;
    private String date;
    private String note;

    public Income(int id, String source, double amount, String date, String note) {
        this.id = id;
        this.source = source;
        this.amount = amount;
        this.date = date;
        this.note = note;
    }

    public int getId() { return id; }
    public String getSource() { return source; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getNote() { return note; }

    public void setSource(String source) { this.source = source; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDate(String date) { this.date = date; }
    public void setNote(String note) { this.note = note; }
}
