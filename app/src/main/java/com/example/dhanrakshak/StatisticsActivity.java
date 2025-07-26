package com.example.dhanrakshak;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.*;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
//import com.github.mikephil.charting.data.BarEntry;
//import com.github.mikephil.charting.data.BarDataSet;
//import com.github.mikephil.charting.data.BarData;
import android.graphics.Color;



public class StatisticsActivity extends AppCompatActivity {

    private TextView totalIncomeText, totalExpenseText, balanceText;
    private RecyclerView transactionRecyclerView;

    private BarChart barChart;
    private DatabaseHelper dbHelper;

    private DatabaseHelper db;
    private TransactionAdapter adapter;
    private List<TransactionModel> transactionList;
    private TabLayout filterTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        db = new DatabaseHelper(this);
        dbHelper = new DatabaseHelper(this);

        totalIncomeText = findViewById(R.id.totalIncomeText);
        totalExpenseText = findViewById(R.id.totalExpenseText);
        balanceText = findViewById(R.id.balanceText);
        barChart = findViewById(R.id.barChart);
        transactionRecyclerView = findViewById(R.id.transactionRecyclerView);
        filterTabLayout = findViewById(R.id.filterTabLayout);

        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(this, transactionList);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionRecyclerView.setAdapter(adapter);

        // Set TabListener
        filterTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String filter = tab.getText().toString();
                loadStatistics(filter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                String filter = tab.getText().toString();
                loadStatistics(filter);
            }
        });

        // Load default data (Daily)
        loadStatistics("Daily");

        // ✅ Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_statistics);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_statistics) return true;
            if (id == R.id.nav_income) {
                startActivity(new Intent(this, AddIncomeActivity.class));
            } else if (id == R.id.nav_expense) {
                startActivity(new Intent(this, AddExpenseActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }

    private void fetchAndAddTransactions(String tableName, String type, String startDate, String endDate) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE date BETWEEN ? AND ?", new String[]{startDate, endDate});

        while (cursor.moveToNext()) {
            float amount = cursor.getFloat(cursor.getColumnIndexOrThrow("amount"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        }

        cursor.close();
    }



    private void loadStatistics(String filterType) {
        String[] range = getDateRange(filterType);
        String startDate = range[0];
        String endDate = range[1];

        double totalIncome = db.getTotalAmountWithDateFilter(DatabaseHelper.TABLE_INCOME, startDate, endDate);
        double totalExpense = db.getTotalAmountWithDateFilter(DatabaseHelper.TABLE_EXPENSE, startDate, endDate);
        double balance = totalIncome - totalExpense;

        totalIncomeText.setText("₹" + totalIncome);
        totalExpenseText.setText("₹" + totalExpense);
        balanceText.setText("₹" + balance);

        // Chart
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, (float) totalIncome));
        entries.add(new BarEntry(1f, (float) totalExpense));
        entries.add(new BarEntry(2f, (float) balance));

        BarDataSet dataSet = new BarDataSet(entries, "Statistics");
        dataSet.setColors(new int[]{Color.GREEN, Color.RED, Color.BLUE});
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1000);
        barChart.invalidate();

        // Recycler data
        transactionList.clear();
        fetchAndAddTransactions(DatabaseHelper.TABLE_INCOME, "Income", startDate, endDate);
        fetchAndAddTransactions(DatabaseHelper.TABLE_EXPENSE, "Expense", startDate, endDate);
        adapter.notifyDataSetChanged();
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