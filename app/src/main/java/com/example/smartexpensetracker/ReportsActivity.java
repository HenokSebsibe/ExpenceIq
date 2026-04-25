package com.example.smartexpensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartexpensetracker.db.DatabaseHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {

    private TextView tvIncomeReport, tvExpenseReport;
    private ProgressBar pbIncome, pbExpense;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        dbHelper = new DatabaseHelper(this);

        tvIncomeReport = findViewById(R.id.tvIncomeReport);
        tvExpenseReport = findViewById(R.id.tvExpenseReport);
        pbIncome = findViewById(R.id.pbIncome);
        pbExpense = findViewById(R.id.pbExpense);
        Button btnExport = findViewById(R.id.btnExportReport);

        updateReport();

        btnExport.setOnClickListener(v -> exportReport());
    }

    private void updateReport() {
        double income = dbHelper.getTotalIncome();
        double expense = dbHelper.getTotalExpense();

        tvIncomeReport.setText(String.format(Locale.getDefault(), "Total Income: $%.2f", income));
        tvExpenseReport.setText(String.format(Locale.getDefault(), "Total Expense: $%.2f", expense));

        double total = income + expense;
        if (total > 0) {
            pbIncome.setProgress((int) ((income / total) * 100));
            pbExpense.setProgress((int) ((expense / total) * 100));
        } else {
            pbIncome.setProgress(0);
            pbExpense.setProgress(0);
        }
    }

    private void exportReport() {
        String reportText = "Smart Expense Tracker Report\n" +
                "----------------------------\n" +
                "Total Income: $" + dbHelper.getTotalIncome() + "\n" +
                "Total Expense: $" + dbHelper.getTotalExpense() + "\n" +
                "Balance: $" + (dbHelper.getTotalIncome() - dbHelper.getTotalExpense()) + "\n";

        File file = new File(getExternalFilesDir(null), "expense_report.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(reportText.getBytes());
            Toast.makeText(this, "Report exported to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to export report", Toast.LENGTH_SHORT).show();
        }
    }
}
