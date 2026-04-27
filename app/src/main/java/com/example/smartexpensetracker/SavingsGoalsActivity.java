package com.example.smartexpensetracker;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.adapters.SavingGoalAdapter;
import com.example.smartexpensetracker.db.DatabaseHelper;
import com.example.smartexpensetracker.models.SavingGoal;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class SavingsGoalsActivity extends AppCompatActivity implements SavingGoalAdapter.OnGoalClickListener {

    private RecyclerView rvSavingGoals;
    private SavingGoalAdapter adapter;
    private List<SavingGoal> goalList;
    private DatabaseHelper dbHelper;
    private PieChart pieChart;
    private TextView tvNoData;

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
        pieChart = findViewById(R.id.pieChartGoals);
        tvNoData = findViewById(R.id.tvNoData);

        rvSavingGoals.setLayoutManager(new LinearLayoutManager(this));
        rvSavingGoals.setNestedScrollingEnabled(false);

        goalList = new ArrayList<>();
        adapter = new SavingGoalAdapter(goalList, this);
        rvSavingGoals.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddGoal);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddSavingGoalActivity.class)));

        setupPieChart();
        loadGoals();
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setCenterText("Savings Distribution");
        pieChart.setCenterTextSize(16f);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.getLegend().setEnabled(false);
    }

    private void updatePieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (SavingGoal goal : goalList) {
            if (goal.getSavedAmount() > 0) {
                entries.add(new PieEntry((float) goal.getSavedAmount(), goal.getName()));
            }
        }

        if (entries.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
            return;
        }

        pieChart.setVisibility(View.VISIBLE);
        tvNoData.setVisibility(View.GONE);

        PieDataSet dataSet = new PieDataSet(entries, "Saving Goals");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.animateY(1400);
        pieChart.invalidate();
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
                String note = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS));

                goalList.add(new SavingGoal(id, name, target, saved, deadline, note, status));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
        updatePieChart();
    }

    @Override
    public void onGoalClick(SavingGoal goal) {
        String[] options = {"Edit Goal", "Delete Goal", "Cancel"};
        new AlertDialog.Builder(this)
                .setTitle(goal.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(this, AddSavingGoalActivity.class);
                        intent.putExtra("GOAL_ID", goal.getId());
                        intent.putExtra("GOAL_NAME", goal.getName());
                        intent.putExtra("GOAL_TARGET", goal.getTargetAmount());
                        intent.putExtra("GOAL_SAVED", goal.getSavedAmount());
                        intent.putExtra("GOAL_DEADLINE", goal.getDeadline());
                        intent.putExtra("GOAL_NOTE", goal.getNote());
                        startActivity(intent);
                    } else if (which == 1) {
                        confirmDelete(goal);
                    }
                })
                .show();
    }

    @Override
    public void onUpdateAmountClick(SavingGoal goal) {
        // Long click also opens update dialog
        showContributionDialog(goal);
    }

    @Override
    public void onAddContributionClick(SavingGoal goal) {
        showContributionDialog(goal);
    }

    private void showContributionDialog(SavingGoal goal) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_savings, null);
        EditText etAmount = view.findViewById(R.id.etAddAmount);
        TextView tvDescription = view.findViewById(R.id.tvDialogDescription);
        
        if (tvDescription != null) {
            tvDescription.setText("Enter amount to add to your savings for " + goal.getName());
        }

        new AlertDialog.Builder(this)
                .setTitle("Add Contribution")
                .setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    String amountStr = etAmount.getText().toString();
                    if (!amountStr.isEmpty()) {
                        double addedAmount = Double.parseDouble(amountStr);
                        double newTotal = goal.getSavedAmount() + addedAmount;
                        
                        if (newTotal > goal.getTargetAmount()) {
                            Toast.makeText(this, "Amount exceeds target! Max allowed: " + (goal.getTargetAmount() - goal.getSavedAmount()), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (dbHelper.updateSavingGoalAmount(goal.getId(), newTotal)) {
                            if (newTotal >= goal.getTargetAmount()) {
                                Toast.makeText(this, "Congratulations! Goal Completed 🎉", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, "Contribution added successfully", Toast.LENGTH_SHORT).show();
                            }
                            loadGoals();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(SavingGoal goal) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Goal")
                .setMessage("Are you sure you want to delete '" + goal.getName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (dbHelper.deleteSavingGoal(goal.getId())) {
                        Toast.makeText(this, "Goal deleted", Toast.LENGTH_SHORT).show();
                        loadGoals();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGoals();
    }
}
