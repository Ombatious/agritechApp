package com.example.agritechapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextInputEditText nameInput, emailInput, usernameInput, phoneInput, passwordInput, confirmPasswordInput;
    private TextInputLayout nameLayout, emailLayout, usernameLayout, phoneLayout, passwordLayout, confirmPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.name_input);
        emailInput = findViewById(R.id.email_input);
        usernameInput = findViewById(R.id.username_input);
        phoneInput = findViewById(R.id.phone_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);

        nameLayout = findViewById(R.id.name_layout);
        emailLayout = findViewById(R.id.email_layout);
        usernameLayout = findViewById(R.id.username_layout);
        phoneLayout = findViewById(R.id.phone_layout);
        passwordLayout = findViewById(R.id.password_layout);
        confirmPasswordLayout = findViewById(R.id.confirm_password_layout);
    }

    private void setupClickListeners() {
        findViewById(R.id.sign_up_button).setOnClickListener(v -> attemptSignUp());
        findViewById(R.id.login_link).setOnClickListener(v -> navigateToLogin());
    }

    private void attemptSignUp() {
        resetErrors();

        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (!validateInputs(name, email, username, phone, password, confirmPassword)) {
            return;
        }

        // Proceed with Firebase registration
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            // Create user data
                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("name", name);
                            userMap.put("email", email);
                            userMap.put("username", username);
                            userMap.put("phone", phone);
                            userMap.put("uid", userId);

                            // Save user data in Firestore
                            DocumentReference userRef = db.collection("Users").document(userId);
                            userRef.set(userMap).addOnCompleteListener(storeTask -> {
                                if (storeTask.isSuccessful()) {
                                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                    clearInputs();
                                    navigateToLogin();
                                } else {
                                    Toast.makeText(this, "Failed to save user data: " + storeTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void resetErrors() {
        nameLayout.setError(null);
        emailLayout.setError(null);
        usernameLayout.setError(null);
        phoneLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
    }

    private boolean validateInputs(String name, String email, String username, String phone, String password, String confirmPassword) {
        boolean isValid = true;

        if (TextUtils.isEmpty(name)) {
            nameLayout.setError("Full name required");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email required");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Invalid email format");
            isValid = false;
        }

        if (TextUtils.isEmpty(username)) {
            usernameLayout.setError("Username required");
            isValid = false;
        } else if (username.length() < 4) {
            usernameLayout.setError("Username too short (min 4 chars)");
            isValid = false;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneLayout.setError("Phone number required");
            isValid = false;
        } else if (!Patterns.PHONE.matcher(phone).matches()) {
            phoneLayout.setError("Invalid phone number");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password required");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Password too short (min 6 chars)");
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordLayout.setError("Please confirm password");
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords don't match");
            isValid = false;
        }

        return isValid;
    }

    private void clearInputs() {
        nameInput.setText("");
        emailInput.setText("");
        usernameInput.setText("");
        phoneInput.setText("");
        passwordInput.setText("");
        confirmPasswordInput.setText("");
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
