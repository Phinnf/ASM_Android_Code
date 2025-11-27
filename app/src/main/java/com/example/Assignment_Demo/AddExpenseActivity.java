package com.example.Assignment_Demo;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    EditText etDescription, etAmount;
    Spinner spinnerCategory;
    Button btnSelectDate, btnSaveExpense;
    DatabaseHelper dbHelper;

    private Calendar selectedDate = Calendar.getInstance();
    private int currentUserId;
    private ImageView btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        dbHelper = new DatabaseHelper(this);

        // Get User ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        etDescription = findViewById(R.id.etExpenseDescription);
        etAmount = findViewById(R.id.etExpenseAmount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSaveExpense = findViewById(R.id.btnSaveExpense);
        btnBack = findViewById(R.id.btn_back);


        // Setup Category Spinner
        // You should define this list in res/values/strings.xml
        String[] categories = {"Food", "Transportation", "Rent", "Education", "Entertainment", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        // Setup Select Date Button
        updateDateButtonText();
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Setup Save Button
        btnSaveExpense.setOnClickListener(v -> saveExpense());

        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateButtonText();
        };

        new DatePickerDialog(this, dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        btnSelectDate.setText("Date: " + sdf.format(selectedDate.getTime()));
    }

    private void saveExpense() {
        String description = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();

        // Format date to "YYYY-MM-DD" to save to DB
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String date = dbDateFormat.format(selectedDate.getTime());

        if (description.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter Description and Amount", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            boolean isAdded = dbHelper.addExpense(currentUserId, description, amount, category, date);

            if(isAdded) {
                Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show();
                finish(); // Close Activity and return to Dashboard
            } else {
                Toast.makeText(this, "Error saving expense", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
        }
    }
}
