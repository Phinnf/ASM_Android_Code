package com.example.Assignment_Demo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etName, etPassword;
    private Button btnUpdate,  btnDelete;

    private SwitchMaterial switchDarkMode;
    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Initialize DB and Views
        dbHelper = new DatabaseHelper(this);
        etEmail = findViewById(R.id.etProfileEmail);
        etName = findViewById(R.id.etProfileName);
        etPassword = findViewById(R.id.etProfilePassword);
        btnUpdate = findViewById(R.id.btnUpdateProfile);
        btnDelete = findViewById(R.id.btnDeleteAccount);
        switchDarkMode = findViewById(R.id.switchDarkMode);

        // 2. Get User ID from SharedPreferences (Assuming you saved it during Login)
        sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt("USER_ID", -1);

        // Handle Dark Mode State
        boolean isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false);
        switchDarkMode.setChecked(isDarkMode);

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close activity
            return;
        }

        // 3. Load User Data
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

        // 6. Delete Account Logic (with confirmation)
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure? This will delete all your expenses, budgets, and notes permanently.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        boolean deleted = dbHelper.deleteAccount(currentUserId);
                        if (deleted) {
                            Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show();
                            // Clear session and close
                            sharedPreferences.edit().clear().apply();
                            finish();
                        } else {
                            Toast.makeText(this, "Error Deleting Account", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        View btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserData() {
        Cursor cursor = dbHelper.getUserDetails(currentUserId);
        if (cursor.moveToFirst()) {
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_EMAIL));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_FULLNAME));
            String pass = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_PASSWORD));

            etEmail.setText(email); // Disabled in XML, so read-only
            etName.setText(name);
            etPassword.setText(pass);
        }
        cursor.close();
    }
}