package com.example.dhanrakshak;

public class GoalModel {
    private int id;
    private String name;
    private double targetAmount;
    private double savedAmount;
    private String createdDate;

    public GoalModel(int id, String name, double targetAmount, double savedAmount, String createdDate) {
        this.id = id;
        this.name = name;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount;
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public double getSavedAmount() {
        return savedAmount;
    }

    public double getRemainingAmount() {
        return targetAmount - savedAmount;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public int getProgressPercent() {
        if (targetAmount <= 0)
            return 0;
        return (int) ((savedAmount / targetAmount) * 100);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void setSavedAmount(double savedAmount) {
        this.savedAmount = savedAmount;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
