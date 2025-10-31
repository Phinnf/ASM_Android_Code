package com.example.Assignment_Demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
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

    private TextView tvWelcome, tvCurrentMonth, tvTotalSpending, tvTotalBudget, tvRemainingBudget;
    private RecyclerView rvExpenses;
    private FloatingActionButton fabAddExpense;
    private Button btnGoToBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);

        // Get User ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);
        if (currentUserId == -1) {
            // If user ID is not found, finish the activity
            finish();
            return;
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
        tvWelcome = findViewById(R.id.tvWelcome);
        rvExpenses = findViewById(R.id.rvExpenses);
        fabAddExpense = findViewById(R.id.fabAddExpense);
        btnGoToBudget = findViewById(R.id.btnGoToBudget);
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        tvTotalSpending = findViewById(R.id.tvTotalSpending);
        tvTotalBudget = findViewById(R.id.tvTotalBudget);
        tvRemainingBudget = findViewById(R.id.tvRemainingBudget);

        tvWelcome.setText("Expenses for User: " + currentUserId);
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
        tvTotalSpending.setText(String.format(Locale.US, "Total Spending: %.0fđ", totalSpending));
        tvTotalBudget.setText(String.format(Locale.US, "Total Budget: %.0fđ", totalBudget));
        tvRemainingBudget.setText(String.format(Locale.US, "Remaining: %.0fđ", remaining));

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