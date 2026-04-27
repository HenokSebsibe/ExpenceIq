package com.example.smartexpensetracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartexpensetracker.db.DatabaseHelper;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        btnExport.setOnClickListener(v -> exportDetailedReport());
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

    private void exportDetailedReport() {
        double totalIncome = dbHelper.getTotalIncome();
        double totalExpense = dbHelper.getTotalExpense();
        double balance = totalIncome - totalExpense;
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder.append("Detailed Financial Report\n");
        reportBuilder.append("============================\n");
        reportBuilder.append("Date of Export: ").append(dateString).append("\n\n");

        // 1. SUMMARY SECTION
        reportBuilder.append("1. SUMMARY SECTION\n");
        reportBuilder.append("------------------\n");
        reportBuilder.append("Total Income:   $").append(String.format(Locale.getDefault(), "%.2f", totalIncome)).append("\n");
        reportBuilder.append("Total Expense:  $").append(String.format(Locale.getDefault(), "%.2f", totalExpense)).append("\n");
        reportBuilder.append("Balance:        $").append(String.format(Locale.getDefault(), "%.2f", balance)).append("\n\n");

        // 2. INCOME DETAILS SECTION
        reportBuilder.append("2. INCOME DETAILS SECTION\n");
        reportBuilder.append("-------------------------\n");
        Cursor incomeCursor = dbHelper.getAllIncomes();
        if (incomeCursor != null && incomeCursor.moveToFirst()) {
            do {
                String source = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_INCOME_SOURCE));
                double amount = incomeCursor.getDouble(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT));
                String date = incomeCursor.getString(incomeCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
                reportBuilder.append(source).append(" - $").append(String.format(Locale.getDefault(), "%.2f", amount)).append(" - ").append(date).append("\n");
            } while (incomeCursor.moveToNext());
            incomeCursor.close();
        } else {
            reportBuilder.append("No income records found.\n");
        }
        reportBuilder.append("\n");

        // 3. EXPENSE DETAILS SECTION
        reportBuilder.append("3. EXPENSE DETAILS SECTION\n");
        reportBuilder.append("--------------------------\n");
        Cursor expenseCursor = dbHelper.getAllExpenses();
        if (expenseCursor != null && expenseCursor.moveToFirst()) {
            do {
                String category = expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
                double amount = expenseCursor.getDouble(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT));
                String note = expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTE));
                String date = expenseCursor.getString(expenseCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
                reportBuilder.append(category).append(" - $").append(String.format(Locale.getDefault(), "%.2f", amount)).append(" - ").append(note).append(" - ").append(date).append("\n");
            } while (expenseCursor.moveToNext());
            expenseCursor.close();
        } else {
            reportBuilder.append("No expense records found.\n");
        }
        reportBuilder.append("\n");

        // 4. FINAL INSIGHT SECTION
        reportBuilder.append("4. FINAL INSIGHT SECTION\n");
        reportBuilder.append("------------------------\n");
        if (totalIncome > totalExpense) {
            reportBuilder.append("Insight: Income is higher than expenses.\n");
        } else if (totalExpense > totalIncome) {
            reportBuilder.append("Insight: Expenses exceed income this month.\n");
        } else {
            reportBuilder.append("Insight: Income and expenses are equal.\n");
        }
        reportBuilder.append("============================\n");

        String reportText = reportBuilder.toString();
        String fileName = "Expense_Detailed_Report.txt";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri != null) {
                try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                    if (outputStream != null) {
                        outputStream.write(reportText.getBytes());
                        outputStream.flush();
                        Toast.makeText(this, "Detailed report exported successfully to Downloads", Toast.LENGTH_LONG).show();
                    } else {
                        throw new IOException("Failed to get output stream.");
                    }
                } catch (IOException e) {
                    getContentResolver().delete(uri, null, null);
                    Toast.makeText(this, "Failed to export report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to create MediaStore entry", Toast.LENGTH_SHORT).show();
            }
        } else {
            java.io.File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            java.io.File file = new java.io.File(downloadsDir, fileName);
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                fos.write(reportText.getBytes());
                fos.flush();
                Toast.makeText(this, "Detailed report exported successfully to Downloads", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, "Failed to export report: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
