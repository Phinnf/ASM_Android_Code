package com.example.Assignment_Demo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

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
    // Budgets Table
    public static final String TABLE_BUDGETS = "budgets";
    public static final String COL_BUDGET_ID = "ID";
    public static final String COL_BUDGET_USER_ID = "USER_ID"; // Foreign Key
    public static final String COL_BUDGET_CATEGORY = "CATEGORY";
    public static final String COL_BUDGET_AMOUNT = "AMOUNT";

    // Note Table
    public static final String TABLE_NOTES = "notes";
    public static final String COL_NOTE_ID = "ID";
    public static final String COL_NOTE_USER_ID = "USER_ID"; // Foreign Key
    public static final String COL_NOTE_CONTENT = "CONTENT";
    public static final String COL_NOTE_IS_COMPLETE = "IS_COMPLETE"; // 0 for false, 1 for true
    public static final String COL_NOTE_DATE = "DATE";

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
        // Create Budgets table
        String createBudgetsTable = "CREATE TABLE " + TABLE_BUDGETS + " (" +
                COL_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BUDGET_USER_ID + " INTEGER, " +
                COL_BUDGET_CATEGORY + " TEXT, " +
                COL_BUDGET_AMOUNT + " REAL, " +
                "FOREIGN KEY(" + COL_BUDGET_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                // Ensure each user has only 1 budget per category
                "UNIQUE (" + COL_BUDGET_USER_ID + ", " + COL_BUDGET_CATEGORY + "))";
        db.execSQL(createBudgetsTable);
        // Create Notes table
        String createNotesTable = "CREATE TABLE " + TABLE_NOTES + " (" +
                COL_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOTE_USER_ID + " INTEGER, " +
                COL_NOTE_CONTENT + " TEXT, " +
                COL_NOTE_IS_COMPLETE + " INTEGER DEFAULT 0, " +
                COL_NOTE_DATE + " TEXT, " +
                "FOREIGN KEY(" + COL_NOTE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createNotesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        // ... (drop users and expenses tables)
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
    // --- Functions for Budgets Table ---

    /**
     * Set/Update a budget (UPSERT)
     * If budget for this category exists -> Update
     * If not -> Insert new
     */
    public boolean setBudget(int userId, String category, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_BUDGET_USER_ID, userId);
        cv.put(COL_BUDGET_CATEGORY, category);
        cv.put(COL_BUDGET_AMOUNT, amount);

        // Try updating first
        int rowsAffected = db.update(TABLE_BUDGETS, cv,
                COL_BUDGET_USER_ID + " = ? AND " + COL_BUDGET_CATEGORY + " = ?",
                new String[]{String.valueOf(userId), category});

        // If no rows were affected (meaning it doesn't exist), insert new
        if (rowsAffected == 0) {
            long result = db.insert(TABLE_BUDGETS, null, cv);
            return result != -1;
        }

        return true; // Update successful
    }
    /**
     * Get the budget for a specific category
     * @return the budget amount, or 0 if not set
     */
    public double getBudget(int userId, String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_BUDGET_AMOUNT + " FROM " + TABLE_BUDGETS +
                        " WHERE " + COL_BUDGET_USER_ID + " = ? AND " + COL_BUDGET_CATEGORY + " = ?",
                new String[]{String.valueOf(userId), category});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            double amount = cursor.getDouble(0);
            cursor.close();
            return amount;
        } else {
            cursor.close();
            return 0; // Budget not set
        }
    }
    // --- Functions for Overview Calculation ---

    /**
     * Get user's total spending FOR THE CURRENT MONTH
     */
    public double getTotalSpendingForCurrentMonth(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Get current month and year, format "YYYY-MM"
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.US);
        String currentMonthYear = sdf.format(calendar.getTime()); // e.g., "2025-10"

        // Query: Calculate SUM(AMOUNT) for the user,
        // where DATE starts with "YYYY-MM"
        String query = "SELECT SUM(" + COL_EXPENSE_AMOUNT + ") FROM " + TABLE_EXPENSES +
                " WHERE " + COL_EXPENSE_USER_ID + " = ? AND " +
                " strftime('%Y-%m', " + COL_EXPENSE_DATE + ") = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), currentMonthYear});

        if (cursor.moveToFirst()) {
            double total = cursor.getDouble(0);
            cursor.close();
            return total;
        } else {
            cursor.close();
            return 0;
        }
    }
    /**
     * Get user's total budget (for all categories)
     */
    public double getTotalBudget(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COL_BUDGET_AMOUNT + ") FROM " + TABLE_BUDGETS +
                " WHERE " + COL_BUDGET_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            double total = cursor.getDouble(0);
            cursor.close();
            return total;
        } else {
            cursor.close();
            return 0;
        }
    }
    /**
     * CREATE: Add a new note
     */
    public boolean addNote(int userId, String content, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOTE_USER_ID, userId);
        cv.put(COL_NOTE_CONTENT, content);
        cv.put(COL_NOTE_DATE, date);
        // IS_COMPLETE defaults to 0 (false) as per table definition

        long result = db.insert(TABLE_NOTES, null, cv);
        return result != -1;
    }
    /**
     * READ: Get all notes for a user
     * Returns a Cursor to be used by an adapter
     */
    public Cursor getNotes(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NOTES +
                " WHERE " + COL_NOTE_USER_ID + " = ? " +
                " ORDER BY " + COL_NOTE_DATE + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }
    /**
     * UPDATE: Change a note's content
     */
    public boolean updateNoteContent(long noteId, String newContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOTE_CONTENT, newContent);

        int result = db.update(TABLE_NOTES, cv, COL_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
        return result > 0;
    }

    /**
     * UPDATE: Change a note's completion status
     */
    public boolean updateNoteCompletion(long noteId, boolean isComplete) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOTE_IS_COMPLETE, isComplete ? 1 : 0); // Convert boolean to 1 or 0

        int result = db.update(TABLE_NOTES, cv, COL_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
        return result > 0;
    }

    /**
     * DELETE: Remove a note
     */
    public boolean deleteNote(long noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NOTES, COL_NOTE_ID + " = ?", new String[]{String.valueOf(noteId)});
        return result > 0;
    }
    /**
     * (Advanced for Breakdown)
     * Get total spending for 1 category, IN THE CURRENT MONTH
     */
    public double getTotalSpendingForCategory(int userId, String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.US);
        String currentMonthYear = sdf.format(calendar.getTime());

        String query = "SELECT SUM(" + COL_EXPENSE_AMOUNT + ") FROM " + TABLE_EXPENSES +
                " WHERE " + COL_EXPENSE_USER_ID + " = ? AND " +
                COL_EXPENSE_CATEGORY + " = ? AND " +
                " strftime('%Y-%m', " + COL_EXPENSE_DATE + ") = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), category, currentMonthYear});

        if (cursor.moveToFirst()) {
            double total = cursor.getDouble(0);
            cursor.close();
            return total;
        } else {
            cursor.close();
            return 0;
        }
    }

}
