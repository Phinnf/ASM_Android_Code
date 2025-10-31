package com.example.Assignment_Demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {

    private int currentUserId;
    private TextView tvWelcome;
    private DatabaseHelper dbHelper;

    private RecyclerView rvExpenses;
    private ExpenseAdapter expenseAdapter;
    private FloatingActionButton fabAddExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        dbHelper = new DatabaseHelper(this);
        fabAddExpense = findViewById(R.id.fabAddExpense);

        // Get User ID
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        currentUserId = prefs.getInt(LoginActivity.KEY_USER_ID, -1);
        if (currentUserId == -1) { finish(); return; }

        tvWelcome.setText("Expenses for User: " + currentUserId);

        // Setup RecyclerView
        rvExpenses = findViewById(R.id.rvExpenses);
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));

        // Get data and assign to Adapter
        Cursor cursor = dbHelper.getExpenses(currentUserId);
        expenseAdapter = new ExpenseAdapter(this, cursor);
        rvExpenses.setAdapter(expenseAdapter);

        // Handle FAB button
        fabAddExpense.setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, AddExpenseActivity.class))
        );
    }

    // Update RecyclerView when returning to this screen
    @Override
    protected void onResume() {
        super.onResume();
        // Get new data from DB and update adapter
        Cursor newCursor = dbHelper.getExpenses(currentUserId);
        expenseAdapter.swapCursor(newCursor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close cursor when Activity is destroyed
        if (expenseAdapter != null && expenseAdapter.mCursor != null) {
            expenseAdapter.mCursor.close();
        }
    }
}


