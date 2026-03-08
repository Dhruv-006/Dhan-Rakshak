
package com.example.dhanrakshak;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_INCOME = "income";
    public static final String TABLE_EXPENSE = "expense";
    public static final String TABLE_GOALS = "goals";

    public static final String DATABASE_NAME = "DhanRakshak.db";
    public static final int DATABASE_VERSION = 6;

    // Table and Column Names
    public static final String TABLE_USERS = "users";

    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PAYMENT_METHOD = "payment_method";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Goals columns
    public static final String COLUMN_GOAL_NAME = "name";
    public static final String COLUMN_TARGET_AMOUNT = "target_amount";
    public static final String COLUMN_SAVED_AMOUNT = "saved_amount";
    public static final String COLUMN_CREATED_DATE = "created_date";

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
                COLUMN_DATE + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_NOTES + " TEXT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT DEFAULT '0')");

        // Expense Table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_EXPENSE + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_AMOUNT + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_PAYMENT_METHOD + " TEXT, " +
                COLUMN_NOTES + " TEXT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT DEFAULT '0')");

        // Goals Table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_GOALS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GOAL_NAME + " TEXT, " +
                COLUMN_TARGET_AMOUNT + " REAL, " +
                COLUMN_SAVED_AMOUNT + " REAL DEFAULT 0, " +
                COLUMN_CREATED_DATE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
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
            if (!columnExists(db, TABLE_EXPENSE, COLUMN_TYPE)) {
                db.execSQL("ALTER TABLE " + TABLE_EXPENSE + " ADD COLUMN " + COLUMN_TYPE + " TEXT");
            }
        }
        if (oldVersion < 5) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_GOALS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_GOAL_NAME + " TEXT, " +
                    COLUMN_TARGET_AMOUNT + " REAL, " +
                    COLUMN_SAVED_AMOUNT + " REAL DEFAULT 0, " +
                    COLUMN_CREATED_DATE + " TEXT)");
        }
        if (oldVersion < 6) {
            if (!columnExists(db, TABLE_INCOME, COLUMN_TIMESTAMP)) {
                db.execSQL("ALTER TABLE " + TABLE_INCOME + " ADD COLUMN " + COLUMN_TIMESTAMP + " TEXT DEFAULT '0'");
            }
            if (!columnExists(db, TABLE_EXPENSE, COLUMN_TIMESTAMP)) {
                db.execSQL("ALTER TABLE " + TABLE_EXPENSE + " ADD COLUMN " + COLUMN_TIMESTAMP + " TEXT DEFAULT '0'");
            }
        }
    }

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
                new String[] { email, password });
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public boolean insertIncome(String title, String amount, String date, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_NOTES, notes);
        values.put(COLUMN_TYPE, "Income");
        values.put(COLUMN_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        long result = db.insert(TABLE_INCOME, null, values);
        return result != -1;
    }

    public boolean insertExpense(String title, String amount, String date, String paymentMethod, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_NOTES, notes);
        values.put(COLUMN_PAYMENT_METHOD, paymentMethod);
        values.put(COLUMN_TYPE, "Expense");
        values.put(COLUMN_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
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
                    new String[] { startDate, endDate });
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

    // Get Transactions by Date Range
    public Cursor getTransactions(String tableName, String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (startDate != null && endDate != null) {
            return db.rawQuery("SELECT * FROM " + tableName +
                    " WHERE " + COLUMN_DATE + " BETWEEN ? AND ?", new String[] { startDate, endDate });
        } else {
            return db.rawQuery("SELECT * FROM " + tableName, null);
        }
    }

    // Get all transactions (for TransactionsFragment)
    @SuppressLint("Range")
    public List<TransactionModel> getAllTransactions(String filterType) {
        List<TransactionModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String incomeQuery = "SELECT " + COLUMN_ID + ", " + COLUMN_TITLE + ", " + COLUMN_AMOUNT + ", " + COLUMN_DATE
                + ", " + COLUMN_TIMESTAMP + ", 'Income' as " + COLUMN_TYPE + " FROM " + TABLE_INCOME;
        String expenseQuery = "SELECT " + COLUMN_ID + ", " + COLUMN_TITLE + ", " + COLUMN_AMOUNT + ", " + COLUMN_DATE
                + ", " + COLUMN_TIMESTAMP + ", 'Expense' as " + COLUMN_TYPE + " FROM " + TABLE_EXPENSE;
        String orderBy = " ORDER BY " + COLUMN_DATE + " DESC, " + COLUMN_TIMESTAMP + " DESC";

        Cursor cursor = null;

        if ("All".equals(filterType) || filterType == null) {
            String query = incomeQuery + " UNION ALL " + expenseQuery + orderBy;
            cursor = db.rawQuery(query, null);
        } else if ("Income".equals(filterType)) {
            cursor = db.rawQuery(incomeQuery + orderBy, null);
        } else if ("Expense".equals(filterType)) {
            cursor = db.rawQuery(expenseQuery + orderBy, null);
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                String amount = cursor.getString(cursor.getColumnIndex(COLUMN_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String type = cursor.getString(cursor.getColumnIndex(COLUMN_TYPE));
                TransactionModel model = new TransactionModel(id, type, title, amount, date);
                list.add(model);
            }
            cursor.close();
        }

        return list;
    }

    // Get recent transactions (for HomeFragment)
    public List<TransactionModel> getRecentTransactions(int limit) {
        List<TransactionModel> all = getAllTransactions("All");
        if (all.size() > limit) {
            return all.subList(0, limit);
        }
        return all;
    }

    // Goals CRUD
    public boolean insertGoal(String name, double targetAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GOAL_NAME, name);
        values.put(COLUMN_TARGET_AMOUNT, targetAmount);
        values.put(COLUMN_SAVED_AMOUNT, 0);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        values.put(COLUMN_CREATED_DATE, sdf.format(new java.util.Date()));

        return db.insert(TABLE_GOALS, null, values) != -1;
    }

    @SuppressLint("Range")
    public List<GoalModel> getAllGoals() {
        List<GoalModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_GOALS + " ORDER BY " + COLUMN_ID + " DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_GOAL_NAME));
            double target = cursor.getDouble(cursor.getColumnIndex(COLUMN_TARGET_AMOUNT));
            double saved = cursor.getDouble(cursor.getColumnIndex(COLUMN_SAVED_AMOUNT));
            String date = cursor.getString(cursor.getColumnIndex(COLUMN_CREATED_DATE));
            list.add(new GoalModel(id, name, target, saved, date));
        }
        cursor.close();
        return list;
    }

    public boolean updateGoalSaved(int goalId, double newSavedAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SAVED_AMOUNT, newSavedAmount);
        return db.update(TABLE_GOALS, values, COLUMN_ID + "=?", new String[] { String.valueOf(goalId) }) > 0;
    }

    public void clearAllFinancialData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INCOME, null, null);
        db.delete(TABLE_EXPENSE, null, null);
    }
}
