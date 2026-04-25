package com.example.smartexpensetracker;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartexpensetracker.adapters.CategoryAdapter;
import com.example.smartexpensetracker.db.DatabaseHelper;
import com.example.smartexpensetracker.models.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryActionListener {

    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        dbHelper = new DatabaseHelper(this);
        rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fabAdd = findViewById(R.id.fabAddCategory);
        fabAdd.setOnClickListener(v -> showCategoryDialog(null));

        categoryList = new ArrayList<>();
        loadCategories();

        adapter = new CategoryAdapter(categoryList, this);
        rvCategories.setAdapter(adapter);

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
    }

    private void loadCategories() {
        categoryList.clear();
        Cursor cursor = dbHelper.getAllCategories();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
                categoryList.add(new Category(id, name));
            } while (cursor.moveToNext());
            cursor.close();
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void showCategoryDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_category, null);
        EditText etName = view.findViewById(R.id.etCategoryName);

        if (category != null) {
            builder.setTitle("Edit Category");
            etName.setText(category.getName());
        } else {
            builder.setTitle("Add Category");
        }

        builder.setView(view);
        builder.setPositiveButton(category != null ? "Update" : "Add", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            if (!name.isEmpty()) {
                if (category != null) {
                    dbHelper.updateCategory(category.getId(), name);
                } else {
                    dbHelper.addCategory(name);
                }
                loadCategories();
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public void onEdit(Category category) {
        showCategoryDialog(category);
    }

    @Override
    public void onDelete(Category category) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete this category?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    dbHelper.deleteCategory(category.getId());
                    loadCategories();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
