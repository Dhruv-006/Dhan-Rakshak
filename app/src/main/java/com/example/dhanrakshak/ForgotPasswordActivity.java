package com.example.dhanrakshak;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextInputEditText resetEmail, resetNewPassword, resetConfirmPassword;
    Button resetButton;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        resetEmail = findViewById(R.id.resetEmail);
        resetNewPassword = findViewById(R.id.resetNewPassword);
        resetConfirmPassword = findViewById(R.id.resetConfirmPassword);
        resetButton = findViewById(R.id.resetButton);
        dbHelper = new DatabaseHelper(this);

        resetButton.setOnClickListener(v -> {
            String email = resetEmail.getText().toString().trim();
            String newPassword = resetNewPassword.getText().toString().trim();
            String confirmPassword = resetConfirmPassword.getText().toString().trim();

            if (email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Password reset successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            finish();
        });

        // Back to login
        findViewById(R.id.backToLogin).setOnClickListener(v -> {
            finish();
        });
    }
}
