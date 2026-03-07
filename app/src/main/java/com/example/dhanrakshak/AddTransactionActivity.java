package com.example.dhanrakshak;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private TextInputEditText editTitle, editAmount, editDate, editNotes;
    private Button saveButton;
    private RadioGroup paymentMethodRadioGroup;
    private TabLayout typeTabLayout;
    private TextView tvPaymentLabel;
    private DatabaseHelper dbHelper;
    private boolean isExpense = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        editTitle = findViewById(R.id.editTitle);
        editAmount = findViewById(R.id.editAmount);
        editDate = findViewById(R.id.editDate);
        editNotes = findViewById(R.id.editNotes);
        saveButton = findViewById(R.id.saveButton);
        paymentMethodRadioGroup = findViewById(R.id.paymentMethodRadioGroup);
        typeTabLayout = findViewById(R.id.typeTabLayout);
        tvPaymentLabel = findViewById(R.id.tvPaymentLabel);
        dbHelper = new DatabaseHelper(this);

        // Auto-fill today's date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        editDate.setText(sdf.format(new Date()));

        // Check intent for pre-selected type
        String intentType = getIntent().getStringExtra("type");
        if ("income".equals(intentType)) {
            typeTabLayout.getTabAt(1).select();
            isExpense = false;
            paymentMethodRadioGroup.setVisibility(View.GONE);
            tvPaymentLabel.setVisibility(View.GONE);
        }

        // Type toggle listener
        typeTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isExpense = tab.getPosition() == 0;
                if (isExpense) {
                    paymentMethodRadioGroup.setVisibility(View.VISIBLE);
                    tvPaymentLabel.setVisibility(View.VISIBLE);
                } else {
                    paymentMethodRadioGroup.setVisibility(View.GONE);
                    tvPaymentLabel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        saveButton.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String amount = editAmount.getText().toString().trim();
            String date = editDate.getText().toString().trim();
            String notes = editNotes.getText().toString().trim();

            if (title.isEmpty() || amount.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!amount.matches("\\d+(\\.\\d{1,2})?")) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success;
            if (isExpense) {
                int selectedPaymentId = paymentMethodRadioGroup.getCheckedRadioButtonId();
                String paymentMethod = "Cash";
                if (selectedPaymentId != -1) {
                    RadioButton selectedRadio = findViewById(selectedPaymentId);
                    paymentMethod = selectedRadio.getText().toString();
                }
                success = dbHelper.insertExpense(title, amount, date, paymentMethod, notes);
            } else {
                success = dbHelper.insertIncome(title, amount, date, notes);
            }

            if (success) {
                Toast.makeText(this, (isExpense ? "Expense" : "Income") + " saved!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
