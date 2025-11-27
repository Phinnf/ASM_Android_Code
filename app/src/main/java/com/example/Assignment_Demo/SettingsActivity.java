package com.example.Assignment_Demo;

import android.content.Intent;
import android.content.SharedPreferences; // 1. Nhớ import cái này
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SettingsActivity extends AppCompatActivity {

    private int currentUserId;
    private DatabaseHelper dbHelper;

    // Các View
    private TextView txtName, txtEmail;
    private ImageView btnBack;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAddExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 1. Khởi tạo Database (Dùng Singleton chuẩn)
        dbHelper = DatabaseHelper.getInstance(this);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        loadUserData();
        setupMenuActions();
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_setting);
        }
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserData() {
        Cursor cursor = null;
        try {
            cursor = dbHelper.getUserDetails(currentUserId);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(DatabaseHelper.COL_USER_FULLNAME);
                int emailIndex = cursor.getColumnIndex(DatabaseHelper.COL_USER_EMAIL);

                if (nameIndex != -1 && emailIndex != -1) {
                    String fullName = cursor.getString(nameIndex);
                    String email = cursor.getString(emailIndex);
                    txtName.setText(fullName);
                    txtEmail.setText(email);
                }
            } else {
                txtName.setText("User not found");
                txtEmail.setText("No Email");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    private void setupMenuActions() {
        setupSingleMenuItem(R.id.menuEdit, "Edit Profile", R.drawable.ic_profile, v -> {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        setupSingleMenuItem(R.id.menuHelp, "Help & Support", R.drawable.ic_help, v -> {
            Toast.makeText(SettingsActivity.this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        setupSingleMenuItem(R.id.menuLogout, "Logout", R.drawable.ic_logout, v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Xóa trạng thái Remember
            editor.remove("USER_ID");
            editor.remove("REMEMBER_ME_CHECKED");

            editor.apply();

            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupSingleMenuItem(int includeId, String title, int iconResId, View.OnClickListener onClick) {
        View itemView = findViewById(includeId);
        if (itemView != null) {
            TextView txtOption = itemView.findViewById(R.id.txtOptionName);
            ImageView imgIcon = itemView.findViewById(R.id.imgIcon);

            if (txtOption != null) txtOption.setText(title);
            if (imgIcon != null) imgIcon.setImageResource(iconResId);

            itemView.setOnClickListener(onClick);
        }
    }

    private void setupNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabAddExpense = findViewById(R.id.fabAddExpense);

        fabAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AddExpenseActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        MenuNavigation.setupBottomNavigation(bottomNavigationView, this, currentUserId, R.id.nav_setting);
    }
}