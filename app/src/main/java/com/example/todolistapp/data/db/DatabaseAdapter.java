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
    public static final String TASK_TABLE = "task";
    public static final String TASK_ID = "id";
    public static final String TASK_TITLE = "title";
    public static final String TASK_TASK = "task";
    public static final String TASK_STATUS = "status";


    // Create table statements
    private static final String CREATE_TASK_TABLE = "CREATE TABLE " + TASK_TABLE + "(" + TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_TITLE + " TEXT, "
            + TASK_TASK + " TEXT, "
            + TASK_STATUS + " INTEGER)";

    public DatabaseAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private SQLiteDatabase db;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create the tables
        sqLiteDatabase.execSQL(CREATE_TASK_TABLE);
    }

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

    // Insert a new task into the database
    public void insertTask(Task task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_TITLE, task.getTitle());
        cv.put(TASK_TASK, task.getTask());
        cv.put(TASK_STATUS, task.getStatus());
        db.insert(TASK_TABLE, null, cv);
    }

    public void updateTask(int id, String title, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_TITLE, title);
        cv.put(TASK_TASK, task);
        db.update(TASK_TABLE, cv, TASK_ID + "=?", new String[]{String.valueOf(id)});

    }


    public void deleteTask(int id) {
        db.delete(TASK_TABLE, TASK_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void updateStatus(int id, int status) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_STATUS, status);
        db.update(TASK_TABLE, cv, TASK_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Get all tasks from the database
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        db.beginTransaction();
        try (Cursor cur = db.query(TASK_TABLE, null, null, null, null, null, null, null)) {
            // Query all rows from the todo table
            if (cur != null) {
                if (cur.moveToFirst()) {
                    int idIndex = cur.getColumnIndex(TASK_ID);
                    int titleIndex = cur.getColumnIndex(TASK_TITLE);
                    int statusIndex = cur.getColumnIndex(TASK_STATUS);
                    int taskIndex = cur.getColumnIndex(TASK_TASK);

                    do {
                        // Create a Task object from the retrieved data
                        Task task = new Task();
                        task.setId(cur.getInt(idIndex));
                        task.setTitle(cur.getString(titleIndex));
                        task.setTask(cur.getString(taskIndex));
                        task.setStatus(cur.getInt(statusIndex));
                        taskList.add(task); // Add the task to the list
                    } while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction(); // End the transaction
            // Close the cursor
        }
        return taskList;
    }
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

}
