package com.example.dhanrakshak;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginButton;
    CheckBox rememberMe;
    TextView forgotPassword, signupText;
    DatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        rememberMe = findViewById(R.id.rememberMe);
        signupText = findViewById(R.id.signupText);
        forgotPassword = findViewById(R.id.forgotPassword); // Make sure this exists in XML

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);

        loadSavedCredentials(); // Load saved email & password if "Remember Me" was used

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

                // Navigate to Income screen after successful login
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
            // You can add ForgotPasswordActivity later if needed
        });
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
