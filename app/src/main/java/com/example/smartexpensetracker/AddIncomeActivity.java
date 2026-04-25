package com.example.smartexpensetracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartexpensetracker.db.DatabaseHelper;
import java.util.Arrays;
import java.util.Calendar;

public class AddIncomeActivity extends AppCompatActivity {

    private EditText etAmount, etDate, etNote;
    private Spinner spinnerSource;
    private DatabaseHelper dbHelper;
    private String[] incomeSources = {"Salary", "Freelance", "Gift", "Business", "Bonus", "Other"};
    private int incomeId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        dbHelper = new DatabaseHelper(this);

        TextView tvTitle = findViewById(R.id.tvAddIncomeTitle);
        etAmount = findViewById(R.id.etIncomeAmount);
        spinnerSource = findViewById(R.id.spinnerIncomeSource);
        etDate = findViewById(R.id.etIncomeDate);
        etNote = findViewById(R.id.etIncomeNote);
        Button btnSave = findViewById(R.id.btnSaveIncome);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, incomeSources);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSource.setAdapter(adapter);

        // Check for edit mode
        if (getIntent().hasExtra("INCOME_ID")) {
            isEditMode = true;
            incomeId = getIntent().getIntExtra("INCOME_ID", -1);
            etAmount.setText(String.valueOf(getIntent().getDoubleExtra("INCOME_AMOUNT", 0.0)));
            etDate.setText(getIntent().getStringExtra("INCOME_DATE"));
            etNote.setText(getIntent().getStringExtra("INCOME_NOTE"));
            
            String source = getIntent().getStringExtra("INCOME_SOURCE");
            int position = Arrays.asList(incomeSources).indexOf(source);
            if (position >= 0) spinnerSource.setSelection(position);
            
            if (tvTitle != null) tvTitle.setText("Edit Income");
            btnSave.setText("Update Income");
        }

        etDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveIncome());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    etDate.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveIncome() {
        String amountStr = etAmount.getText().toString();
        String source = spinnerSource.getSelectedItem().toString();
        String date = etDate.getText().toString();
        String note = etNote.getText().toString();

        if (amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        boolean success;
        if (isEditMode) {
            success = dbHelper.updateIncome(incomeId, source, amount, date, note);
        } else {
            success = dbHelper.addIncome(source, amount, date, note) != -1;
        }

        if (success) {
            Toast.makeText(this, isEditMode ? "Income updated" : "Income added", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error saving income", Toast.LENGTH_SHORT).show();
        }
    }
}
