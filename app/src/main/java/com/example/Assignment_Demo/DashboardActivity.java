package com.example.Assignment_Demo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

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
//    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);

        // Lấy User ID từ Intent
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "User ID not found in Intent", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initializeViews();
        setupListeners();
        setupRecyclerView();
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
//        bottomNavigationView = findViewById(R.id.bottom_navigation);

        tvHello.setText("Hello, " + currentUserId);
    }

    private void setupListeners() {
        // FloatingActionButton để thêm Expense
        fabAddExpense.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, AddExpenseActivity.class))
        );

        // Button đi đến Budget
        btnGoToBudget.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, BudgetActivity.class))
        );

        // BottomNavigationView listener
//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull android.view.MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.nav_home:
//                        Toast.makeText(DashboardActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.nav_chart:
//                        Toast.makeText(DashboardActivity.this, "Chart clicked", Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.nav_layers:
//                        Toast.makeText(DashboardActivity.this, "Layers clicked", Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.nav_user:
//                        Toast.makeText(DashboardActivity.this, "User clicked", Toast.LENGTH_SHORT).show();
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//        });
    }

    private void setupRecyclerView() {
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        expenseAdapter = new ExpenseAdapter(this, null); // khởi tạo với cursor null
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
        tvCurrentMonth.setText("Overview " + sdf.format(calendar.getTime()));

        double totalSpending = dbHelper.getTotalSpendingForCurrentMonth(currentUserId);
        double totalBudget = dbHelper.getTotalBudget(currentUserId);
        double remaining = totalBudget - totalSpending;

        tvTotalSpending.setText(String.format(Locale.US, "Expense: %.0f $", totalSpending));
        tvTotalBudget.setText(String.format(Locale.US, "USD: %.0f $/Mo", totalBudget));
        tvRemainingBudget.setText(String.format(Locale.US, "Remaining: %.0f $", remaining));

        tvRemainingBudget.setTextColor(getResources().getColor(
                remaining < 0 ? android.R.color.holo_red_dark : android.R.color.holo_green_dark
        ));
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
