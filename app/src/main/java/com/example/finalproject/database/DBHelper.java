package com.example.finalproject.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "productivity.db";
    private static final int DB_VERSION = 1;

    // Table names
    public static final String TABLE_TASK = "tasks";
    public static final String TABLE_EVENT = "events";
    public static final String TABLE_LEADERBOARD = "leaderboard";
    public static final String TABLE_SCREEN_TIME = "screen_time";
    public static final String TABLE_SPLIT_BILL = "split_bill";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TASKS (Firebase mode: table ini tidak dipakai, tapi dibiarkan biar gak error jika ada kode legacy)
        db.execSQL("CREATE TABLE " + TABLE_TASK + " (id INTEGER PRIMARY KEY, title TEXT)");

        // EVENTS
        db.execSQL("CREATE TABLE " + TABLE_EVENT + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "eventName TEXT, " +
                "eventDate TEXT, " +
                "notes TEXT)");

        // LEADERBOARD
        db.execSQL("CREATE TABLE " + TABLE_LEADERBOARD + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "score INTEGER)");

        // SCREEN TIME
        db.execSQL("CREATE TABLE " + TABLE_SCREEN_TIME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT, " +
                "duration INTEGER)");

        // SPLIT BILL
        db.execSQL("CREATE TABLE " + TABLE_SPLIT_BILL + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "description TEXT, " +
                "date TEXT, " +
                "totalAmount REAL, " +
                "personCount INTEGER, " +
                "resultPerPerson REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEADERBOARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCREEN_TIME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPLIT_BILL);
        onCreate(db);
    }

    // -------------------- EVENTS --------------------
    public boolean insertEvent(String name, String date, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("eventName", name);
        cv.put("eventDate", date);
        cv.put("notes", notes);
        return db.insert(TABLE_EVENT, null, cv) != -1;
    }

    public Cursor getAllEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_EVENT, null);
    }

    // [BARU] Fungsi untuk menghapus Event berdasarkan ID
    public boolean deleteEvent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EVENT, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    // -------------------- LEADERBOARD --------------------
    public void addScore(String username, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("score", score);
        db.insert(TABLE_LEADERBOARD, null, cv);
    }

    public Cursor getLeaderboard() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_LEADERBOARD + " ORDER BY score DESC", null);
    }

    // -------------------- SCREEN TIME --------------------
    public void addScreenTime(String date, int duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", date);
        cv.put("duration", duration);
        db.insert(TABLE_SCREEN_TIME, null, cv);
    }

    public Cursor getScreenTime() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SCREEN_TIME, null);
    }

    // -------------------- SPLIT BILL --------------------
    public boolean addSplitBill(String desc, String date, double total, int persons, double each) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("description", desc);
        cv.put("date", date);
        cv.put("totalAmount", total);
        cv.put("personCount", persons);
        cv.put("resultPerPerson", each);
        return db.insert(TABLE_SPLIT_BILL, null, cv) != -1;
    }

    public Cursor getSplitBillHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SPLIT_BILL, null);
    }
}