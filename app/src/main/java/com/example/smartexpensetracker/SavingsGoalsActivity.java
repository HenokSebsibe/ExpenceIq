package com.example.smartexpensetracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.adapters.SavingGoalAdapter;
import com.example.smartexpensetracker.db.DatabaseHelper;
import com.example.smartexpensetracker.models.SavingGoal;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class SavingsGoalsActivity extends AppCompatActivity {

    private RecyclerView rvSavingGoals;
    private SavingGoalAdapter adapter;
    private List<SavingGoal> goalList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_goals);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        rvSavingGoals = findViewById(R.id.rvSavingGoals);
        rvSavingGoals.setLayoutManager(new LinearLayoutManager(this));

        goalList = new ArrayList<>();
        loadGoals();

        adapter = new SavingGoalAdapter(goalList, this::onGoalClick);
        rvSavingGoals.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddGoal);
        fab.setOnClickListener(v -> {
            // Start AddSavingGoalActivity
            // startActivity(new Intent(this, AddSavingGoalActivity.class));
        });
    }

    private void loadGoals() {
        goalList.clear();
        Cursor cursor = dbHelper.getAllSavingGoals();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_GOAL_NAME));
                double target = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TARGET_AMOUNT));
                double saved = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SAVED_AMOUNT));
                String deadline = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DEADLINE));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS));

                goalList.add(new SavingGoal(id, name, target, saved, deadline, status));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void onGoalClick(SavingGoal goal) {
        // Handle goal click (edit/delete)
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGoals();
        adapter.notifyDataSetChanged();
    }
}
