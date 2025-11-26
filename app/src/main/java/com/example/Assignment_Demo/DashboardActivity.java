package com.example.Assignment_Demo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    private int currentUserId;
    private DatabaseHelper dbHelper;
    private ExpenseAdapter expenseAdapter;

    private TextView tvHello;
    private TextView tabDaily, tabWeekly, tabMonthly, tabYear;
    private RecyclerView rvExpenses;
    private FloatingActionButton fabAddExpense;
    private Button btnGoToBudget;
    private BottomNavigationView bottomNavigationView;

    // Layout include
    private View summaryInclude;

    private BarChart chartExpense;

    private String selectedTab = "Daily";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "User ID missing", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeViews();
        setupTabListeners();
        setupOtherListeners();
        setupRecyclerView();
    }

    private void initializeViews() {
        tvHello = findViewById(R.id.tvHello);
        rvExpenses = findViewById(R.id.rvExpenses);
        fabAddExpense = findViewById(R.id.fabAddExpense);
        btnGoToBudget = findViewById(R.id.btnGoToBudget);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        summaryInclude = findViewById(R.id.includeSummary);

        String username = dbHelper.getUserName(currentUserId);
        tvHello.setText("Hello, " + username);

        chartExpense = findViewById(R.id.chartExpense);

        tabDaily = findViewById(R.id.tab_daily);
        tabWeekly = findViewById(R.id.tab_weekly);
        tabMonthly = findViewById(R.id.tab_monthly);
        tabYear = findViewById(R.id.tab_year);
    }

    private void setupTabListeners() {
        View.OnClickListener tabListener = view -> {
            resetTabStyle(tabDaily);
            resetTabStyle(tabWeekly);
            resetTabStyle(tabMonthly);
            resetTabStyle(tabYear);

            ((TextView) view).setBackgroundResource(R.drawable.tab_selected);
            ((TextView) view).setTextColor(ContextCompat.getColor(this, R.color.primary_dark));

            if (view == tabDaily) selectedTab = "Daily";
            else if (view == tabWeekly) selectedTab = "Weekly";
            else if (view == tabMonthly) selectedTab = "Monthly";
            else if (view == tabYear) selectedTab = "Year";

            ChartHelper.loadChart(this, chartExpense, selectedTab, currentUserId, dbHelper);
        };

        tabDaily.setOnClickListener(tabListener);
        tabWeekly.setOnClickListener(tabListener);
        tabMonthly.setOnClickListener(tabListener);
        tabYear.setOnClickListener(tabListener);
    }

    private void resetTabStyle(TextView tab) {
        tab.setBackgroundResource(0);
        tab.setTextColor(ContextCompat.getColor(this, R.color.gray_text));
    }

    private void setupOtherListeners() {
        fabAddExpense.setOnClickListener(v ->
                startActivity(new Intent(this, AddExpenseActivity.class))
        );

        btnGoToBudget.setOnClickListener(v ->
                startActivity(new Intent(this, BudgetActivity.class))
        );

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_chart) {
                startActivity(new Intent(this, ChartActivity.class)
                        .putExtra("USER_ID", currentUserId));
                return true;
            } else if (id == R.id.nav_layers) {
                startActivity(new Intent(this, LayerActivity.class)
                        .putExtra("USER_ID", currentUserId));
                return true;
            } else if (id == R.id.nav_user) {
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
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        loadDashboardSummary();
        loadExpenseList();
    }

    private void loadDashboardSummary() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.US);

        TextView tvCurrentMonth = summaryInclude.findViewById(R.id.tvCurrentMonth);
        tvCurrentMonth.setText(sdf.format(calendar.getTime()));

        double totalSpending = dbHelper.getTotalSpendingForCurrentMonth(currentUserId);
        double totalBudget = dbHelper.getTotalBudget(currentUserId);

        SummaryHelper.updateSummary(summaryInclude, totalBudget, totalSpending, this);

        // Load chart with selected tab
        ChartHelper.loadChart(this, chartExpense, selectedTab, currentUserId, dbHelper);

        // Update initial tab UI
        updateTabUI(selectedTab);
    }

    private void updateTabUI(String tab) {
        resetTabStyle(tabDaily);
        resetTabStyle(tabWeekly);
        resetTabStyle(tabMonthly);
        resetTabStyle(tabYear);

        switch (tab) {
            case "Daily":
                tabDaily.setBackgroundResource(R.drawable.tab_selected);
                tabDaily.setTextColor(ContextCompat.getColor(this, R.color.primary_dark));
                break;
            case "Weekly":
                tabWeekly.setBackgroundResource(R.drawable.tab_selected);
                tabWeekly.setTextColor(ContextCompat.getColor(this, R.color.primary_dark));
                break;
            case "Monthly":
                tabMonthly.setBackgroundResource(R.drawable.tab_selected);
                tabMonthly.setTextColor(ContextCompat.getColor(this, R.color.primary_dark));
                break;
            case "Year":
                tabYear.setBackgroundResource(R.drawable.tab_selected);
                tabYear.setTextColor(ContextCompat.getColor(this, R.color.primary_dark));
                break;
        }
    }

    private void loadExpenseList() {
        Cursor newCursor = dbHelper.getExpenses(currentUserId);
        expenseAdapter.swapCursor(newCursor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (expenseAdapter != null) {
            expenseAdapter.swapCursor(null);
        }
    }
}
