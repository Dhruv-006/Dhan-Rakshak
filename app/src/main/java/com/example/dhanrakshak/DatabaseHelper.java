package com.example.dhanrakshak;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "DhanRakshak.db";
    public static final int DATABASE_VERSION = 2;

    // Table and Column Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_INCOME = "income";
    public static final String TABLE_EXPENSE = "expense";

    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users Table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT)");

        // Income Table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_INCOME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_AMOUNT + " TEXT, " +
                COLUMN_DATE + " TEXT)");

        // Expense Table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_EXPENSE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_AMOUNT + " TEXT, " +
                COLUMN_DATE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Optional: Use this if you want to reset tables on upgrade
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE);
        onCreate(db);
    }

    // Register user
    public boolean registerUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values) != -1;
    }

    // Login check
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    // Insert Income
    public boolean insertIncome(String title, String amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date);
        return db.insert(TABLE_INCOME, null, values) != -1;
    }

    // Insert Expense
    public boolean insertExpense(String title, String amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date);
        return db.insert(TABLE_EXPENSE, null, values) != -1;
    }

    // Get Total by Date Range
    public double getTotalAmountWithDateFilter(String tableName, String startDate, String endDate) {
        double total = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        if (startDate != null && endDate != null) {
            cursor = db.rawQuery(
                    "SELECT " + COLUMN_AMOUNT + " FROM " + tableName +
                            " WHERE " + COLUMN_DATE + " BETWEEN ? AND ?",
                    new String[]{startDate, endDate});
        } else {
            cursor = db.rawQuery("SELECT " + COLUMN_AMOUNT + " FROM " + tableName, null);
        }

        if (cursor.moveToFirst()) {
            do {
                try {
                    total += Double.parseDouble(cursor.getString(0));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return total;
    }

    // Get Transactions
    public Cursor getTransactions(String tableName, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (startDate != null && endDate != null) {
            return db.rawQuery("SELECT * FROM " + tableName +
                    " WHERE " + COLUMN_DATE + " BETWEEN ? AND ?", new String[]{startDate, endDate});
        } else {
            return db.rawQuery("SELECT * FROM " + tableName, null);
        }
    }

    // Update Income
    public boolean updateIncome(int id, String title, String amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date);
        return db.update(TABLE_INCOME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    // Update Expense
    public boolean updateExpense(int id, String title, String amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date);
        return db.update(TABLE_EXPENSE, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    // Delete Income
    public boolean deleteIncomeById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_INCOME, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    // Delete Expense
    public boolean deleteExpenseById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EXPENSE, COLUMN_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }
}
