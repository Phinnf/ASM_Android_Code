package com.example.Assignment_Demo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CampusExpense.db";
    public static final int DATABASE_VERSION = 1;

    // Users Table
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "ID";
    public static final String COL_USER_USERNAME = "USERNAME";
    public static final String COL_USER_PASSWORD = "PASSWORD";

    // Expenses Table (Prepare for Lab 2)
    public static final String TABLE_EXPENSES = "expenses";
    public static final String COL_EXPENSE_ID = "ID";
    public static final String COL_EXPENSE_USER_ID = "USER_ID"; // Foreign Key
    public static final String COL_EXPENSE_DESC = "DESCRIPTION";
    public static final String COL_EXPENSE_AMOUNT = "AMOUNT";
    public static final String COL_EXPENSE_CATEGORY = "CATEGORY";
    public static final String COL_EXPENSE_DATE = "DATE"; // Format: "YYYY-MM-DD"

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_USERNAME + " TEXT UNIQUE, " +
                COL_USER_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);

        // Create Expenses table
        String createExpensesTable = "CREATE TABLE " + TABLE_EXPENSES + " (" +
                COL_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EXPENSE_USER_ID + " INTEGER, " +
                COL_EXPENSE_DESC + " TEXT, " +
                COL_EXPENSE_AMOUNT + " REAL, " + // Use REAL for money
                COL_EXPENSE_CATEGORY + " TEXT, " +
                COL_EXPENSE_DATE + " TEXT, " +
                "FOREIGN KEY(" + COL_EXPENSE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createExpensesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // --- Functions for Users Table ---

    /**
     * Register new user
     */
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_USERNAME, username);
        cv.put(COL_USER_PASSWORD, password); // Should be encrypted in production

        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    /**
     * Check if username exists
     */
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_USERNAME + " = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    /**
     * Check user login and return user ID
     * @return User ID if successful, -1 if failed
     */
    public int checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                        COL_USER_USERNAME + " = ? AND " + COL_USER_PASSWORD + " = ?",
                new String[]{username, password});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            // Use getColumnIndexOrThrow for safety
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID));
            cursor.close();
            return userId; // Login successful, return ID
        } else {
            cursor.close();
            return -1; // Login failed
        }
    }
    // ... (Functions for Users Table)

// --- Functions for Expenses Table ---

    /**
     * Add a new expense
     */
    public boolean addExpense(int userId, String description, double amount, String category, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EXPENSE_USER_ID, userId);
        cv.put(COL_EXPENSE_DESC, description);
        cv.put(COL_EXPENSE_AMOUNT, amount);
        cv.put(COL_EXPENSE_CATEGORY, category);
        cv.put(COL_EXPENSE_DATE, date); // Format "YYYY-MM-DD"

        long result = db.insert(TABLE_EXPENSES, null, cv);
        return result != -1;
    }

    /**
     * Get all expenses for a user
     * Sorted by latest date
     */
    public Cursor getExpenses(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EXPENSES +
                " WHERE " + COL_EXPENSE_USER_ID + " = ? " +
                " ORDER BY " + COL_EXPENSE_DATE + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    /**
     * (Advanced) Delete an expense
     */
    public boolean deleteExpense(long expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_EXPENSES, COL_EXPENSE_ID + " = ?", new String[]{String.valueOf(expenseId)});
        return result > 0;
    }

}
