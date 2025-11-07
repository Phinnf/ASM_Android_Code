package com.example.Assignment_Demo;

// met cai github qua
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout tilEmailUsername, tilPassword;
    private TextInputEditText etEmailUsername, etPassword;
    private MaterialButton btnSignIn, btnSignInTab, btnSignUpTab;
    private CheckBox cbRememberMe;

    private DatabaseHelper dbHelper;
    private SharedPreferences sharedPreferences;

    public static final String PREFS_NAME = "UserPrefs";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_REMEMBER_EMAIL = "REMEMBER_EMAIL";
    public static final String KEY_REMEMBER_PASSWORD = "REMEMBER_PASSWORD";
    public static final String KEY_REMEMBER_ME_CHECKED = "REMEMBER_ME_CHECKED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // --- (FIX 1) UPDATED AUTO-LOGIN CHECK ---
        // Check if user *wanted* to be remembered
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME_CHECKED, false);
        int rememberedUserId = sharedPreferences.getInt(KEY_USER_ID, -1);

        if (rememberMe && rememberedUserId != -1) {
            // User is remembered and has a valid ID, go to Dashboard
            goToDashboard(rememberedUserId);
            return; // Skip the rest of onCreate (no need to show login)
        }

        tilEmailUsername = findViewById(R.id.til_email_username);
        etEmailUsername = findViewById(R.id.et_email_username);
        tilPassword = findViewById(R.id.til_password);
        etPassword = findViewById(R.id.et_password);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignInTab = findViewById(R.id.btn_sign_in_tab);
        btnSignUpTab = findViewById(R.id.btn_sign_up_tab);
        cbRememberMe = findViewById(R.id.cb_remember_me);

        // Load remembered credentials (if they exist)
        boolean rememberMeChecked = sharedPreferences.getBoolean(KEY_REMEMBER_ME_CHECKED, false);
        cbRememberMe.setChecked(rememberMeChecked);
        if (rememberMeChecked) {
            etEmailUsername.setText(sharedPreferences.getString(KEY_REMEMBER_EMAIL, ""));
            etPassword.setText(sharedPreferences.getString(KEY_REMEMBER_PASSWORD, ""));
        }

        btnSignIn.setOnClickListener(v -> login());

        btnSignUpTab.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        // Your tab styling logic (unchanged)
        btnSignInTab.setBackgroundTintList(getColorStateList(R.color.colorPrimary));
        btnSignInTab.setTextColor(getColorStateList(R.color.white));
        btnSignUpTab.setBackgroundTintList(getColorStateList(android.R.color.transparent));
        btnSignUpTab.setTextColor(getColorStateList(R.color.colorPrimary));
        btnSignUpTab.setStrokeColorResource(R.color.colorPrimary);
        btnSignUpTab.setStrokeWidth(1);

        btnSignInTab.setOnClickListener(v -> {
            btnSignInTab.setBackgroundTintList(getColorStateList(R.color.colorPrimary));
            btnSignInTab.setTextColor(getColorStateList(R.color.white));
            btnSignUpTab.setBackgroundTintList(getColorStateList(android.R.color.transparent));
            btnSignUpTab.setTextColor(getColorStateList(R.color.colorPrimary));
            btnSignUpTab.setStrokeColorResource(R.color.colorPrimary);
            btnSignUpTab.setStrokeWidth(1);
        });
    }

    private void login() {
        String emailUsername = etEmailUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        tilEmailUsername.setError(null);
        tilPassword.setError(null);

        boolean isValid = true;

        if (TextUtils.isEmpty(emailUsername)) {
            tilEmailUsername.setError("Email or Username is required");
            isValid = false;
        } else if (!isValidEmail(emailUsername)) {
            if (emailUsername.contains("@")) {
                tilEmailUsername.setError("Enter a valid email address");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (!isStrongPassword(password)) {
            tilPassword.setError("Password is not strong enough. Must be at least 8 characters, include uppercase, lowercase, a digit, and a special character.");
            isValid = false;
        }

        if (isValid) {
            int userId = dbHelper.checkUser(emailUsername, password);

            if (userId != -1) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // --- (FIX 2) CORRECTED "REMEMBER ME" LOGIC ---

                // ALWAYS save the USER_ID.
                // DashboardActivity and AddExpenseActivity need this
                // to work for the CURRENT session.
                editor.putInt(KEY_USER_ID, userId);

                // Now, handle the "Remember me" logic for next time
                if (cbRememberMe.isChecked()) {
                    // Save credentials and the "remember me" flag for auto-login
                    editor.putString(KEY_REMEMBER_EMAIL, emailUsername);
                    editor.putString(KEY_REMEMBER_PASSWORD, password);
                    editor.putBoolean(KEY_REMEMBER_ME_CHECKED, true);
                } else {
                    // Clear credentials and the flag so the user is NOT auto-logged in
                    editor.remove(KEY_REMEMBER_EMAIL);
                    editor.remove(KEY_REMEMBER_PASSWORD);
                    editor.putBoolean(KEY_REMEMBER_ME_CHECKED, false);
                }
                editor.apply();

                // --- END OF FIX 2 ---

                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                goToDashboard(userId);
            } else {
                Toast.makeText(this, "Incorrect email/username or password", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean isStrongPassword(String password) {
        if (password.length() < 8) return false;
        if (!Pattern.compile("[A-Z]").matcher(password).find()) return false; // Uppercase
        if (!Pattern.compile("[a-z]").matcher(password).find()) return false; // Lowercase
        if (!Pattern.compile("\\d").matcher(password).find()) return false; // Digit
        if (!Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]").matcher(password).find()) return false; // Special character
        return true;
    }

    private void goToDashboard(int userId) {
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        if (userId != -1) {
            intent.putExtra("USER_ID", userId);
        }
        startActivity(intent);
        finish();
    }
}