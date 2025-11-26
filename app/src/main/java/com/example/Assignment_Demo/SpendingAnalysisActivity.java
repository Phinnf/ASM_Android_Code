package com.example.Assignment_Demo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class SpendingAnalysisActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int currentUserId;

    private TextView tvGrade, tvGradeComment, tvTotalSpent, tvTotalBudget, tvAdvice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spending_analysis);

        // Init Views
        View btnBack = findViewById(R.id.btn_back);
        tvGrade = findViewById(R.id.tvGrade);
        tvGradeComment = findViewById(R.id.tvGradeComment);
        tvTotalSpent = findViewById(R.id.tvTotalSpent);
        tvTotalBudget = findViewById(R.id.tvTotalBudget);
        tvAdvice = findViewById(R.id.tvAdvice);

        dbHelper = new DatabaseHelper(this);
        currentUserId = getIntent().getIntExtra("USER_ID", -1);

        calculateGrade();

        btnBack.setOnClickListener(v -> finish());
    }

    private void calculateGrade() {
        double totalSpent = dbHelper.getTotalSpendingForCurrentMonth(currentUserId);
        double totalBudget = dbHelper.getTotalBudget(currentUserId);

        // Display raw numbers
        tvTotalSpent.setText(String.format(Locale.US, "$%.0f", totalSpent));
        tvTotalBudget.setText(String.format(Locale.US, "$%.0f", totalBudget));

        if (totalBudget == 0) {
            tvGrade.setText("?");
            tvGrade.setTextColor(Color.GRAY);
            tvGradeComment.setText("No Budget Set");
            tvAdvice.setText("You haven't set a budget yet. Go to the Budget section to set goals for your categories.");
            return;
        }

        // Calculate Percentage Used
        double percentage = (totalSpent / totalBudget) * 100;

        // --- GRADING LOGIC ---
        if (percentage >= 100) {
            // F Grade
            setGrade("F", "#D32F2F", "Over Budget!",
                    "You have exceeded your total budget. Review your categories to see where you can cut costs next month.");
        }
        else if (percentage >= 90) {
            // C Grade
            setGrade("C", "#FFA000", "Caution",
                    "You are very close to your limit. Try to avoid any non-essential spending for the rest of the month.");
        }
        else if (percentage >= 75) {
            // B Grade
            setGrade("B", "#1976D2", "Good",
                    "You are on track, but keep an eye on your expenses. You have used a significant portion of your budget.");
        }
        else {
            // A Grade
            setGrade("A", "#2E7D32", "Excellent",
                    "Great job! You are well under budget. You are managing your finances perfectly.");
        }
    }

    private void setGrade(String grade, String colorHex, String comment, String advice) {
        tvGrade.setText(grade);
        tvGrade.setTextColor(Color.parseColor(colorHex));
        tvGradeComment.setText(comment);
        tvGradeComment.setTextColor(Color.parseColor(colorHex));
        tvAdvice.setText(advice);
    }
}