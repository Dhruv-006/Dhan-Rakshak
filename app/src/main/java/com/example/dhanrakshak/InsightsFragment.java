package com.example.dhanrakshak;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.*;

public class InsightsFragment extends Fragment {

    private PieChart pieChart;
    private BarChart trendBarChart;
    private TextView tvInsight1, tvPrediction, tvSavingsTip;
    private DatabaseHelper db;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insights, container, false);

        db = new DatabaseHelper(requireContext());
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("LoginPrefs",
                requireContext().MODE_PRIVATE);
        userEmail = prefs.getString("email", "unknown");

        pieChart = view.findViewById(R.id.pieChart);
        trendBarChart = view.findViewById(R.id.trendBarChart);
        tvInsight1 = view.findViewById(R.id.tvInsight1);
        tvPrediction = view.findViewById(R.id.tvPrediction);
        tvSavingsTip = view.findViewById(R.id.tvSavingsTip);

        setupPieChart();
        setupTrendChart();
        loadInsights();

        return view;
    }

    private void setupPieChart() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String endDate = sdf.format(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = sdf.format(cal.getTime());

        double totalIncome = db.getTotalAmountWithDateFilter(userEmail, DatabaseHelper.TABLE_INCOME, startDate,
                endDate);
        double totalExpense = db.getTotalAmountWithDateFilter(userEmail, DatabaseHelper.TABLE_EXPENSE, startDate,
                endDate);

        List<PieEntry> entries = new ArrayList<>();
        if (totalIncome > 0)
            entries.add(new PieEntry((float) totalIncome, "Income"));
        if (totalExpense > 0)
            entries.add(new PieEntry((float) totalExpense, "Expense"));

        if (entries.isEmpty()) {
            entries.add(new PieEntry(1, "No Data"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[] {
                Color.parseColor("#10B981"),
                Color.parseColor("#EF4444"),
                Color.parseColor("#94A3B8")
        });
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        dataSet.setSliceSpace(3f);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(55f);
        pieChart.setTransparentCircleRadius(60f);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setCenterText("Breakdown");
        pieChart.setCenterTextSize(14f);
        pieChart.setCenterTextColor(Color.parseColor("#64748B"));
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setTextColor(Color.parseColor("#64748B"));
        pieChart.animateY(800);
        pieChart.invalidate();
    }

    private void setupTrendChart() {
        List<BarEntry> entries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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
        dataSet.setColor(Color.parseColor("#3B82F6"));
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        trendBarChart.setData(barData);
        trendBarChart.getDescription().setEnabled(false);
        trendBarChart.getLegend().setEnabled(false);
        trendBarChart.setDrawGridBackground(false);

        String[] finalLabels = labels;
        trendBarChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = (int) value;
                if (idx >= 0 && idx < finalLabels.length)
                    return finalLabels[idx];
                return "";
            }
        });

        trendBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        trendBarChart.getXAxis().setGranularity(1f);
        trendBarChart.getXAxis().setDrawGridLines(false);
        trendBarChart.getXAxis().setTextColor(Color.parseColor("#64748B"));
        trendBarChart.getAxisLeft().setDrawGridLines(false);
        trendBarChart.getAxisLeft().setTextColor(Color.parseColor("#64748B"));
        trendBarChart.getAxisRight().setEnabled(false);
        trendBarChart.setTouchEnabled(false);
        trendBarChart.animateY(800);
        trendBarChart.invalidate();
    }

    private void loadInsights() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String endDate = sdf.format(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = sdf.format(cal.getTime());

        double monthlyExpense = db.getTotalAmountWithDateFilter(userEmail, DatabaseHelper.TABLE_EXPENSE, startDate,
                endDate);
        double monthlyIncome = db.getTotalAmountWithDateFilter(userEmail, DatabaseHelper.TABLE_INCOME, startDate,
                endDate);

        // Dynamic insights
        if (monthlyExpense > 0) {
            tvInsight1.setText(String.format("You've spent ₹%.0f this month so far.", monthlyExpense));
        } else {
            tvInsight1.setText("No expenses recorded this month yet.");
        }

        // Prediction (simple linear projection)
        int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int daysInMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        double projectedExpense = (monthlyExpense / Math.max(dayOfMonth, 1)) * daysInMonth;
        tvPrediction.setText(String.format("At this rate, you may spend ₹%.0f this month.", projectedExpense));

        // Savings tip
        if (monthlyIncome > 0 && monthlyExpense > monthlyIncome * 0.7) {
            tvSavingsTip.setText("Your expenses are over 70% of income. Try cutting discretionary spending.");
        } else if (monthlyIncome > 0) {
            double savings = monthlyIncome - monthlyExpense;
            tvSavingsTip.setText(String.format("Great job! You're saving ₹%.0f this month.", savings));
        } else {
            tvSavingsTip.setText("Start tracking your income to get personalized savings tips.");
        }
    }
}
