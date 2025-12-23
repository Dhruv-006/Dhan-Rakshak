package com.example.dhanrakshak;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailInput, newPasswordInput;
    Button resetButton;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            WindowInsetsCompat defaultInsets = ViewCompat.onApplyWindowInsets(v, insets);
            return defaultInsets;
        });

        emailInput = findViewById(R.id.forgotEmail);
        newPasswordInput = findViewById(R.id.forgotNewPassword);
        resetButton = findViewById(R.id.resetPasswordButton);
        dbHelper = new DatabaseHelper(this);

        resetButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();

            if (email.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!checkIfUserExists(email)) {
                Toast.makeText(this, "Email not registered", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = updatePassword(email, newPassword);
            if (success) {
                Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finish this activity
            } else {
                Toast.makeText(this, "Failed to reset password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkIfUserExists(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        int rows = db.update("users", values, "email = ?", new String[]{email});
        return rows > 0;
    }
}
