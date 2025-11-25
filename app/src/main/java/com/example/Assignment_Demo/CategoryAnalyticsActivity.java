package com.example.Assignment_Demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView; // IMPORT THIS
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class CategoryAnalyticsActivity extends AppCompatActivity {

    private LinearLayout container;
    private DatabaseHelper dbHelper;
    private int currentUserId;

    // Hardcoded categories to match your other files
    private final String[] CATEGORIES = {"Food", "Transportation", "Rent", "Education", "Entertainment", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_analytics);

        View btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);
        container = findViewById(R.id.llCategoryContainer);

        // Get User ID
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            currentUserId = prefs.getInt("USER_ID", -1);
        }
        loadCategoryData();
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadCategoryData() {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (String category : CATEGORIES) {
            // 1. Get Data from DB
            double spent = dbHelper.getTotalSpendingForCategory(currentUserId, category);
            double budget = dbHelper.getBudget(currentUserId, category);

            // Only show if there is a budget OR spending exists
            if (budget > 0 || spent > 0) {

                // 2. Inflate the row layout
                View rowView = inflater.inflate(R.layout.item_category_status, container, false);

                // 3. Bind Views
                // --- FIX START: Bind the ImageView ---
                ImageView iconView = rowView.findViewById(R.id.imgCategoryIcon);
                // --- FIX END ---

                TextView tvName = rowView.findViewById(R.id.tvCategoryName);
                TextView tvStats = rowView.findViewById(R.id.tvCategoryStats);
                ProgressBar progressBar = rowView.findViewById(R.id.progressBarBudget);
                TextView tvAlert = rowView.findViewById(R.id.tvAlert);

                // 4. Set Logic
                tvName.setText(category);
                tvStats.setText(String.format(Locale.US, "$%.0f / $%.0f", spent, budget));

                // --- FIX START: Set the correct icon ---
                iconView.setImageResource(DatabaseHelper.getCategoryIcon(category));
                // --- FIX END ---

                if (budget > 0) {
                    int progress = (int) ((spent / budget) * 100);
                    progressBar.setProgress(progress);

                    // Change color if over budget
                    if (progress > 100) {
                        progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.RED));
                        tvAlert.setVisibility(View.VISIBLE);
                    } else {
                        progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Green
                        tvAlert.setVisibility(View.GONE);
                    }
                } else {
                    // No budget set, but money spent
                    progressBar.setProgress(0);
                    tvStats.setText(String.format(Locale.US, "$%.0f (Not spent)", spent));
                }

                // 5. Add row to the main container
                container.addView(rowView);
            }
        }
    }
}