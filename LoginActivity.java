package com.example.agritechapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputEditText emailInput, passwordInput;
    private TextInputLayout emailLayout, passwordLayout;
    private int loginAttempts = 0;
    private static final int MAX_ATTEMPTS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        emailLayout = findViewById(R.id.email_layout);
        passwordLayout = findViewById(R.id.password_layout);
    }

    private void setupClickListeners() {
        findViewById(R.id.login_button).setOnClickListener(v -> attemptLogin());
        findViewById(R.id.forgot_password).setOnClickListener(v -> showForgotPasswordDialog());
        findViewById(R.id.sign_up_link).setOnClickListener(v -> navigateToSignUp());
    }

    private void attemptLogin() {
        resetErrors();

        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (!validateInputs(email, password)) return;

        // Firebase Authentication logic
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        handleSuccessfulLogin();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed";
                        passwordLayout.setError(errorMessage);
                        handleFailedLogin();
                    }
                });
    }

    private void resetErrors() {
        emailLayout.setError(null);
        passwordLayout.setError(null);
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email required");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password required");
            return false;
        }
        return true;
    }

    private void handleSuccessfulLogin() {
        clearCredentials();
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class)); // Navigate to MainActivity
        finish(); // Close the LoginActivity
    }

    private void handleFailedLogin() {
        loginAttempts++;

        if (loginAttempts >= MAX_ATTEMPTS) {
            showMaxAttemptsDialog();
        } else {
            Toast.makeText(this,
                    String.format("%d attempts remaining", MAX_ATTEMPTS - loginAttempts),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void clearCredentials() {
        emailInput.setText("");
        passwordInput.setText("");
    }

    private void showForgotPasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot_password, null);
        TextInputEditText emailField = dialogView.findViewById(R.id.reset_email_input);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Reset Password")
                .setView(dialogView)
                .setPositiveButton("Send", (dialog, which) -> {
                    String email = emailField.getText().toString().trim();
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    } else {
                        sendPasswordResetEmail(email);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset instructions sent to " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToSignUp() {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    private void showMaxAttemptsDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Security Alert")
                .setMessage("Maximum login attempts reached. Please try again later.")
                .setPositiveButton("Exit", (dialog, which) -> finishAffinity()) // Close app after max attempts
                .setCancelable(false)
                .show();
    }
}
