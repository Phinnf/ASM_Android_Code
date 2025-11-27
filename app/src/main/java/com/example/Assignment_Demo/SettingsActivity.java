package com.example.Assignment_Demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private int currentUserId;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new DatabaseHelper(this);

        // 1. Get User ID passed from Dashboard
        currentUserId = getIntent().getIntExtra("USER_ID", -1);

        // 2. Initialize Views
        ImageButton btnBack = findViewById(R.id.btn_back);
        LinearLayout layoutEditProfile = findViewById(R.id.layoutEditProfile);
        LinearLayout layoutNotifications = findViewById(R.id.layoutNotifications);
        LinearLayout layoutSecurity = findViewById(R.id.layoutSecurity);
        SwitchMaterial switchDarkMode = findViewById(R.id.switchDarkMode);
        Button btnLogout = findViewById(R.id.btnSettingsLogout);

        // 3. Handle Back Button
        btnBack.setOnClickListener(v -> finish()); // Closes this activity and returns to Dashboard

        // 4. Handle Edit Profile Click
        layoutEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        // 5. Handle Dark Mode Switch
        // Check current mode to set switch state
        int currentNightMode = AppCompatDelegate.getDefaultNightMode();
        switchDarkMode.setChecked(currentNightMode == AppCompatDelegate.MODE_NIGHT_YES);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // 6. Handle Logout
        btnLogout.setOnClickListener(v -> {
            // Navigate back to LoginActivity and clear the back stack
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class); // Assuming your login screen is LoginActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Handle other clicks
        layoutNotifications.setOnClickListener(v ->
                Toast.makeText(this, "Notifications Clicked", Toast.LENGTH_SHORT).show()
        );

        layoutSecurity.setOnClickListener(v ->
                Toast.makeText(this, "Security Clicked", Toast.LENGTH_SHORT).show()
        );
    }
}