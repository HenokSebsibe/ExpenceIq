package com.example.smartexpensetracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartExpenseTracker.db";
    private static final int DATABASE_VERSION = 8;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_INCOME = "income";
    public static final String TABLE_EXPENSES = "expenses";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TABLE_BUDGETS = "budgets";
    public static final String TABLE_SETTINGS = "settings";
    public static final String TABLE_SAVING_GOALS = "saving_goals";
    public static final String TABLE_ASSETS = "assets";
    public static final String TABLE_NOTIFICATIONS = "notifications";

    // Common Column Names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NOTE = "note";

    // Users Table Columns
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";

    // Income Table Columns
    public static final String COLUMN_INCOME_SOURCE = "source";

    // Expenses Table Columns
    public static final String COLUMN_EXPENSE_CATEGORY_ID = "category_id";

    // Categories Table Columns
    public static final String COLUMN_CATEGORY_NAME = "category_name";
    public static final String COLUMN_CATEGORY_ICON = "category_icon";

    // Transactions Table Columns
    public static final String COLUMN_TRANSACTION_TYPE = "type"; // income or expense
    public static final String COLUMN_TRANSACTION_REF_ID = "reference_id";

    // Budgets Table Columns
    public static final String COLUMN_BUDGET_LIMIT = "monthly_limit";

    // Settings Table Columns
    public static final String COLUMN_SETTING_CURRENCY = "currency";
    public static final String COLUMN_SETTING_THEME = "theme";
    public static final String COLUMN_SETTING_NOTIFICATION = "notification_status";

    // Saving Goals Table Columns
    public static final String COLUMN_GOAL_NAME = "goal_name";
    public static final String COLUMN_TARGET_AMOUNT = "target_amount";
    public static final String COLUMN_SAVED_AMOUNT = "saved_amount";
    public static final String COLUMN_DEADLINE = "deadline";
    public static final String COLUMN_STATUS = "status"; // 0 for active, 1 for completed

    // Assets Table Columns
    public static final String COLUMN_ASSET_NAME = "asset_name";
    public static final String COLUMN_ASSET_VALUE = "asset_value";

    // Notifications Table Columns
    public static final String COLUMN_NOTIF_TYPE = "type";
    public static final String COLUMN_NOTIF_MESSAGE = "message";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT,"
                + COLUMN_USER_EMAIL + " TEXT,"
                + COLUMN_USER_PASSWORD + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CATEGORY_NAME + " TEXT,"
                + COLUMN_CATEGORY_ICON + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + TABLE_INCOME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_INCOME_SOURCE + " TEXT,"
                + COLUMN_AMOUNT + " REAL,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_NOTE + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + TABLE_EXPENSES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_EXPENSE_CATEGORY_ID + " INTEGER,"
                + COLUMN_AMOUNT + " REAL,"
                + COLUMN_NOTE + " TEXT,"
                + COLUMN_DATE + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TRANSACTION_TYPE + " TEXT,"
                + COLUMN_TRANSACTION_REF_ID + " INTEGER,"
                + COLUMN_AMOUNT + " REAL,"
                + COLUMN_DATE + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + TABLE_BUDGETS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EXPENSE_CATEGORY_ID + " INTEGER,"
                + COLUMN_BUDGET_LIMIT + " REAL" + ")");

        db.execSQL("CREATE TABLE " + TABLE_SETTINGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SETTING_CURRENCY + " TEXT,"
                + COLUMN_SETTING_THEME + " TEXT,"
                + COLUMN_SETTING_NOTIFICATION + " INTEGER" + ")");

        db.execSQL("CREATE TABLE " + TABLE_SAVING_GOALS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_GOAL_NAME + " TEXT NOT NULL,"
                + COLUMN_TARGET_AMOUNT + " REAL NOT NULL,"
                + COLUMN_SAVED_AMOUNT + " REAL NOT NULL,"
                + COLUMN_DEADLINE + " TEXT,"
                + COLUMN_NOTE + " TEXT,"
                + COLUMN_STATUS + " INTEGER DEFAULT 0" + ")");

        db.execSQL("CREATE TABLE " + TABLE_ASSETS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ASSET_NAME + " TEXT,"
                + COLUMN_ASSET_VALUE + " REAL,"
                + COLUMN_DATE + " TEXT" + ")");

        createNotificationsTable(db);

        insertInitialCategories(db);
    }

    private void createNotificationsTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOTIF_TYPE + " TEXT,"
                + COLUMN_NOTIF_MESSAGE + " TEXT,"
                + COLUMN_DATE + " TEXT" + ")");
    }

    private void insertInitialCategories(SQLiteDatabase db) {
        String[] cats = {"Food", "Transport", "Bills", "Shopping", "Entertainment", "Health", "Education", "Others"};
        for (String cat : cats) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_NAME, cat);
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 8) {
            createNotificationsTable(db);
        }
    }

    // --- Category Methods ---
    public long addCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, name);
        return db.insert(TABLE_CATEGORIES, null, values);
    }

    public Cursor getAllCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CATEGORIES, null);
    }

    public boolean updateCategory(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, name);
        return db.update(TABLE_CATEGORIES, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CATEGORIES, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    // --- Income Methods ---
    public long addIncome(String source, double amount, String date, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INCOME_SOURCE, source);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_NOTE, note);
        long id = db.insert(TABLE_INCOME, null, values);
        if (id != -1) {
            addTransaction("income", id, amount, date);
            addNotification("Income", "Income of " + String.format("%.2f", amount) + " added successfully.");
        }
        return id;
    }

    public boolean updateIncome(int id, String source, double amount, String date, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INCOME_SOURCE, source);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_NOTE, note);
        boolean updated = db.update(TABLE_INCOME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
        if (updated) {
            updateTransaction("income", id, amount, date);
        }
        return updated;
    }

    public boolean deleteIncome(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean deleted = db.delete(TABLE_INCOME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
        if (deleted) {
            db.delete(TABLE_TRANSACTIONS, COLUMN_TRANSACTION_TYPE + " = 'income' AND " + COLUMN_TRANSACTION_REF_ID + " = ?", new String[]{String.valueOf(id)});
        }
        return deleted;
    }

    public Cursor getAllIncomes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INCOME + " ORDER BY " + COLUMN_DATE + " DESC", null);
    }

    // --- Expense Methods ---
    public long addExpense(int categoryId, double amount, String note, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXPENSE_CATEGORY_ID, categoryId);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_DATE, date);
        long id = db.insert(TABLE_EXPENSES, null, values);
        if (id != -1) {
            addTransaction("expense", id, amount, date);
            addNotification("Expense", "Expense of " + String.format("%.2f", amount) + " recorded successfully.");
        }
        return id;
    }

    public Cursor getAllExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT e.*, c." + COLUMN_CATEGORY_NAME + 
                " FROM " + TABLE_EXPENSES + " e " +
                " JOIN " + TABLE_CATEGORIES + " c ON e." + COLUMN_EXPENSE_CATEGORY_ID + " = c." + COLUMN_ID +
                " ORDER BY " + COLUMN_DATE + " DESC";
        return db.rawQuery(query, null);
    }

    // --- Transaction Methods ---
    private void addTransaction(String type, long refId, double amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRANSACTION_TYPE, type);
        values.put(COLUMN_TRANSACTION_REF_ID, refId);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date);
        db.insert(TABLE_TRANSACTIONS, null, values);
    }

    private void updateTransaction(String type, long refId, double amount, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_DATE, date);
        db.update(TABLE_TRANSACTIONS, values, COLUMN_TRANSACTION_TYPE + " = ? AND " + COLUMN_TRANSACTION_REF_ID + " = ?", 
                new String[]{type, String.valueOf(refId)});
    }

    public Cursor getAllTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TRANSACTIONS + " ORDER BY " + COLUMN_DATE + " DESC", null);
    }

    // --- Saving Goals Methods ---
    public long addSavingGoal(String name, double target, double saved, String deadline, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GOAL_NAME, name);
        values.put(COLUMN_TARGET_AMOUNT, target);
        values.put(COLUMN_SAVED_AMOUNT, saved);
        values.put(COLUMN_DEADLINE, deadline);
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_STATUS, (saved >= target) ? 1 : 0);
        
        long result = db.insert(TABLE_SAVING_GOALS, null, values);
        if (result != -1 && saved >= target) {
            addNotification("Goal", "🎉 Goal Completed: " + name);
        }
        return result;
    }

    public boolean updateSavingGoal(int id, String name, double target, double saved, String deadline, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GOAL_NAME, name);
        values.put(COLUMN_TARGET_AMOUNT, target);
        values.put(COLUMN_SAVED_AMOUNT, saved);
        values.put(COLUMN_DEADLINE, deadline);
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_STATUS, (saved >= target) ? 1 : 0);
        boolean updated = db.update(TABLE_SAVING_GOALS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
        if (updated && saved >= target) {
            addNotification("Goal", "🎉 Goal Completed: " + name);
        }
        return updated;
    }

    public boolean deleteSavingGoal(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SAVING_GOALS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean updateSavingGoalAmount(int id, double newSavedAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_GOAL_NAME + ", " + COLUMN_TARGET_AMOUNT + " FROM " + TABLE_SAVING_GOALS + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        double target = 0;
        String name = "";
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
            target = cursor.getDouble(1);
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SAVED_AMOUNT, newSavedAmount);
        values.put(COLUMN_STATUS, (newSavedAmount >= target) ? 1 : 0);
        boolean updated = db.update(TABLE_SAVING_GOALS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
        if (updated) {
            if (newSavedAmount >= target) {
                addNotification("Goal", "🎉 Goal Completed: " + name);
            } else {
                addNotification("Goal", "Contribution added to " + name);
            }
        }
        return updated;
    }

    public Cursor getAllSavingGoals() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SAVING_GOALS, null);
    }

    public double getTotalSavingsProgress() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_SAVED_AMOUNT + ") FROM " + TABLE_SAVING_GOALS, null);
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    // --- Notification Methods ---
    public void addNotification(String type, String message) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NOTIF_TYPE, type);
            values.put(COLUMN_NOTIF_MESSAGE, message);
            values.put(COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));
            db.insert(TABLE_NOTIFICATIONS, null, values);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding notification", e);
        }
    }

    public Cursor getAllNotifications() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " ORDER BY " + COLUMN_ID + " DESC", null);
    }

    public boolean deleteAllNotifications() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NOTIFICATIONS, null, null) > 0;
    }

    // --- Summary Methods ---
    public double getTotalIncome() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_INCOME, null);
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    public double getTotalExpense() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_EXPENSES, null);
        double total = 0;
        if (cursor.moveToFirst()) total = cursor.getDouble(0);
        cursor.close();
        return total;
    }
}
