package com.example.smartexpensetracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.adapters.IncomeAdapter;
import com.example.smartexpensetracker.db.DatabaseHelper;
import com.example.smartexpensetracker.models.Income;
import java.util.ArrayList;
import java.util.List;

public class IncomeListActivity extends AppCompatActivity implements IncomeAdapter.OnIncomeClickListener {

    private RecyclerView rvIncome;
    private IncomeAdapter adapter;
    private List<Income> incomeList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_list);

        dbHelper = new DatabaseHelper(this);
        rvIncome = findViewById(R.id.rvIncome);
        ImageButton btnAdd = findViewById(R.id.btnAddIncomeHeader);

        rvIncome.setLayoutManager(new LinearLayoutManager(this));
        incomeList = new ArrayList<>();
        adapter = new IncomeAdapter(incomeList, this);
        rvIncome.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> startActivity(new Intent(this, AddIncomeActivity.class)));

        loadIncomes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIncomes();
    }

    private void loadIncomes() {
        incomeList.clear();
        Cursor cursor = dbHelper.getAllIncomes();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String source = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_SOURCE));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE));

                incomeList.add(new Income(id, source, amount, date, note));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEditClick(Income income) {
        // For simplicity, we can pass data to AddIncomeActivity and modify it to handle edits
        Intent intent = new Intent(this, AddIncomeActivity.class);
        intent.putExtra("INCOME_ID", income.getId());
        intent.putExtra("INCOME_SOURCE", income.getSource());
        intent.putExtra("INCOME_AMOUNT", income.getAmount());
        intent.putExtra("INCOME_DATE", income.getDate());
        intent.putExtra("INCOME_NOTE", income.getNote());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Income income) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Income")
                .setMessage("Are you sure you want to delete this income record?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (dbHelper.deleteIncome(income.getId())) {
                        Toast.makeText(this, "Income deleted", Toast.LENGTH_SHORT).show();
                        loadIncomes();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
