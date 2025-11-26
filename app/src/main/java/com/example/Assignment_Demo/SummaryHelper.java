package com.example.Assignment_Demo;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.Locale;

public class SummaryHelper {

    public static void updateSummary(View include, double totalBudget, double totalSpending, Context context) {
        TextView tvTotalBudget = include.findViewById(R.id.tvTotalBudget);
        TextView tvTotalSpending = include.findViewById(R.id.tvTotalSpending);
        TextView tvRemainingBudget = include.findViewById(R.id.tvRemainingBudget);
        TextView tvProgressLabel = include.findViewById(R.id.tvProgressLabel);
        LinearProgressIndicator progressBar = include.findViewById(R.id.progressBar);

        double remaining = totalBudget - totalSpending;

        tvTotalBudget.setText(String.format(Locale.US, "$%.0f", totalBudget));
        tvTotalSpending.setText(String.format(Locale.US, "$%.0f", totalSpending));
        tvRemainingBudget.setText(String.format(Locale.US, "$%.0f", remaining));

        tvRemainingBudget.setTextColor(ContextCompat.getColor(
                context,
                remaining < 0 ? android.R.color.holo_red_dark : android.R.color.white
        ));

        int progress = totalBudget == 0 ? 0 : (int) ((totalSpending / totalBudget) * 100);
        progressBar.setProgressCompat(progress, true);

        tvProgressLabel.setText(progress + "%");
    }
}
