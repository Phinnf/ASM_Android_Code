package com.example.Assignment_Demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast; // Added for Toast message
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private int currentUserId;
    private DatabaseHelper dbHelper;
    private ExpenseAdapter expenseAdapter;

    private TextView tvHello, tvCurrentMonth, tvTotalSpending, tvTotalBudget, tvRemainingBudget;
    private RecyclerView rvExpenses;
    private FloatingActionButton fabAddExpense;
    private Button btnGoToBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);

        // Get User ID from Intent
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "User ID not found in Intent, finishing activity.", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            // Added Toast to show the received User ID
            Toast.makeText(this, "Dashboard received User ID: " + currentUserId, Toast.LENGTH_SHORT).show();
        }

        initializeViews();
        setupListeners();
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardSummary();
        loadExpenseList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the cursor by swapping it with null
        if (expenseAdapter != null) {
            expenseAdapter.swapCursor(null);
        }
    }

    private void initializeViews() {
        tvHello = findViewById(R.id.tvHello);
        rvExpenses = findViewById(R.id.rvExpenses);
        fabAddExpense = findViewById(R.id.fabAddExpense);
        btnGoToBudget = findViewById(R.id.btnGoToBudget);
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        tvTotalSpending = findViewById(R.id.tvTotalSpending);
        tvTotalBudget = findViewById(R.id.tvTotalBudget);
        tvRemainingBudget = findViewById(R.id.tvRemainingBudget);

        tvHello.setText("Hello, " + currentUserId);
    }

    private void setupListeners() {
        fabAddExpense.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, AddExpenseActivity.class))
        );
        btnGoToBudget.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, BudgetActivity.class))
        );
    }

    private void setupRecyclerView() {
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        // Initialize adapter with a null cursor. The cursor will be loaded in onResume.
        expenseAdapter = new ExpenseAdapter(this, null);
        rvExpenses.setAdapter(expenseAdapter);
    }

    private void loadDashboardSummary() {
        // Get current month/year
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.US);
        tvCurrentMonth.setText("Overview " + sdf.format(calendar.getTime()));

        // Calculate totals
        double totalSpending = dbHelper.getTotalSpendingForCurrentMonth(currentUserId);
        double totalBudget = dbHelper.getTotalBudget(currentUserId);
        double remaining = totalBudget - totalSpending;

        // Display totals
        tvTotalSpending.setText(String.format(Locale.US, "Expense: %.0f $", totalSpending));
        tvTotalBudget.setText(String.format(Locale.US, "USD: %.0f $/Mo", totalBudget));
        tvRemainingBudget.setText(String.format(Locale.US, "Remaining: %.0f $", remaining));

        // Change color if remaining is negative
        if (remaining < 0) {
            tvRemainingBudget.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvRemainingBudget.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    private void loadExpenseList() {
        // Get new data from DB and update adapter
        Cursor newCursor = dbHelper.getExpenses(currentUserId);
        expenseAdapter.swapCursor(newCursor);
    }
}