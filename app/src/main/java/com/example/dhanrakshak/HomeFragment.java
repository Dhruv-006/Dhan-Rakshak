package com.example.dhanrakshak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.*;

public class HomeFragment extends Fragment {

    private TextView tvGreeting, tvUserName, tvTotalBalance, tvMonthlyIncome, tvMonthlyExpenses;
    private TextView tvHealthScore, tvHealthDescription;
    private BarChart barChart;
    private RecyclerView recentTransactionsRecyclerView;
    private DatabaseHelper db;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = new DatabaseHelper(requireContext());

        // Init views
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance);
        tvMonthlyIncome = view.findViewById(R.id.tvMonthlyIncome);
        tvMonthlyExpenses = view.findViewById(R.id.tvMonthlyExpenses);
        tvHealthScore = view.findViewById(R.id.tvHealthScore);
        tvHealthDescription = view.findViewById(R.id.tvHealthDescription);
        barChart = view.findViewById(R.id.barChart);
        recentTransactionsRecyclerView = view.findViewById(R.id.recentTransactionsRecyclerView);

        // Set greeting
        setGreeting();

        // Load username
        SharedPreferences prefs = requireContext().getSharedPreferences("LoginPrefs", requireContext().MODE_PRIVATE);
        String username = prefs.getString("username", "Dhruv");
        userEmail = prefs.getString("email", "unknown");
        tvUserName.setText(username);

        // Load data
        loadBalanceData();
        setupBarChart();
        loadRecentTransactions();

        // Quick action clicks
        view.findViewById(R.id.btnQuickExpense).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddTransactionActivity.class);
            intent.putExtra("type", "expense");
            startActivity(intent);
        });

        view.findViewById(R.id.btnQuickIncome).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddTransactionActivity.class);
            intent.putExtra("type", "income");
            startActivity(intent);
        });

        view.findViewById(R.id.tvSeeAll).setOnClickListener(v -> {
            // Switch to transactions tab
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).findViewById(R.id.bottomNavigationView)
                        .findViewById(R.id.nav_transactions).performClick();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBalanceData();
        setupBarChart();
        loadRecentTransactions();
    }

    private void setGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            tvGreeting.setText("Good Morning,");
        } else if (hour < 17) {
            tvGreeting.setText("Good Afternoon,");
        } else {
            tvGreeting.setText("Good Evening,");
        }
    }

    private void loadBalanceData() {
        // Get monthly date range
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String endDate = sdf.format(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = sdf.format(cal.getTime());

        double totalIncome = db.getTotalAmountWithDateFilter(userEmail, DatabaseHelper.TABLE_INCOME, startDate,
                endDate);
        double totalExpense = db.getTotalAmountWithDateFilter(userEmail, DatabaseHelper.TABLE_EXPENSE, startDate,
                endDate);
        double balance = totalIncome - totalExpense;

        tvTotalBalance.setText(String.format("₹%.2f", balance));
        tvMonthlyIncome.setText(String.format("₹%.2f", totalIncome));
        tvMonthlyExpenses.setText(String.format("₹%.2f", totalExpense));

        // Financial health score
        int score = calculateHealthScore(totalIncome, totalExpense);
        tvHealthScore.setText(String.valueOf(score));

        if (score >= 80) {
            tvHealthDescription.setText("Excellent! You're saving well.");
        } else if (score >= 60) {
            tvHealthDescription.setText("Good! Keep tracking expenses.");
        } else if (score >= 40) {
            tvHealthDescription.setText("Be cautious with spending.");
        } else {
            tvHealthDescription.setText("Expenses are too high!");
        }
    }

    private int calculateHealthScore(double income, double expense) {
        if (income == 0)
            return 50;
        double ratio = expense / income;
        if (ratio <= 0.3)
            return 95;
        if (ratio <= 0.5)
            return 80;
        if (ratio <= 0.7)
            return 65;
        if (ratio <= 0.9)
            return 45;
        return 25;
    }

    private void setupBarChart() {
        List<BarEntry> entries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        // Last 6 months spending
        String[] labels = new String[6];
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());

        for (int i = 5; i >= 0; i--) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -i);
            c.set(Calendar.DAY_OF_MONTH, 1);
            String start = sdf.format(c.getTime());
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
            String end = sdf.format(c.getTime());

            double expense = db.getTotalAmountWithDateFilter(userEmail, DatabaseHelper.TABLE_EXPENSE, start, end);
            entries.add(new BarEntry(5 - i, (float) expense));
            labels[5 - i] = monthFormat.format(c.getTime());
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(Color.parseColor("#10B981"));
        dataSet.setValueTextColor(Color.parseColor("#64748B"));
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawGridBackground(false);

        String[] finalLabels = labels;
        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = (int) value;
                if (idx >= 0 && idx < finalLabels.length)
                    return finalLabels[idx];
                return "";
            }
        });

        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setTextColor(Color.parseColor("#64748B"));
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisLeft().setTextColor(Color.parseColor("#64748B"));
        barChart.getAxisRight().setEnabled(false);
        barChart.setTouchEnabled(false);
        barChart.animateY(800);
        barChart.invalidate();
    }

    private void loadRecentTransactions() {
        List<TransactionModel> transactions = db.getRecentTransactions(userEmail, 5);
        TransactionAdapter adapter = new TransactionAdapter(requireContext(), transactions);
        recentTransactionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recentTransactionsRecyclerView.setAdapter(adapter);
    }
}
