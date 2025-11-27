package com.example.Assignment_Demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etName, etPassword;
    private Button btnUpdate, btnDelete;
    private View btnBack; // Thêm biến cho nút Back

    private SwitchMaterial switchDarkMode;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        // --- FIX 1: Dùng Singleton để tránh Leak Database ---
        dbHelper = DatabaseHelper.getInstance(this);

        // Init Views
        etEmail = findViewById(R.id.etProfileEmail);
        etName = findViewById(R.id.etProfileName);
        etPassword = findViewById(R.id.etProfilePassword);
        btnUpdate = findViewById(R.id.btnUpdateProfile);
        btnDelete = findViewById(R.id.btnDeleteAccount);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        btnBack = findViewById(R.id.btn_back);

        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // --- FIX 2: Ưu tiên lấy ID từ Intent (do Settings gửi sang), nếu không có mới lấy SharedPrefs ---
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            currentUserId = sharedPreferences.getInt("USER_ID", -1);
        }

        // Handle Dark Mode State
        boolean isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false);
        switchDarkMode.setChecked(isDarkMode);

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3. Load User Data (Safe Mode)
        loadUserData();

        // 4. Update Profile Button Logic
        btnUpdate.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newPass = etPassword.getText().toString().trim();

            if (newName.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.updateUserProfile(currentUserId, newName, newPass);
                if (success) {
                    Toast.makeText(ProfileActivity.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 5. Dark Mode Toggle Logic
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("DARK_MODE", true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("DARK_MODE", false);
            }
            editor.apply();
        });

        // 6. Delete Account Logic
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure? This will delete all your expenses, budgets, and notes permanently.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        boolean deleted = dbHelper.deleteAccount(currentUserId);
                        if (deleted) {
                            Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show();
                            sharedPreferences.edit().clear().apply();

                            // Chuyển về màn hình Login và xóa hết Activity cũ
                            // (Tránh trường hợp user bấm Back lại vào được màn hình cũ)
                            finishAffinity();
                        } else {
                            Toast.makeText(this, "Error Deleting Account", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Back Button
        btnBack.setOnClickListener(v -> finish());
    }

    // --- FIX 3: Cursor Management An Toàn (Chống Crash) ---
    private void loadUserData() {
        Cursor cursor = null;
        try {
            cursor = dbHelper.getUserDetails(currentUserId);

            if (cursor != null && cursor.moveToFirst()) {
                // Lấy index an toàn (tránh lỗi cột không tồn tại)
                int emailIdx = cursor.getColumnIndex(DatabaseHelper.COL_USER_EMAIL);
                int nameIdx = cursor.getColumnIndex(DatabaseHelper.COL_USER_FULLNAME);
                int passIdx = cursor.getColumnIndex(DatabaseHelper.COL_USER_PASSWORD);

                if (emailIdx != -1 && nameIdx != -1 && passIdx != -1) {
                    String email = cursor.getString(emailIdx);
                    String name = cursor.getString(nameIdx);
                    String pass = cursor.getString(passIdx);

                    etEmail.setText(email);
                    etName.setText(name);
                    etPassword.setText(pass);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải thông tin user", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }
}