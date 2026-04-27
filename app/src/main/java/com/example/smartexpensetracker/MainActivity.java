package com.example.smartexpensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.smartexpensetracker.db.DatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalBalance, tvTotalIncome, tvTotalExpense, tvSavingsTotal;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyThemeSettings();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        // Initialize Summary Views
        tvTotalBalance = findViewById(R.id.tvTotalBalance);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvSavingsTotal = findViewById(R.id.tvSavingsTotal);

        // Quick Actions
        MaterialCardView cardAddExpense = findViewById(R.id.cardAddExpense);
        MaterialCardView cardAddIncome = findViewById(R.id.cardAddIncome);
        MaterialCardView cardTransactions = findViewById(R.id.cardTransactions);
        MaterialCardView cardReports = findViewById(R.id.cardReports);

        cardAddExpense.setOnClickListener(v -> startActivity(new Intent(this, AddExpenseActivity.class)));
        cardAddIncome.setOnClickListener(v -> startActivity(new Intent(this, AddIncomeActivity.class)));
        cardTransactions.setOnClickListener(v -> startActivity(new Intent(this, TransactionHistoryActivity.class)));
        cardReports.setOnClickListener(v -> startActivity(new Intent(this, ReportsActivity.class)));

        // Notification Bell - Re-implemented to open NotificationsActivity
        ImageView ivNotification = findViewById(R.id.ivNotification);
        if (ivNotification != null) {
            ivNotification.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NotificationsActivity.class)));
        }

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return true;
            if (id == R.id.nav_wallet) {
                startActivity(new Intent(this, IncomeListActivity.class));
                return true;
            }
            if (id == R.id.nav_reports) {
                startActivity(new Intent(this, ReportsActivity.class));
                return true;
            }
            if (id == R.id.nav_goals) {
                startActivity(new Intent(this, SavingsGoalsActivity.class));
                return true;
            }
            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });

        updateSummary();
    }

    private void applyThemeSettings() {
        android.content.SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("DarkMode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSummary();
    }

    private void updateSummary() {
        double income = dbHelper.getTotalIncome();
        double expense = dbHelper.getTotalExpense();
        double savings = dbHelper.getTotalSavingsProgress();
        double balance = income - expense;

        tvTotalIncome.setText(String.format(Locale.getDefault(), "$%.2f", income));
        tvTotalExpense.setText(String.format(Locale.getDefault(), "-$%.2f", expense));
        tvTotalBalance.setText(String.format(Locale.getDefault(), "$%.2f", balance));
        tvSavingsTotal.setText(String.format(Locale.getDefault(), "$%.2f", savings));
    }
}
