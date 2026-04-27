package com.example.smartexpensetracker;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartexpensetracker.db.DatabaseHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText etAmount, etNote, etDate;
    private Spinner spinnerCategory;
    private DatabaseHelper dbHelper;
    private List<Integer> categoryIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        dbHelper = new DatabaseHelper(this);

        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        etDate = findViewById(R.id.etDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        Button btnSave = findViewById(R.id.btnSaveExpense);

        loadCategories();

        etDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void loadCategories() {
        Cursor cursor = dbHelper.getAllCategories();
        List<String> categories = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                categoryIds.add(cursor.getInt(0));
                categories.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
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

    private void saveExpense() {
        String amountStr = etAmount.getText().toString();
        String note = etNote.getText().toString();
        String date = etDate.getText().toString();

        if (amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        int categoryId = categoryIds.get(spinnerCategory.getSelectedItemPosition());

        long id = dbHelper.addExpense(categoryId, amount, note, date);
        if (id != -1) {
            Toast.makeText(this, "Expense recorded successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error adding expense", Toast.LENGTH_SHORT).show();
        }
    }
}
