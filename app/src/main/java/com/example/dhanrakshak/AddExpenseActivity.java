package com.example.dhanrakshak;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    EditText editTitle, editAmount, editDate;
    Button saveButton;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        editTitle = findViewById(R.id.editTitle);
        editAmount = findViewById(R.id.editAmount);
        editDate = findViewById(R.id.editDate);
        saveButton = findViewById(R.id.saveButton);
        dbHelper = new DatabaseHelper(this);

        // ✅ Auto-fill today's date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());
        editDate.setText(todayDate);

        saveButton.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String amount = editAmount.getText().toString().trim();
            String date = editDate.getText().toString().trim();

            if (title.isEmpty() || amount.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!amount.matches("\\d+(\\.\\d{1,2})?")) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.insertExpense(title, amount, date);
            if (success) {
                Toast.makeText(this, "Expense saved successfully!", Toast.LENGTH_SHORT).show();
                clearInputs();
                // Reset date after clear
                editDate.setText(sdf.format(new Date()));
            } else {
                Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show();
            }
        });

        setupBottomNavigation();
    }

    private void clearInputs() {
        editTitle.setText("");
        editAmount.setText("");
        editDate.setText("");
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_expense);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_income) {
                startActivity(new Intent(this, AddIncomeActivity.class));
            } else if (id == R.id.nav_statistics) {
                startActivity(new Intent(this, StatisticsActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.nav_expense) {
                return true; // Already here
            }

            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }
}
