package com.example.smartexpensetracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartexpensetracker.db.DatabaseHelper;
import java.util.Calendar;

public class AddSavingGoalActivity extends AppCompatActivity {

    private EditText etName, etTarget, etSaved, etDeadline;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_saving_goal);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etGoalName);
        etTarget = findViewById(R.id.etTargetAmount);
        etSaved = findViewById(R.id.etSavedAmount);
        etDeadline = findViewById(R.id.etDeadline);
        Button btnSave = findViewById(R.id.btnSaveGoal);

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
        String name = etName.getText().toString();
        String targetStr = etTarget.getText().toString();
        String savedStr = etSaved.getText().toString();
        String deadline = etDeadline.getText().toString();

        if (name.isEmpty() || targetStr.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double target = Double.parseDouble(targetStr);
        double saved = savedStr.isEmpty() ? 0 : Double.parseDouble(savedStr);

        long id = dbHelper.addSavingGoal(name, target, saved, deadline);
        if (id != -1) {
            Toast.makeText(this, "Saving goal created", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error creating goal", Toast.LENGTH_SHORT).show();
        }
    }
}
