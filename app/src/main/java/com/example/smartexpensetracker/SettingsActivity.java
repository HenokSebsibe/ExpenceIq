package com.example.smartexpensetracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private SwitchMaterial switchTheme;
    private Spinner spinnerCurrency;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("Settings", MODE_PRIVATE);

        switchTheme = findViewById(R.id.switchTheme);
        spinnerCurrency = findViewById(R.id.spinnerCurrency);
        Button btnSave = findViewById(R.id.btnSaveSettings);

        // Load current theme setting
        boolean isDarkMode = prefs.getBoolean("DarkMode", false);
        switchTheme.setChecked(isDarkMode);

        String[] currencies = {"USD ($)", "EUR (€)", "GBP (£)", "INR (₹)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapter);

        btnSave.setOnClickListener(v -> {
            boolean dark = switchTheme.isChecked();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("DarkMode", dark);
            editor.apply();

            if (dark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            Toast.makeText(this, "Settings Saved Successfully", Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
    }
}
