package com.example.Assignment_Demo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private int currentUserId;
    private DatabaseHelper dbHelper;
    private ExpenseAdapter expenseAdapter;

    private TextView tvCurrentMonth, tvTotalSpending, tvTotalBudget, tvRemainingBudget, tvProgressLabel;
    private LinearProgressIndicator progressBar;
    private RecyclerView rvExpenses;
    private FloatingActionButton fabAddExpense;
    private Button btnGoToBudget;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeViews();
        setupListeners();
        setupRecyclerView();
    }

    private void initializeViews() {
        rvExpenses = findViewById(R.id.rvExpenses);
        fabAddExpense = findViewById(R.id.fabAddExpense);
        btnGoToBudget = findViewById(R.id.btnGoToBudget);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Views inside the Include layout
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth);
        tvTotalSpending = findViewById(R.id.tvTotalSpending);
        tvTotalBudget = findViewById(R.id.tvTotalBudget);
        tvRemainingBudget = findViewById(R.id.tvRemainingBudget);
        tvProgressLabel = findViewById(R.id.tvProgressLabel);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        fabAddExpense.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, AddExpenseActivity.class))
        );

        btnGoToBudget.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, BudgetActivity.class))
        );

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_chart) {
                startActivity(new Intent(DashboardActivity.this, ChartActivity.class));
                return true;
            } else if (itemId == R.id.nav_layers) {
                Intent intent = new Intent(DashboardActivity.this, LayerActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void setupRecyclerView() {
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        expenseAdapter = new ExpenseAdapter(this, null);
        rvExpenses.setAdapter(expenseAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardSummary();
        loadExpenseList();
    }

    private void loadDashboardSummary() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.US);
        tvCurrentMonth.setText(sdf.format(calendar.getTime()));

        double totalSpending = dbHelper.getTotalSpendingForCurrentMonth(currentUserId);
        double totalBudget = dbHelper.getTotalBudget(currentUserId);
        double remaining = totalBudget - totalSpending;

        // 1. Format Currency nicely (e.g., $1,250 instead of 1250.0)
        tvTotalSpending.setText(String.format(Locale.US, "$%,.0f", totalSpending));
        tvTotalBudget.setText(String.format(Locale.US, "$%,.0f", totalBudget));
        tvRemainingBudget.setText(String.format(Locale.US, "$%,.0f", remaining));

        // 2. Logic for Progress Bar
        int progress = 0;
        if (totalBudget > 0) {
            progress = (int) ((totalSpending / totalBudget) * 100);
        }

        // 3. Set Progress with animation
        progressBar.setProgressCompat(progress, true);
        tvProgressLabel.setText(progress + "%");

        // 4. Color Logic:
        if (remaining < 0) {
            // Use Android default RED if over budget (since you didn't provide a red in colors.xml)
            tvRemainingBudget.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
        } else {
            // Use your "white" color if under budget (to look good on the Green card)
            tvRemainingBudget.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    private void loadExpenseList() {
        Cursor newCursor = dbHelper.getExpenses(currentUserId);
        if(expenseAdapter != null) {
            expenseAdapter.swapCursor(newCursor);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (expenseAdapter != null) {
            expenseAdapter.swapCursor(null);
        }
    }
}