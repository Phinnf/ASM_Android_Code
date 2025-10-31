package com.example.Assignment_Demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;

public class BudgetActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    private int currentUserId;

    // Map to link EditText with Category name
    private Map<String, EditText> budgetEditTextMap;
    private Button btnSaveBudgets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        dbHelper = new DatabaseHelper(this);

        // Get User ID
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);
        if (currentUserId == -1) { finish(); return; }

        initializeViews();
        loadBudgets();

        btnSaveBudgets.setOnClickListener(v -> saveBudgets());
    }

    private void initializeViews() {
        budgetEditTextMap = new HashMap<>();
        // This category list MUST MATCH the list in Lab 2
        budgetEditTextMap.put("Food", findViewById(R.id.etBudgetFood));
        budgetEditTextMap.put("Transportation", findViewById(R.id.etBudgetTransport));
        budgetEditTextMap.put("Rent", findViewById(R.id.etBudgetRent));
        budgetEditTextMap.put("Education", findViewById(R.id.etBudgetEducation));
        budgetEditTextMap.put("Entertainment", findViewById(R.id.etBudgetEntertainment));
        budgetEditTextMap.put("Other", findViewById(R.id.etBudgetOther));

        btnSaveBudgets = findViewById(R.id.btnSaveBudgets);
    }

    // Load saved budgets from DB into the EditTexts
    private void loadBudgets() {
        for (Map.Entry<String, EditText> entry : budgetEditTextMap.entrySet()) {
            String category = entry.getKey();
            EditText editText = entry.getValue();

            double budgetAmount = dbHelper.getBudget(currentUserId, category);
            if (budgetAmount > 0) {
                editText.setText(String.valueOf(budgetAmount));
            }
        }
    }

    // Save budgets from EditTexts to DB
    private void saveBudgets() {
        boolean allSuccess = true;
        for (Map.Entry<String, EditText> entry : budgetEditTextMap.entrySet()) {
            String category = entry.getKey();
            String amountStr = entry.getValue().getText().toString().trim();

            double amount = 0;
            if (!amountStr.isEmpty()) {
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid amount for: " + category, Toast.LENGTH_SHORT).show();
                    allSuccess = false;
                    continue; // Skip if error
                }
            }

            // Save to DB (save 0 if user cleared the field)
            dbHelper.setBudget(currentUserId, category, amount);
        }

        if(allSuccess) {
            Toast.makeText(this, "Budgets saved!", Toast.LENGTH_SHORT).show();
            finish(); // Close Activity
        }
    }
}


