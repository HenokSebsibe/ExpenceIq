package com.example.smartexpensetracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartexpensetracker.db.DatabaseHelper;
import java.util.Calendar;

public class AddSavingGoalActivity extends AppCompatActivity {

    private EditText etName, etTarget, etSaved, etDeadline, etNote;
    private DatabaseHelper dbHelper;
    private int goalId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_saving_goal);

        dbHelper = new DatabaseHelper(this);

        TextView tvTitle = findViewById(R.id.tvAddGoalTitle);
        etName = findViewById(R.id.etGoalName);
        etTarget = findViewById(R.id.etTargetAmount);
        etSaved = findViewById(R.id.etSavedAmount);
        etDeadline = findViewById(R.id.etDeadline);
        etNote = findViewById(R.id.etGoalNote);
        Button btnSave = findViewById(R.id.btnSaveGoal);

        // Check for Edit Mode
        if (getIntent().hasExtra("GOAL_ID")) {
            isEditMode = true;
            goalId = getIntent().getIntExtra("GOAL_ID", -1);
            etName.setText(getIntent().getStringExtra("GOAL_NAME"));
            etTarget.setText(String.valueOf(getIntent().getDoubleExtra("GOAL_TARGET", 0.0)));
            etSaved.setText(String.valueOf(getIntent().getDoubleExtra("GOAL_SAVED", 0.0)));
            etDeadline.setText(getIntent().getStringExtra("GOAL_DEADLINE"));
            etNote.setText(getIntent().getStringExtra("GOAL_NOTE"));
            
            tvTitle.setText("Edit Saving Goal");
            btnSave.setText("Update Goal");
        }

        etDeadline.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveGoal());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    etDeadline.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveGoal() {
        try {
            String name = etName.getText().toString().trim();
            String targetStr = etTarget.getText().toString().trim();
            String savedStr = etSaved.getText().toString().trim();
            String deadline = etDeadline.getText().toString().trim();
            String note = etNote.getText().toString().trim();

            if (name.isEmpty() || targetStr.isEmpty() || deadline.isEmpty()) {
                Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double target = Double.parseDouble(targetStr);
            double saved = savedStr.isEmpty() ? 0 : Double.parseDouble(savedStr);

            boolean success;
            if (isEditMode) {
                success = dbHelper.updateSavingGoal(goalId, name, target, saved, deadline, note);
            } else {
                long result = dbHelper.addSavingGoal(name, target, saved, deadline, note);
                success = (result != -1);
                if (result == -1) {
                    Log.e("GOAL_ERROR", "db.insert returned -1 for goals table");
                }
            }

            if (success) {
                Toast.makeText(this, "Goal created successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Database Error: Could not save goal", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("GOAL_ERROR", "Save failed", e);
            Toast.makeText(this, "Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
