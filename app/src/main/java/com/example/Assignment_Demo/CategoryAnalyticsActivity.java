package com.example.Assignment_Demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList; // Import this
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class CategoryAnalyticsActivity extends AppCompatActivity {

    private LinearLayout container;
    private DatabaseHelper dbHelper;
    private int currentUserId;

    private final String[] CATEGORIES = {"Food", "Transportation", "Rent", "Education", "Entertainment", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_analytics);

        View btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        dbHelper = new DatabaseHelper(this);
        container = findViewById(R.id.llCategoryContainer);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            currentUserId = prefs.getInt("USER_ID", -1);
        }
        loadCategoryData();
    }

    private void loadCategoryData() {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (String category : CATEGORIES) {
            double spent = dbHelper.getTotalSpendingForCategory(currentUserId, category);
            double budget = dbHelper.getBudget(currentUserId, category);

            if (budget > 0 || spent > 0) {
                View rowView = inflater.inflate(R.layout.item_category_status, container, false);

                ImageView iconView = rowView.findViewById(R.id.imgCategoryIcon);
                TextView tvName = rowView.findViewById(R.id.tvCategoryName);
                TextView tvStats = rowView.findViewById(R.id.tvCategoryStats);
                ProgressBar progressBar = rowView.findViewById(R.id.progressBarBudget);
                TextView tvAlert = rowView.findViewById(R.id.tvAlert);

                tvName.setText(category);
                tvStats.setText(String.format(Locale.US, "$%.0f / $%.0f", spent, budget));
                iconView.setImageResource(DatabaseHelper.getCategoryIcon(category));

                if (budget > 0) {
                    int progress = (int) ((spent / budget) * 100);
                    progressBar.setProgress(progress);

                    // --- NEW COLOR LOGIC START ---
                    if (progress >= 100) {
                        // 1. RED: Over Budget
                        progressBar.setProgressTintList(ColorStateList.valueOf(Color.RED));

                        tvAlert.setVisibility(View.VISIBLE);
                        tvAlert.setText("⚠ Over Budget!");
                        tvAlert.setTextColor(Color.RED);

                    } else if (progress >= 80) {
                        // 2. YELLOW: Caution (Between 80% and 99%)
                        // Using Amber (#FFC107) because standard Yellow is hard to read on white
                        int warningColor = Color.parseColor("#FFC107");
                        progressBar.setProgressTintList(ColorStateList.valueOf(warningColor));

                        // Show a warning text
                        tvAlert.setVisibility(View.VISIBLE);
                        tvAlert.setText("⚠ Approaching Limit");
                        tvAlert.setTextColor(Color.parseColor("#FF8F00")); // Darker Orange for text readability

                    } else {
                        // 3. GREEN: OK (Under 80%)
                        int safeColor = Color.parseColor("#4CAF50");
                        progressBar.setProgressTintList(ColorStateList.valueOf(safeColor));

                        // Hide alert
                        tvAlert.setVisibility(View.GONE);
                    }
                    // --- NEW COLOR LOGIC END ---

                } else {
                    // No budget set, but money spent
                    progressBar.setProgress(0);
                    // Use a neutral color (e.g., Grey) if no budget is set
                    progressBar.setProgressTintList(ColorStateList.valueOf(Color.GRAY));
                    tvStats.setText(String.format(Locale.US, "$%.0f (No Budget)", spent));
                    tvAlert.setVisibility(View.GONE);
                }

                container.addView(rowView);
            }
        }
    }
}