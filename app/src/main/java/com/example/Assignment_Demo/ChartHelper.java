package com.example.Assignment_Demo;

import android.content.Context;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ChartHelper {

    public static void loadChart(
            Context context,
            BarChart chartExpense,
            String selectedTab,
            int currentUserId,
            DatabaseHelper dbHelper
    ) {

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        switch (selectedTab) {

            case "Daily":
                double todayExpense = dbHelper.getTotalSpendingForDate(
                        currentUserId,
                        new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                                .format(Calendar.getInstance().getTime())
                );

                entries.add(new BarEntry(0, (float) todayExpense));
                labels.add("Today");
                break;

            case "Weekly":
                ArrayList<Double> weekExpenses = dbHelper.getTotalSpendingForWeek(currentUserId);
                String[] dayLabels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

                for (int i = 0; i < weekExpenses.size(); i++) {
                    entries.add(new BarEntry(i, weekExpenses.get(i).floatValue()));
                    labels.add(dayLabels[i]);
                }
                break;

            case "Monthly":
                ArrayList<Double> monthExpenses = dbHelper.getTotalSpendingForMonth(currentUserId);
                for (int i = 0; i < monthExpenses.size(); i++) {
                    entries.add(new BarEntry(i, monthExpenses.get(i).floatValue()));
                    labels.add(String.valueOf(i + 1));
                }
                break;

            case "Year":
                ArrayList<Double> yearExpenses = dbHelper.getTotalSpendingForYear(currentUserId);
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

                for (int i = 0; i < yearExpenses.size(); i++) {
                    entries.add(new BarEntry(i, yearExpenses.get(i).floatValue()));
                    labels.add(months[i]);
                }
                break;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Expenses");
        dataSet.setColor(ContextCompat.getColor(context, R.color.primary_dark));
        dataSet.setValueTextColor(context.getResources().getColor(android.R.color.black));
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        chartExpense.setData(barData);

        XAxis xAxis = chartExpense.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        chartExpense.getAxisLeft().setDrawGridLines(false);
        chartExpense.getAxisRight().setEnabled(false);
        chartExpense.getLegend().setEnabled(false);
        chartExpense.getDescription().setEnabled(false);

        chartExpense.animateY(1000);
        chartExpense.invalidate();
    }
}
