package com.example.dhanrakshak;

public class TransactionModel {
    private int id;              // Unique ID
    private String type;         // "Income" or "Expense"
    private String category;
    private String amount;
    private String date;

    // Constructor
    public TransactionModel(int id, String type, String category, String amount, String date) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    // Setters (Optional)
    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Debugging/Logging Helper
    @Override
    public String toString() {
        return "TransactionModel{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", category='" + category + '\'' +
                ", amount='" + amount + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
