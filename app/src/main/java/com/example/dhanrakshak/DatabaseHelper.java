package com.example.dhanrakshak;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "DhanRakshak.db";
    public static final int DATABASE_VERSION = 4;  // Updated version

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
    public static final String COLUMN_CATEGORY = "category";  // New Column for category
    public static final String COLUMN_PAYMENT_METHOD = "payment_method";  // New Column for payment method
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_TYPE = "type";  // New Column for type

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users Table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT)");

        // Income Table (Updated to include category, notes, and type)
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_INCOME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_AMOUNT + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_NOTES + " TEXT, " +
                COLUMN_TYPE + " TEXT)");

        // Expense Table (Updated to include category, payment method, and notes)
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_EXPENSE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_AMOUNT + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_PAYMENT_METHOD + " TEXT, " +
                COLUMN_NOTES + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            // Handle addition of new columns only if they do not exist
            if (!columnExists(db, TABLE_INCOME, COLUMN_CATEGORY)) {
                db.execSQL("ALTER TABLE " + TABLE_INCOME + " ADD COLUMN " + COLUMN_CATEGORY + " TEXT");
            }
            if (!columnExists(db, TABLE_INCOME, COLUMN_NOTES)) {
                db.execSQL("ALTER TABLE " + TABLE_INCOME + " ADD COLUMN " + COLUMN_NOTES + " TEXT");
            }
            if (!columnExists(db, TABLE_INCOME, COLUMN_TYPE)) {
                db.execSQL("ALTER TABLE " + TABLE_INCOME + " ADD COLUMN " + COLUMN_TYPE + " TEXT");
            }

            if (!columnExists(db, TABLE_EXPENSE, COLUMN_CATEGORY)) {
                db.execSQL("ALTER TABLE " + TABLE_EXPENSE + " ADD COLUMN " + COLUMN_CATEGORY + " TEXT");
            }
            if (!columnExists(db, TABLE_EXPENSE, COLUMN_PAYMENT_METHOD)) {
                db.execSQL("ALTER TABLE " + TABLE_EXPENSE + " ADD COLUMN " + COLUMN_PAYMENT_METHOD + " TEXT");
            }
            if (!columnExists(db, TABLE_EXPENSE, COLUMN_NOTES)) {
                db.execSQL("ALTER TABLE " + TABLE_EXPENSE + " ADD COLUMN " + COLUMN_NOTES + " TEXT");
            }
        }
    }

    // Helper method to check if a column exists in a table
    @SuppressLint("Range")
    private boolean columnExists(SQLiteDatabase db, String tableName, String columnName) {
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex("name")).equals(columnName)) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
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

    // Insert Income (Updated to include category, notes, and type)
    public boolean insertIncome(String title, String amount, String date, String category, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_NOTES, notes);
        values.put(COLUMN_TYPE, "Income");

        long result = db.insert(TABLE_INCOME, null, values);
        return result != -1;
    }

    // Insert Expense (Updated to handle category, payment method, and notes)
    public boolean insertExpense(String title, String amount, String date, String category, String paymentMethod, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_PAYMENT_METHOD, paymentMethod);
        values.put(COLUMN_NOTES, notes);
        return db.insert(TABLE_EXPENSE, null, values) != -1;
    }

    // Get Total by Date Range (Daily, Weekly, Monthly)
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

    // Get Transactions by Date Range (Daily, Weekly, Monthly)
    public Cursor getTransactions(String tableName, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (startDate != null && endDate != null) {
            return db.rawQuery("SELECT * FROM " + tableName +
                    " WHERE " + COLUMN_DATE + " BETWEEN ? AND ?", new String[]{startDate, endDate});
        } else {
            return db.rawQuery("SELECT * FROM " + tableName, null);
        }
    }

    // Helper method to get the start and end date for daily, weekly, or monthly views
    public String[] getDateRange(String filterType) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String[] dateRange = new String[2];

        switch (filterType) {
            case "daily":
                dateRange[0] = sdf.format(calendar.getTime());
                dateRange[1] = dateRange[0];
                break;

            case "weekly":
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                dateRange[0] = sdf.format(calendar.getTime());
                calendar.add(Calendar.DATE, 6);
                dateRange[1] = sdf.format(calendar.getTime());
                break;

            case "monthly":
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                dateRange[0] = sdf.format(calendar.getTime());
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 0);
                dateRange[1] = sdf.format(calendar.getTime());
                break;
        }

        return dateRange;
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
    public boolean updateExpense(int id, String title, String amount, String date, String category, String paymentMethod, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_PAYMENT_METHOD, paymentMethod);
        values.put(COLUMN_NOTES, notes);
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
