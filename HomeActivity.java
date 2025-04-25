package com.example.agritechapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private MaterialButton btnLogin, btnSignup, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        initializeViews();
        setupClickListeners();

        // Check if user is already logged in
        checkUserSession();
    }

    private void initializeViews() {
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        btnExit = findViewById(R.id.btnExit);
    }

    private void setupClickListeners() {
        // Login button with smooth transition
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Signup button with smooth transition
        btnSignup.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, SignUpActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Exit button with confirmation dialog
        btnExit.setOnClickListener(v -> showExitConfirmationDialog());
    }

    private void checkUserSession() {
        // If user is already logged in, redirect to MainActivity
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            finish();
        }
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit AgriTech")
                .setMessage("Are you sure you want to exit the application?")
                .setPositiveButton("EXIT", (dialog, which) -> finishAffinity())
                .setNegativeButton("CANCEL", null)
                .setCancelable(false)
                .show();
    }

    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }
}