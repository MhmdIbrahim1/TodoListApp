package com.example.todolistapp.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todolistapp.data.models.Subtask;
import com.example.todolistapp.data.models.Task;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 1;

    // Task table name and columns
    public static final String TASK_TABLE = "task";
    public static final String TASK_ID = "id";
    public static final String TASK_TITLE = "title";

    // Subtask table name and columns
    public static final String SUBTASK_TABLE = "subtask";
    public static final String SUBTASK_ID = "id";
    public static final String SUBTASK_TITLE = "title";
    public static final String IS_DONE = "is_done";

    // Create table statements
    private static final String CREATE_TASK_TABLE = "CREATE TABLE " + TASK_TABLE + "(" +
            TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TASK_TITLE + " TEXT)";

    private static final String CREATE_SUBTASK_TABLE = "CREATE TABLE " + SUBTASK_TABLE + "(" +
            SUBTASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SUBTASK_TITLE + " TEXT, " +
            IS_DONE + " INTEGER, " +
            "FOREIGN KEY (" + TASK_ID + ") REFERENCES " + TASK_TABLE + "(" + TASK_ID + "))";

    public DatabaseAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private SQLiteDatabase db;

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create the tables
        sqLiteDatabase.execSQL(CREATE_TASK_TABLE);
        sqLiteDatabase.execSQL(CREATE_SUBTASK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop the tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SUBTASK_TABLE);

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
    public void insertTask(String title) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_TITLE, title);
        db.insert(TASK_TABLE, null, cv);
    }

    // Insert a new subtask into the database
    public void insertSubtask(Subtask subtask, int taskId) {
        ContentValues cv = new ContentValues();
        cv.put(SUBTASK_TITLE, subtask.getTitle());
        cv.put(IS_DONE, subtask.isDone());
        cv.put(TASK_ID, taskId);
        db.insert(SUBTASK_TABLE, null, cv);
    }

    // Update a task in the database
    public void updateTask(int id, String title) {
        ContentValues cv = new ContentValues();
        cv.put(TASK_TITLE, title);
        db.update(TASK_TABLE, cv, TASK_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Update a subtask in the database
    public void updateSubtask(int id, String title) {
        ContentValues cv = new ContentValues();
        cv.put(SUBTASK_TITLE, title);
        db.update(SUBTASK_TABLE, cv, SUBTASK_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void updateIsDone(int id, boolean isDone) {
        ContentValues cv = new ContentValues();
        cv.put(IS_DONE, isDone);
        db.update(SUBTASK_TABLE, cv, SUBTASK_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Delete a task from the database
    public void deleteTask(int id) {
        db.delete(TASK_TABLE, TASK_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Delete a subtask from the database
    public void deleteSubtask(int id) {
        db.delete(SUBTASK_TABLE, SUBTASK_ID + "=?", new String[]{String.valueOf(id)});
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
                    do {
                        // Create a Task object from the retrieved data
                        Task task = new Task();
                        task.setId(cur.getInt(idIndex));
                        task.setTitle(cur.getString(titleIndex));
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

    // Get all subtasks from the database
    public List<Subtask> getAllSubtasks(int taskId) {
        List<Subtask> subtaskList = new ArrayList<>();
        db.beginTransaction();
        try (Cursor cur = db.query(SUBTASK_TABLE, null, null, null, null, null, null, null)) {
            // Query all rows from the todo table
            if (cur != null) {
                if (cur.moveToFirst()) {
                    int idIndex = cur.getColumnIndex(SUBTASK_ID);
                    int titleIndex = cur.getColumnIndex(SUBTASK_TITLE);
                    int isDoneIndex = cur.getColumnIndex(IS_DONE);
                    do {
                        // Create a Subtask object from the retrieved data
                        Subtask subtask = new Subtask();
                        subtask.setId(cur.getInt(idIndex));
                        subtask.setTitle(cur.getString(titleIndex));
                        subtask.setDone(cur.getInt(isDoneIndex) == 1);
                        subtaskList.add(subtask); // Add the subtask to the list
                    } while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction(); // End the transaction
            // Close the cursor
        }
        return subtaskList;
    }


}
