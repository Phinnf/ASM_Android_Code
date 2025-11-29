package com.example.Assignment_Demo;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private DatabaseHelper dbHelper;
    private int currentUserId;
    private HistoryAdapter adapter;

    // We need TWO lists
    private List<HistoryItem> masterList; // Holds EVERYTHING from DB
    private List<HistoryItem> displayList; // Holds what is currently shown

    private Button btnAll, btnDaily, btnWeekly, btnMonthly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        dbHelper = DatabaseHelper.getInstance(this);

        initViews();
        loadDataFromDB(); // 1. Load data
        setupFilterListeners(); // 2. Setup buttons
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        btnAll = findViewById(R.id.btnAll);
        btnDaily = findViewById(R.id.btnDaily);
        btnWeekly = findViewById(R.id.btnWeekly);
        btnMonthly = findViewById(R.id.btnMonthly);
    }

    private void loadDataFromDB() {
        masterList = new ArrayList<>();
        displayList = new ArrayList<>(); // Initialize display list

        Cursor cursor = dbHelper.getExpenses(currentUserId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPENSE_DESC));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPENSE_CATEGORY));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPENSE_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXPENSE_DATE));

                masterList.add(new HistoryItem(desc, category, date, amount));
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Initially, show everything
        displayList.addAll(masterList);
        adapter = new HistoryAdapter(displayList);
        rvHistory.setAdapter(adapter);
    }

    private void setupFilterListeners() {
        btnAll.setOnClickListener(v -> filterList("ALL"));
        btnDaily.setOnClickListener(v -> filterList("DAILY"));
        btnWeekly.setOnClickListener(v -> filterList("WEEKLY"));
        btnMonthly.setOnClickListener(v -> filterList("MONTHLY"));
    }

    private void filterList(String type) {
        // 1. Highlight the selected button visually
        updateButtonStyles(type);

        // 2. Clear current display
        displayList.clear();

        // 3. Get current dates for comparison
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat sdfMonth = new SimpleDateFormat("yyyy-MM", Locale.US);

        String todayStr = sdfDay.format(cal.getTime());
        String thisMonthStr = sdfMonth.format(cal.getTime());
        int thisWeek = cal.get(Calendar.WEEK_OF_YEAR);
        int thisYear = cal.get(Calendar.YEAR);

        // 4. Loop through master list and pick matches
        for (HistoryItem item : masterList) {
            boolean isMatch = false;

            try {
                switch (type) {
                    case "ALL":
                        isMatch = true;
                        break;
                    case "DAILY":
                        if (item.date.equals(todayStr)) isMatch = true;
                        break;
                    case "MONTHLY":
                        // Check if "2023-11" matches "2023-11"
                        if (item.date.startsWith(thisMonthStr)) isMatch = true;
                        break;
                    case "WEEKLY":
                        // Convert item date string to Calendar object
                        Date dateObj = sdfDay.parse(item.date);
                        Calendar itemCal = Calendar.getInstance();
                        itemCal.setTime(dateObj);

                        // Check if it's the same week number AND same year
                        if (itemCal.get(Calendar.WEEK_OF_YEAR) == thisWeek &&
                                itemCal.get(Calendar.YEAR) == thisYear) {
                            isMatch = true;
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (isMatch) {
                displayList.add(item);
            }
        }

        // 5. Tell adapter data changed
        adapter.notifyDataSetChanged();
    }

    private void updateButtonStyles(String activeType) {
        // Reset all to gray
        int colorInactive = ContextCompat.getColor(this, R.color.textColorSecondary);
        int colorActive = ContextCompat.getColor(this, R.color.colorPrimaryDark);

        btnAll.setTextColor(colorInactive);
        btnDaily.setTextColor(colorInactive);
        btnWeekly.setTextColor(colorInactive);
        btnMonthly.setTextColor(colorInactive);

        // Set active one to primary color
        switch (activeType) {
            case "ALL": btnAll.setTextColor(colorActive); break;
            case "DAILY": btnDaily.setTextColor(colorActive); break;
            case "WEEKLY": btnWeekly.setTextColor(colorActive); break;
            case "MONTHLY": btnMonthly.setTextColor(colorActive); break;
        }
    }

    // --- ADAPTER (Same as before) ---
    class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
        List<HistoryItem> list;

        public HistoryAdapter(List<HistoryItem> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
            return new HistoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
            HistoryItem item = list.get(position);
            holder.tvDesc.setText(item.description);
            holder.tvCategoryDate.setText(item.category + " • " + item.date);
            holder.tvAmount.setText(String.format("-$%.2f", item.amount));
            holder.imgIcon.setImageResource(DatabaseHelper.getCategoryIcon(item.category));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class HistoryViewHolder extends RecyclerView.ViewHolder {
            TextView tvDesc, tvCategoryDate, tvAmount;
            ImageView imgIcon;

            public HistoryViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDesc = itemView.findViewById(R.id.tvDescription);
                tvCategoryDate = itemView.findViewById(R.id.tvCategoryDate);
                tvAmount = itemView.findViewById(R.id.tvAmount);
                imgIcon = itemView.findViewById(R.id.imgCategory);
            }
        }
    }
    public class HistoryItem {
        // These are the variables that hold data for one row
        public String description;
        public String category;
        public String date;
        public double amount;

        // This is the constructor
        public HistoryItem(String description, String category, String date, double amount) {
            this.description = description;
            this.category = category;
            this.date = date;
            this.amount = amount;
        }
    }
}