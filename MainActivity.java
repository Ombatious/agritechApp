package com.example.agritechapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.agritechapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        setupToolbar();
        setupNavigation();
        observeUserData();
        setupDashboardCards();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.toolbarTitle.setText("Welcome!");
    }

    private void setupNavigation() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Configure top-level destinations
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.profileFragment,
                R.id.settingsFragment
        ).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Apply tech-inspired styling
        bottomNav.setBackgroundResource(R.drawable.bg_nav_bar);
        bottomNav.setItemIconTintList(getResources().getColorStateList(R.color.nav_tint_selector));
        bottomNav.setItemTextColor(getResources().getColorStateList(R.color.nav_tint_selector));
        bottomNav.setLabelVisibilityMode(BottomNavigationView.LABEL_VISIBILITY_LABELED);
    }

    private void observeUserData() {
        userViewModel.getCurrentUser().observe(this, user -> {
            if (user != null && user.name != null) {
                binding.toolbarTitle.setText(String.format("Welcome, %s!", user.name));
            } else {
                binding.toolbarTitle.setText("Welcome!");
            }
        });
    }

    private void setupDashboardCards() {
        binding.scannerCard.setOnClickListener(v ->
                navController.navigate(R.id.diseaseScannerFragment));

        binding.weatherCard.setOnClickListener(v ->
                navController.navigate(R.id.weatherAlertsFragment));

        binding.marketCard.setOnClickListener(v ->
                navController.navigate(R.id.marketPricesFragment));

        binding.transportCard.setOnClickListener(v ->
                Toast.makeText(this, "Group Transport - Coming Soon!", Toast.LENGTH_SHORT).show());

        binding.smsCard.setOnClickListener(v ->
                Toast.makeText(this, "SMS Alerts - Coming Soon!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}