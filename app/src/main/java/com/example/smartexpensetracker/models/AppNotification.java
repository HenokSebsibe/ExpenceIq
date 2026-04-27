package com.example.smartexpensetracker.models;

public class AppNotification {
    private int id;
    private String type;
    private String message;
    private String date;

    public AppNotification(int id, String type, String message, String date) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.date = date;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public String getDate() { return date; }
}
