package com.example.dhanrakshak;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail, tvAvatarLetter, tvProfileScore, tvScoreStatus;
    private SwitchCompat switchDarkMode;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper db;
    private String userEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        db = new DatabaseHelper(requireContext());
        sharedPreferences = requireContext().getSharedPreferences("LoginPrefs", requireContext().MODE_PRIVATE);
        userEmail = sharedPreferences.getString("email", "unknown");

        // Init views
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvAvatarLetter = view.findViewById(R.id.tvAvatarLetter);
        tvProfileScore = view.findViewById(R.id.tvProfileScore);
        tvScoreStatus = view.findViewById(R.id.tvScoreStatus);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);

        // Load user info
        String username = sharedPreferences.getString("username", "Dhruv");
        String email = sharedPreferences.getString("email", "user@example.com");
        tvProfileName.setText(username);
        tvProfileEmail.setText(email);
        tvAvatarLetter.setText(username.substring(0, 1).toUpperCase());

        // Calculate score
        loadFinancialScore();

        // Dark mode toggle
        boolean isDarkMode = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
        switchDarkMode.setChecked(isDarkMode);
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Edit Username
        view.findViewById(R.id.btnEditUsername).setOnClickListener(v -> showEditUsernameDialog());

        // Export Data
        view.findViewById(R.id.btnExportData).setOnClickListener(
                v -> Toast.makeText(requireContext(), "Export feature coming soon!", Toast.LENGTH_SHORT).show());

        // Security
        view.findViewById(R.id.btnSecurity).setOnClickListener(
                v -> Toast.makeText(requireContext(), "Security settings coming soon!", Toast.LENGTH_SHORT).show());

        // About
        view.findViewById(R.id.btnAbout).setOnClickListener(v -> Toast
                .makeText(requireContext(), "Dhan Rakshak\nVersion 1.0\nMade with ❤️ by Dhruv", Toast.LENGTH_LONG)
                .show());

        // Clear Data
        view.findViewById(R.id.btnClearData).setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Clear All Data")
                    .setMessage("Are you sure? This will delete all your financial data.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        db.clearAllFinancialData(userEmail);
                        Toast.makeText(requireContext(), "All data cleared!", Toast.LENGTH_SHORT).show();
                        loadFinancialScore();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Logout
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply();
            Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void loadFinancialScore() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String endDate = sdf.format(cal.getTime());
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        String startDate = sdf.format(cal.getTime());

        double income = db.getTotalAmountWithDateFilter(userEmail, DatabaseHelper.TABLE_INCOME, startDate, endDate);
        double expense = db.getTotalAmountWithDateFilter(userEmail, DatabaseHelper.TABLE_EXPENSE, startDate, endDate);

        int score;
        if (income == 0) {
            score = 50;
        } else {
            double ratio = expense / income;
            if (ratio <= 0.3)
                score = 95;
            else if (ratio <= 0.5)
                score = 80;
            else if (ratio <= 0.7)
                score = 65;
            else if (ratio <= 0.9)
                score = 45;
            else
                score = 25;
        }

        tvProfileScore.setText(score + "/100");

        if (score >= 80)
            tvScoreStatus.setText("Excellent standing");
        else if (score >= 60)
            tvScoreStatus.setText("Good standing");
        else if (score >= 40)
            tvScoreStatus.setText("Needs improvement");
        else
            tvScoreStatus.setText("Critical - reduce spending");
    }

    private void showEditUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Username");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        EditText input = new EditText(requireContext());
        input.setHint("New username");
        input.setText(sharedPreferences.getString("username", ""));
        layout.addView(input);

        builder.setView(layout);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                sharedPreferences.edit().putString("username", newName).apply();
                tvProfileName.setText(newName);
                tvAvatarLetter.setText(newName.substring(0, 1).toUpperCase());
                Toast.makeText(requireContext(), "Username updated!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
