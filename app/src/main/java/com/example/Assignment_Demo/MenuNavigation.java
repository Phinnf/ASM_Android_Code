package com.example.Assignment_Demo;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuNavigation {

    public static void setupBottomNavigation(BottomNavigationView bottomNav, Activity activity, int currentUserId, int selectedItemId) {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == selectedItemId) return true;

            Intent intent = null;

            if (id == R.id.nav_home) {
                intent = new Intent(activity, DashboardActivity.class);
            } else if (id == R.id.nav_chart) {
                intent = new Intent(activity, ChartActivity.class);
            } else if (id == R.id.nav_layers) {
                intent = new Intent(activity, LayerActivity.class);
            } else if (id == R.id.nav_setting) {
                intent = new Intent(activity, SettingsActivity.class);
            }

            if (intent != null) {
                intent.putExtra("USER_ID", currentUserId);
                activity.startActivity(intent);
                activity.finish();
            }

            return true;
        });

        bottomNav.setSelectedItemId(selectedItemId);
    }
}
