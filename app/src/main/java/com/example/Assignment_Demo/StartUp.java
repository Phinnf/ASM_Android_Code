package com.example.Assignment_Demo;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class StartUp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 1. Get the Shared Preferences (Use the exact same name "UserSession" as in your activity)
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // 2. Retrieve the saved boolean
        boolean isDarkMode = sharedPreferences.getBoolean("DARK_MODE", false);

        // 3. Apply the theme immediately
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}