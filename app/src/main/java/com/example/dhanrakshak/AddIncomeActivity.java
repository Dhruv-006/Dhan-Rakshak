package com.example.dhanrakshak;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddIncomeActivity extends AppCompatActivity {

    TextInputEditText editTitle, editAmount, editDate, editNotes;
    Button saveButton;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        // Initialize views
        editTitle = findViewById(R.id.editTitle);
        editAmount = findViewById(R.id.editAmount);
        editDate = findViewById(R.id.editDate);
        editNotes = findViewById(R.id.editNotes);
        saveButton = findViewById(R.id.saveButton);
        dbHelper = new DatabaseHelper(this);

        // ✅ Auto-fill current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());
        editDate.setText(todayDate);

        // ✅ Save button logic
        saveButton.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String amount = editAmount.getText().toString().trim();
            String date = editDate.getText().toString().trim();
            String notes = editNotes.getText().toString().trim();



            if (title.isEmpty() || amount.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!amount.matches("\\d+(\\.\\d{1,2})?")) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.insertIncome(title, amount, date, notes);
            if (success) {
                Toast.makeText(this, "Income saved successfully!", Toast.LENGTH_SHORT).show();
                clearInputs();
                editDate.setText(sdf.format(new Date())); // Reset to current date
            } else {
                Toast.makeText(this, "Failed to save income", Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_income);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_income) {
                return true;
            } else if (id == R.id.nav_expense) {
                startActivity(new Intent(this, AddExpenseActivity.class));
            } else if (id == R.id.nav_statistics) {
                startActivity(new Intent(this, StatisticsActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            }

            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }

    private void clearInputs() {
        editTitle.setText("");
        editAmount.setText("");
        editNotes.setText("");
    }
}
