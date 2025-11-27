package com.example.Assignment_Demo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    // Removed tilMobileNumber and etMobileNumber declarations
    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnSignUp, btnSignInTab, btnSignUpTab;
    private TextView tvAlreadyHaveAccount;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        tilName = findViewById(R.id.til_name);
        etName = findViewById(R.id.et_name);
        tilEmail = findViewById(R.id.til_email);
        etEmail = findViewById(R.id.et_email);
        tilPassword = findViewById(R.id.til_password);
        etPassword = findViewById(R.id.et_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignUp = findViewById(R.id.btn_sign_up);
        btnSignInTab = findViewById(R.id.btn_sign_in_tab);
        btnSignUpTab = findViewById(R.id.btn_sign_up_tab);
        tvAlreadyHaveAccount = findViewById(R.id.tv_already_have_account);

        btnSignUp.setOnClickListener(v -> register());

        btnSignInTab.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Finish RegisterActivity
        });

        // Set initial state for tabs
        btnSignUpTab.setBackgroundTintList(getColorStateList(R.color.colorPrimary));
        btnSignUpTab.setTextColor(getColorStateList(R.color.white));
        btnSignInTab.setBackgroundTintList(getColorStateList(android.R.color.transparent));
        btnSignInTab.setTextColor(getColorStateList(R.color.colorPrimary));
        btnSignInTab.setStrokeColorResource(R.color.colorPrimary);
        btnSignInTab.setStrokeWidth(1);

        btnSignUpTab.setOnClickListener(v -> {
            // Already on Sign Up, just update button appearance (already set above)
            btnSignUpTab.setBackgroundTintList(getColorStateList(R.color.colorPrimary));
            btnSignUpTab.setTextColor(getColorStateList(R.color.white));
            btnSignInTab.setBackgroundTintList(getColorStateList(android.R.color.transparent));
            btnSignInTab.setTextColor(getColorStateList(R.color.colorPrimary));
            btnSignInTab.setStrokeColorResource(R.color.colorPrimary);
            btnSignInTab.setStrokeWidth(1);
        });

        tvAlreadyHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Finish RegisterActivity
        });
    }

    private void register() {
        String name = etName.getText().toString().trim();

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            tilName.setError("Name is required");
            isValid = false;
        }


        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!isValidEmail(email)) {
            tilEmail.setError("Enter a valid email address");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (!isStrongPassword(password)) {
            tilPassword.setError("Password is not strong enough. Must be at least 8 characters, include uppercase, lowercase, a digit, and a special character.");
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Confirm password is required");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }

        if (isValid) {
            if (dbHelper.checkUsernameExists(email)) { // Assuming email as username for check
                tilEmail.setError("This email is already registered");
                Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show();
            } else {
                boolean isRegistered = dbHelper.registerUser(name, email, password); // Using email as username for registration
                if (isRegistered) {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Go back to Login screen
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
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
        // Corrected escape characters for the special character regex pattern
        if (!Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]").matcher(password).find()) return false; // Special character
        return true;
    }
}