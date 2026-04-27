package com.example.smartexpensetracker;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.adapters.NotificationAdapter;
import com.example.smartexpensetracker.db.DatabaseHelper;
import com.example.smartexpensetracker.models.AppNotification;
import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<AppNotification> notificationList;
    private DatabaseHelper dbHelper;
    private TextView tvNoNotifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Notifications");
        }

        dbHelper = new DatabaseHelper(this);
        rvNotifications = findViewById(R.id.rvNotifications);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);
        
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        notificationList = new ArrayList<>();
        
        adapter = new NotificationAdapter(notificationList);
        rvNotifications.setAdapter(adapter);
        
        loadNotifications();
    }

    private void loadNotifications() {
        notificationList.clear();
        try (Cursor cursor = dbHelper.getAllNotifications()) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIF_TYPE));
                    String message = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NOTIF_MESSAGE));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
                    
                    notificationList.add(new AppNotification(id, type, message, date));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (notificationList.isEmpty()) {
            tvNoNotifications.setVisibility(View.VISIBLE);
            tvNoNotifications.setText("No notifications available");
            rvNotifications.setVisibility(View.GONE);
        } else {
            tvNoNotifications.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(0, 1, 0, "Clear All");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            if (dbHelper.deleteAllNotifications()) {
                Toast.makeText(this, "Notifications cleared", Toast.LENGTH_SHORT).show();
                loadNotifications();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
