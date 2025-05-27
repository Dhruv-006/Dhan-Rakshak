package com.example.dhanrakshak;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    TextInputEditText editTitle, editAmount, editDate, editNotes;
    Button saveButton;
    ChipGroup categoryChipGroup;
    RadioGroup paymentMethodRadioGroup;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        editTitle = findViewById(R.id.editTitle);
        editAmount = findViewById(R.id.editAmount);
        editDate = findViewById(R.id.editDate);
        editNotes = findViewById(R.id.editNotes);
        saveButton = findViewById(R.id.saveButton);
        categoryChipGroup = findViewById(R.id.categoryChipGroup);
        paymentMethodRadioGroup = findViewById(R.id.paymentMethodRadioGroup);
        dbHelper = new DatabaseHelper(this);

        // Auto-fill today's date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        editDate.setText(sdf.format(new Date()));

        saveButton.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String amount = editAmount.getText().toString().trim();
            String date = editDate.getText().toString().trim();
            String notes = editNotes.getText().toString().trim();

            // Get selected category chip text
            int selectedChipId = categoryChipGroup.getCheckedChipId();
            String category = null;
            if (selectedChipId != -1) {
                Chip selectedChip = findViewById(selectedChipId);
                category = selectedChip.getText().toString();
            }

            // Get selected payment method
            int selectedPaymentId = paymentMethodRadioGroup.getCheckedRadioButtonId();
            String paymentMethod = null;
            if (selectedPaymentId != -1) {
                RadioButton selectedRadio = findViewById(selectedPaymentId);
                paymentMethod = selectedRadio.getText().toString();
            }

            if (title.isEmpty() || amount.isEmpty() || date.isEmpty() || category == null || paymentMethod == null) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!amount.matches("\\d+(\\.\\d{1,2})?")) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to database
            boolean success = dbHelper.insertExpense(title, amount, date, category, paymentMethod, notes);

            if (success) {
                Toast.makeText(this, "Expense saved successfully!", Toast.LENGTH_SHORT).show();
                clearInputs();
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
        editNotes.setText("");
        categoryChipGroup.clearCheck();
        paymentMethodRadioGroup.clearCheck();
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
