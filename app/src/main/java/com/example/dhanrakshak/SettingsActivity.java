package com.example.dhanrakshak;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    private Button btnLogout, btnAbout, btnSaveUsername;
    private EditText editUsername;
    private TextView tvUserName;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Init
        btnLogout = findViewById(R.id.btnLogout);
        btnAbout = findViewById(R.id.btnAbout);
        btnSaveUsername = findViewById(R.id.btnSaveUsername);
        editUsername = findViewById(R.id.editUsername);
        tvUserName = findViewById(R.id.tvUserName);

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "User");
        tvUserName.setText("Welcome, " + username + "!");

        // 🔄 Save new username
        btnSaveUsername.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            if (!newUsername.isEmpty()) {
                sharedPreferences.edit().putString("username", newUsername).apply();
                tvUserName.setText("Welcome, " + newUsername + "!");
                Toast.makeText(this, "Username updated!", Toast.LENGTH_SHORT).show();
                editUsername.setText("");
            } else {
                Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
            }
        });

        // 🚪 Logout
        btnLogout.setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply();
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // ℹ️ About
        btnAbout.setOnClickListener(v ->
                Toast.makeText(this, "Dhan Rakshak\nVersion 1.0\nMade with ❤️ by Dhruv", Toast.LENGTH_LONG).show()
        );

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        bottomNav.setSelectedItemId(R.id.nav_settings);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_income) {
                startActivity(new Intent(this, AddIncomeActivity.class));
            } else if (id == R.id.nav_expense) {
                startActivity(new Intent(this, AddExpenseActivity.class));
            } else if (id == R.id.nav_statistics) {
                startActivity(new Intent(this, StatisticsActivity.class));
            } else if (id == R.id.nav_settings) {
                return true;
            }
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
    }
}
