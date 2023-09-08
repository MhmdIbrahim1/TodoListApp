package com.example.todolistapp.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todolistapp.data.models.Task;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 2;


    // Task table name and columns
    public static final String TASK_DATE = "date";
    public static final String TASK_TABLE = "task";
    public static final String TASK_ID = "id";
    public static final String TASK_TITLE = "title";
    public static final String TASK_TASK = "task";
    public static final String TASK_STATUS = "status";

    // Constants for column names
    private static final String COLUMN_TITLE = TASK_TITLE;
    private static final String COLUMN_TASK = TASK_TASK;
    private static final String COLUMN_DATE = TASK_DATE;
    private static final String COLUMN_STATUS = TASK_STATUS;



    // Create table SQL query for the task table
    private static final String CREATE_TASK_TABLE = "CREATE TABLE " + TASK_TABLE + "(" + TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_TITLE + " TEXT, "
            + TASK_TASK + " TEXT, "
            + TASK_STATUS + " INTEGER, "
            + TASK_DATE + " INTEGER)";

    // Constructor
    public DatabaseAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private SQLiteDatabase db;

    // Create the task table
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create the tables
        sqLiteDatabase.execSQL(CREATE_TASK_TABLE);
    }

    // Upgrade the database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop the tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE);

        // Recreate the tables
        onCreate(sqLiteDatabase);
    }


    /**
     * Open the database for writing.
     */
    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    /**
     * Close the database.
     */

    public void closeDatabase() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    /**
     * Insert a task into the database.
     */
    public void insertTask(Task task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_TITLE, task.getTitle());
        cv.put(TASK_TASK, task.getTask());
        cv.put(TASK_STATUS, task.getStatus());
        cv.put(TASK_DATE, task.getDate());
        db.insert(TASK_TABLE, null, cv);
    }

    /**
     * Update a task in the database.
     */

    public void updateTask(int id, String title, String task, Long date) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_TITLE, title);
        cv.put(TASK_TASK, task);
        cv.put(TASK_DATE, date);
        db.update(TASK_TABLE, cv, TASK_ID + "=?", new String[]{String.valueOf(id)});

    }

    /**
     * Delete a task from the database.
     */

    public void deleteTask(int id) {
        db.delete(TASK_TABLE, TASK_ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * Update the status of a task in the database.
     */
    public void updateStatus(int id, int status) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_STATUS, status);
        db.update(TASK_TABLE, cv, TASK_ID + "=?", new String[]{String.valueOf(id)});
    }


    /**
     * Update the date of a task in the database.
     */

    public void updateDate(int id, Long date) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_DATE, date);
        db.update(TASK_TABLE, cv, TASK_ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * delete all tasks from the database.
     */
    public void deleteAllTasks() {
        if (!db.isOpen()) {
            openDatabase();
        }

        db.beginTransaction();
        try {
            db.delete(TASK_TABLE, null, null);
            db.setTransactionSuccessful(); // Mark the transaction as successful
        } finally {
            db.endTransaction(); // End the transaction
        }
    }

    /**
     * Get all tasks from the database.
     */
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        db.beginTransaction();
        try (Cursor cursor = db.query(TASK_TABLE, null, null, null, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Task task = createTaskFromCursor(cursor);
                    taskList.add(task);
                } while (cursor.moveToNext());
            }
        } finally {
            db.endTransaction();
        }
        return taskList;
    }


    // Method to search for tasks in the database
    public List<Task> searchTasks(String searchQuery) {
        List<Task> taskList = new ArrayList<>();
        db.beginTransaction();
        try (Cursor cursor = db.query(
                TASK_TABLE,
                null,
                COLUMN_TITLE + " LIKE ? OR " + COLUMN_TASK + " LIKE ?",
                new String[]{"%" + searchQuery + "%", "%" + searchQuery + "%"},
                null,
                null,
                null,
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Task task = createTaskFromCursor(cursor);
                    taskList.add(task);
                } while (cursor.moveToNext());
            }
        } finally {
            db.endTransaction();
        }
        return taskList;
    }


    /**
     * Helper method to create a Task object from a Cursor.
     */

    private Task createTaskFromCursor(Cursor cursor) {
        Task task = new Task();
        int idIndex = cursor.getColumnIndex(TASK_ID);
        int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
        int taskIndex = cursor.getColumnIndex(COLUMN_TASK);
        int statusIndex = cursor.getColumnIndex(COLUMN_STATUS);
        int dateIndex = cursor.getColumnIndex(COLUMN_DATE);

        if (idIndex >= 0) {
            task.setId(cursor.getInt(idIndex));
        }

        if (titleIndex >= 0) {
            task.setTitle(cursor.getString(titleIndex));
        }

        if (taskIndex >= 0) {
            task.setTask(cursor.getString(taskIndex));
        }

        if (statusIndex >= 0) {
            task.setStatus(cursor.getInt(statusIndex));
        }

        if (dateIndex >= 0) {
            task.setDate(cursor.getLong(dateIndex));
        }

        return task;
    }


    // to search with title only
//    public List<Task> searchTasks(String searchQuery) {
//        List<Task> taskList = new ArrayList<>();
//        db.beginTransaction();
//        try (Cursor cursor = db.query(
//                TASK_TABLE,
//                null,
//                COLUMN_TITLE + " LIKE ?",
//                new String[]{"%" + searchQuery + "%"},
//                null,
//                null,
//                null,
//                null
//        )) {
//            if (cursor != null && cursor.moveToFirst()) {
//                do {
//                    Task task = createTaskFromCursor(cursor);
//                    taskList.add(task);
//                } while (cursor.moveToNext());
//            }
//        } finally {
//            db.endTransaction();
//        }
//        return taskList;
//    }


}
