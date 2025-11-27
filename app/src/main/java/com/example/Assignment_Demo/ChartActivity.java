package com.example.Assignment_Demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ChartActivity extends AppCompatActivity {

    private int currentUserId;
    private FloatingActionButton fabAddExpense;
    private Button btnGoToBudget;


    private DatabaseHelper dbHelper;
    private View summaryInclude;
    private TextView tvIncome, tvExpense;
    private CircularProgressIndicator progressTravel, progressCar;
    private ImageView btnBack;
    private BarChart chartExpense;

    private TextView tabDaily, tabWeekly, tabMonthly, tabYear;
    private BottomNavigationView bottomNavigationView;

    private String selectedTab = "Weekly";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        dbHelper = new DatabaseHelper(this);
        currentUserId = getIntent().getIntExtra("USER_ID", -1);

        initViews();
        loadData();
        setupChart();
        setupEvents();
        setupBottomNavigation();
    }

    private void initViews() {
        summaryInclude = findViewById(R.id.includeSummary);

        tvIncome = findViewById(R.id.tv_income);
        tvExpense = findViewById(R.id.tv_expense2);

        progressTravel = findViewById(R.id.progress_travel);
        progressCar = findViewById(R.id.progress_car);
        fabAddExpense = findViewById(R.id.fabAddExpense);


        btnBack = findViewById(R.id.btn_back);
        btnGoToBudget = findViewById(R.id.btnGoToBudget);
//        btnNotify = findViewById(R.id.btn_notify);
        chartExpense = findViewById(R.id.chart_expense);

        tabDaily = findViewById(R.id.tab_daily);
        tabWeekly = findViewById(R.id.tab_weekly);
        tabMonthly = findViewById(R.id.tab_monthly);
        tabYear = findViewById(R.id.tab_year);
    }

    private void loadData() {
        double totalBudget = dbHelper.getTotalBudget(currentUserId);
        double totalSpending = dbHelper.getTotalSpendingForCurrentMonth(currentUserId);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.US);
        TextView tvCurrentMonth = summaryInclude.findViewById(R.id.tvCurrentMonth);
        tvCurrentMonth.setText(sdf.format(calendar.getTime()));

        // Update summary include
        SummaryHelper.updateSummary(summaryInclude, totalBudget, totalSpending, this);

        // Update progress circular
        double spendFood = dbHelper.getTotalSpendingForCategory(currentUserId, "Food");
        double spendTransport = dbHelper.getTotalSpendingForCategory(currentUserId, "Transportation");

        progressTravel.setProgress((int) Math.min((spendFood / totalBudget) * 100, 100));
        progressCar.setProgress((int) Math.min((spendTransport / totalBudget) * 100, 100));

        tvIncome.setText(String.format(Locale.US, "$%.2f", totalBudget));
        tvExpense.setText(String.format(Locale.US, "$%.2f", totalSpending));
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


        View.OnClickListener tabListener = view -> {
            updateTabUI((TextView) view);
            if (view == tabDaily) selectedTab = "Daily";
            else if (view == tabWeekly) selectedTab = "Weekly";
            else if (view == tabMonthly) selectedTab = "Monthly";
            else if (view == tabYear) selectedTab = "Year";
            setupChart();
        };

        tabDaily.setOnClickListener(tabListener);
        tabWeekly.setOnClickListener(tabListener);
        tabMonthly.setOnClickListener(tabListener);
        tabYear.setOnClickListener(tabListener);
    }

    private void updateTabUI(TextView selectedTabView) {
        resetTabStyle(tabDaily);
        resetTabStyle(tabWeekly);
        resetTabStyle(tabMonthly);
        resetTabStyle(tabYear);

        selectedTabView.setBackgroundResource(R.drawable.tab_selected);
        selectedTabView.setTextColor(ContextCompat.getColor(this, R.color.primary_dark));
    }

    private void resetTabStyle(TextView tab) {
        tab.setBackgroundResource(0);
        tab.setTextColor(ContextCompat.getColor(this, R.color.gray_text));
    }

    private void setupChart() {
        ChartHelper.loadChart(
                this,
                chartExpense,
                selectedTab,
                currentUserId,
                dbHelper
        );
    }

    private void setupBottomNavigation() {

        fabAddExpense.setOnClickListener(v ->
                startActivity(new Intent(this, AddExpenseActivity.class))
        );

        btnGoToBudget.setOnClickListener(v ->
                startActivity(new Intent(this, BudgetActivity.class))
        );

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                finish();
            } else if (itemId == R.id.nav_layers) {
                Intent intent = new Intent(this, LayerActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
            } else if (itemId == R.id.nav_setting) {
                // Logic user
            }
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_chart);
    }
}
