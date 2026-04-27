package com.example.smartexpensetracker.models;

public class AppNotification {
    private int id;
    private String type; // success, warning, info
    private String message;
    private String date;
    private int isRead;

    public AppNotification(int id, String type, String message, String date, int isRead) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.date = date;
        this.isRead = isRead;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public String getDate() { return date; }
    public int getIsRead() { return isRead; }
}
