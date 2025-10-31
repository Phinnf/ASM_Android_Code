package com.example.Assignment_Demo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {
    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvGoToRegister;
    DatabaseHelper dbHelper;
    SharedPreferences sharedPreferences;

    public static final String PREFS_NAME = "UserPrefs";
    public static final String KEY_USER_ID = "USER_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Changed layout

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if already logged in, go straight to Dashboard
        if(sharedPreferences.getInt(KEY_USER_ID, -1) != -1){
            goToDashboard(-1); // -1 signals no need to putExtra
        }

        etUsername = findViewById(R.id.etLoginUsername);
        etPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> login());
        tvGoToRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class))
        );
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = dbHelper.checkUser(username, password);

        if (userId != -1) {
            // Login successful, save User ID to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_USER_ID, userId);
            editor.apply();

            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            goToDashboard(userId);
        } else {
            Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToDashboard(int userId){
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        if(userId != -1){
            intent.putExtra("USER_ID", userId);
        }
        startActivity(intent);
        finish(); // Close LoginActivity
    }

}