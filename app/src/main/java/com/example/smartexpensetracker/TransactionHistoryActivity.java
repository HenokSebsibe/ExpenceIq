package com.example.smartexpensetracker;

import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.adapters.TransactionAdapter;
import com.example.smartexpensetracker.db.DatabaseHelper;
import com.example.smartexpensetracker.models.Transaction;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    private RecyclerView rvTransactions;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        dbHelper = new DatabaseHelper(this);
        rvTransactions = findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));

        transactionList = new ArrayList<>();
        loadTransactions();

        adapter = new TransactionAdapter(transactionList);
        rvTransactions.setAdapter(adapter);
    }

    private void loadTransactions() {
        transactionList.clear();
        Cursor cursor = dbHelper.getAllTransactions();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRANSACTION_TYPE));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));

                transactionList.add(new Transaction(id, type, amount, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
