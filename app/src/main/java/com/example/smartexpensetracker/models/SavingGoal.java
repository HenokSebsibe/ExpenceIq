package com.example.smartexpensetracker.models;

public class SavingGoal {
    private int id;
    private String name;
    private double targetAmount;
    private double savedAmount;
    private String deadline;
    private String note;
    private int status; // 0 for active, 1 for completed

    public SavingGoal(int id, String name, double targetAmount, double savedAmount, String deadline, String note, int status) {
        this.id = id;
        this.name = name;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.deadline = deadline;
        this.note = note;
        this.status = status;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getTargetAmount() { return targetAmount; }
    public double getSavedAmount() { return savedAmount; }
    public String getDeadline() { return deadline; }
    public String getNote() { return note; }
    public int getStatus() { return status; }
    
    public int getProgress() {
        if (targetAmount <= 0) return 0;
        int progress = (int) ((savedAmount / targetAmount) * 100);
        return Math.min(progress, 100);
    }
}
