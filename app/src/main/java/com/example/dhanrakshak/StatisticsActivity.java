package com.example.dhanrakshak;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.*;

public class StatisticsActivity extends AppCompatActivity {

    private TextView totalIncomeText, totalExpenseText, balanceText;
    private RecyclerView transactionRecyclerView;
    private Button btnDaily, btnWeekly, btnMonthly;

    private DatabaseHelper db;
    private TransactionAdapter adapter;
    private List<TransactionModel> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        db = new DatabaseHelper(this);

        totalIncomeText = findViewById(R.id.totalIncomeText);
        totalExpenseText = findViewById(R.id.totalExpenseText);
        balanceText = findViewById(R.id.balanceText);
        transactionRecyclerView = findViewById(R.id.transactionRecyclerView);

        btnDaily = findViewById(R.id.btnDaily);
        btnWeekly = findViewById(R.id.btnWeekly);
        btnMonthly = findViewById(R.id.btnMonthly);

        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(this, transactionList);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionRecyclerView.setAdapter(adapter);

        btnDaily.setOnClickListener(v -> loadStatistics("Daily"));
        btnWeekly.setOnClickListener(v -> loadStatistics("Weekly"));
        btnMonthly.setOnClickListener(v -> loadStatistics("Monthly"));

        // Load all by default
        loadStatistics("All");

        // ✅ Bottom Navigation setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_statistics);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_statistics) {
                return true; // Already here
            } else if (id == R.id.nav_income) {
                startActivity(new Intent(this, AddIncomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_expense) {
                startActivity(new Intent(this, AddExpenseActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private void loadStatistics(String filterType) {
        String[] range = getDateRange(filterType);
        String startDate = range[0];
        String endDate = range[1];

        double totalIncome = db.getTotalAmountWithDateFilter(DatabaseHelper.TABLE_INCOME, startDate, endDate);
        double totalExpense = db.getTotalAmountWithDateFilter(DatabaseHelper.TABLE_EXPENSE, startDate, endDate);
        double balance = totalIncome - totalExpense;

        totalIncomeText.setText("Total Income: ₹" + totalIncome);
        totalExpenseText.setText("Total Expense: ₹" + totalExpense);
        balanceText.setText("Balance: ₹" + balance);

        transactionList.clear();

        fetchAndAddTransactions(DatabaseHelper.TABLE_INCOME, "Income", startDate, endDate);
        fetchAndAddTransactions(DatabaseHelper.TABLE_EXPENSE, "Expense", startDate, endDate);

        adapter.notifyDataSetChanged();
    }

    private void fetchAndAddTransactions(String tableName, String type, String startDate, String endDate) {
        Cursor cursor = db.getTransactions(tableName, startDate, endDate);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID);
            int titleIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE);
            int amountIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT);
            int dateIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE);

            do {
                int id = cursor.getInt(idIndex);
                String category = cursor.getString(titleIndex);
                String amount = cursor.getString(amountIndex);
                String date = cursor.getString(dateIndex);

                TransactionModel model = new TransactionModel(id, type, category, amount, date);
                transactionList.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private String[] getDateRange(String filterType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String endDate = sdf.format(cal.getTime());
        String startDate;

        switch (filterType) {
            case "Daily":
                startDate = endDate;
                break;
            case "Weekly":
                cal.add(Calendar.DAY_OF_YEAR, -6);
                startDate = sdf.format(cal.getTime());
                break;
            case "Monthly":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = sdf.format(cal.getTime());
                break;
            default:
                cal.set(Calendar.YEAR, 2000);
                startDate = sdf.format(cal.getTime());
        }
        return new String[]{startDate, endDate};
    }
}
