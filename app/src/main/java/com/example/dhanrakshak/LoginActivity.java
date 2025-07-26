package com.example.dhanrakshak;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText email, password;
    MaterialButton loginButton;
    MaterialCheckBox rememberMe;
    TextView forgotPassword, signupText;
    DatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize sharedPreferences first
        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);

        // 🟢 Check if user is already logged in
        boolean isRemembered = sharedPreferences.getBoolean("remember", false);
        if (isRemembered) {
            // If already logged in, go to main screen
            Intent intent = new Intent(LoginActivity.this, AddIncomeActivity.class);
            startActivity(intent);
            finish();  // Finish LoginActivity so user can't go back to it
            return;
        }

        // Only setContentView if not already logged in
        setContentView(R.layout.activity_login);

        // 🔽 Initialize views
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        rememberMe = findViewById(R.id.rememberMe);
        signupText = findViewById(R.id.signupText);
        forgotPassword = findViewById(R.id.forgotPassword);

        dbHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isValid = dbHelper.checkUser(userEmail, userPassword);
            if (isValid) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                if (rememberMe.isChecked()) {
                    saveCredentials(userEmail, userPassword);
                } else {
                    clearCredentials();
                }

                startActivity(new Intent(LoginActivity.this, AddIncomeActivity.class));
                finish();

            } else {
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });

        signupText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });

        forgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show();
            // You can navigate to ForgotPasswordActivity here if implemented
        });

        loadSavedCredentials(); // Moved here so it doesn't run if auto-login happens
    }


    private void saveCredentials(String userEmail, String userPassword) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", userEmail);
        editor.putString("password", userPassword);
        editor.putBoolean("remember", true);
        editor.apply();
    }

    private void loadSavedCredentials() {
        boolean isRemembered = sharedPreferences.getBoolean("remember", false);
        if (isRemembered) {
            email.setText(sharedPreferences.getString("email", ""));
            password.setText(sharedPreferences.getString("password", ""));
            rememberMe.setChecked(true);
        }
    }

    private void clearCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
