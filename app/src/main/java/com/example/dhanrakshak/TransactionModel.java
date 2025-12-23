package com.example.dhanrakshak;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionModel {
    private int id;               // Unique ID
    private String type;          // "Income" or "Expense"
    private String category;      // Consider using Enum for better type safety
    private BigDecimal amount;    // Store amount as BigDecimal to prevent precision issues
    private Date date;            // Store date as Date object for proper handling
    private String notes;
    // Constructor with String amount and date
    public TransactionModel(int id, String type, String category, String amountStr, String dateStr) {
        this.id = id;
        this.type = type;
        this.category = category;

        try {
            this.amount = new BigDecimal(amountStr);
        } catch (NumberFormatException e) {
            this.amount = BigDecimal.ZERO; // fallback
            e.printStackTrace();
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.date = sdf.parse(dateStr);
        } catch (ParseException e) {
            this.date = new Date(); // fallback to current date
            e.printStackTrace();
        }
    }

    public TransactionModel(String type, float amount, String dateStr, String category) {
        this.type = type;
        this.category = category;

        try {
            this.amount = new BigDecimal(String.valueOf(amount));
        } catch (NumberFormatException e) {
            this.amount = BigDecimal.ZERO;
            e.printStackTrace();
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.date = sdf.parse(dateStr);
        } catch (ParseException e) {
            this.date = new Date();
            e.printStackTrace();
        }
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

    public BigDecimal getAmount() {
        return amount;
    }

    public Date getDate() {
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

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // Method to format date as String
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    // Debugging/Logging Helper
    @Override
    public String toString() {
        return "TransactionModel{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", category='" + category + '\'' +
                ", amount=" + amount +
                ", date=" + getFormattedDate() +
                '}';
    }

    public String getNotes() {
        return notes;
    }

}
