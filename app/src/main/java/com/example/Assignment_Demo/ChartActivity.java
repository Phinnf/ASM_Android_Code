package com.example.Assignment_Demo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator; // Import thêm cái này

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {

    // 1. Khai báo biến View (Cập nhật theo XML mới)
    private TextView tvTotalBudget, tvTotalSpending, tvRemainingBudget; // Header card
    private TextView tvIncome, tvExpense; // Summary row (bên dưới chart)
    private TextView tabDaily, tabWeekly, tabMonthly, tabYear; // Tabs

    private CircularProgressIndicator progressTravel, progressCar; // Vòng tròn mục tiêu
    private LinearProgressIndicator progressBar; // Thanh ngang ở Header (MỚI)

    private ImageView btnBack, btnNotify;
    private BarChart chartExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        initViews();
        setupDummyData();
        setupChart();
        setupEvents();
    }

    private void initViews() {
        // --- Header Area (Mapping với ID mới trong XML) ---
        tvTotalBudget = findViewById(R.id.tvTotalBudget);       // ID trong XML là tvTotalBudget
        tvTotalSpending = findViewById(R.id.tvTotalSpending);   // ID trong XML là tvTotalSpending
        tvRemainingBudget = findViewById(R.id.tvRemainingBudget); // ID trong XML là tvRemainingBudget
        progressBar = findViewById(R.id.progressBar);           // ID trong XML là progressBar

        // --- Summary Row ---
        tvIncome = findViewById(R.id.tv_income);
        tvExpense = findViewById(R.id.tv_expense2);

        // --- Targets ---
        progressTravel = findViewById(R.id.progress_travel);
        progressCar = findViewById(R.id.progress_car);

        // --- Toolbar & Chart ---
        btnBack = findViewById(R.id.btn_back);
        btnNotify = findViewById(R.id.btn_notify);
        chartExpense = findViewById(R.id.chart_expense);

        // --- Tabs ---
        tabDaily = findViewById(R.id.tab_daily);
        tabWeekly = findViewById(R.id.tab_weekly);
        tabMonthly = findViewById(R.id.tab_monthly);
        tabYear = findViewById(R.id.tab_year);
    }

    private void setupDummyData() {
        // Set dữ liệu giả lập khớp với giao diện mới
        tvTotalBudget.setText("USD: 25.520 $/Mo");
        tvTotalSpending.setText("Expense: 5.000 $");
        tvRemainingBudget.setText("Remaining: 20.520 $");

        // Set thanh progress ngang (Header)
        progressBar.setProgress(25); // Ví dụ 25%

        // Set text phần summary dưới chart
        tvIncome.setText("$4,120.00");
        tvExpense.setText("$1,187.40");

        // Set vòng tròn mục tiêu
        progressTravel.setProgress(30);
        progressCar.setProgress(50);
    }

    private void setupEvents() {
        // Nút Back
        btnBack.setOnClickListener(v -> finish());

        View.OnClickListener tabListener = view -> {
            updateTabUI((TextView) view);
            // TODO: Logic load lại chart khi đổi tab
        };

        tabDaily.setOnClickListener(tabListener);
        tabWeekly.setOnClickListener(tabListener);
        tabMonthly.setOnClickListener(tabListener);
        tabYear.setOnClickListener(tabListener);
    }

    private void updateTabUI(TextView selectedTab) {
        resetTabStyle(tabDaily);
        resetTabStyle(tabWeekly);
        resetTabStyle(tabMonthly);
        resetTabStyle(tabYear);

        selectedTab.setBackgroundResource(R.drawable.tab_selected);
        selectedTab.setTextColor(ContextCompat.getColor(this, R.color.primary_dark));
    }

    private void resetTabStyle(TextView tab) {
        tab.setBackgroundResource(0); // Xóa background
        tab.setTextColor(ContextCompat.getColor(this, R.color.gray_text));
    }

    private void setupChart() {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 1200f));
        entries.add(new BarEntry(1, 1450f));
        entries.add(new BarEntry(2, 1100f));
        entries.add(new BarEntry(3, 1600f));
        entries.add(new BarEntry(4, 900f));
        entries.add(new BarEntry(5, 1300f));
        entries.add(new BarEntry(6, 1500f));

        BarDataSet dataSet = new BarDataSet(entries, "Weekly Expenses");
        dataSet.setColor(ContextCompat.getColor(this, R.color.primary_dark));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        chartExpense.setData(barData);
        chartExpense.getDescription().setEnabled(false);
        chartExpense.getLegend().setEnabled(false);
        chartExpense.setDrawGridBackground(false);

        // Trục X
        String[] days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        XAxis xAxis = chartExpense.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        chartExpense.getAxisLeft().setDrawGridLines(false);
        chartExpense.getAxisRight().setEnabled(false);
        chartExpense.animateY(1000);
        chartExpense.invalidate();
    }
}