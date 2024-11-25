package com.example.todo.utlis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.todo.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DataBaseName = "TodoDatabase";
    private static final int data_baseVersion = 1;
    private static final String tablename = "todolist";
    private static final String col1 = "ID";
    private static final String col2 = "TASK";
    private static final String col3 = "STATUS";

    public DataBaseHelper(Context context) {
        super(context, DataBaseName, null, data_baseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + tablename + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TASK TEXT, STATUS INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tablename);
        onCreate(sqLiteDatabase);
    }

    public void inserttask(ToDoModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col2, model.getTask());
        contentValues.put(col3, 0); // New tasks are assumed to have status 0 (not completed)
        db.insert(tablename, null, contentValues);
        db.close(); // Close the database after the operation
    }

    public void updateTask(int id, String task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col2, task);
        // Corrected SQL query string; use "ID = ?" instead of "ID + ?"
        db.update(tablename, contentValues, "ID = ?", new String[]{String.valueOf(id)});
        db.close(); // Close the database after the operation
    }

    public void updateStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col3, status);
        // Corrected SQL query string; use "ID = ?" instead of "ID + ?"
        db.update(tablename, contentValues, "ID = ?", new String[]{String.valueOf(id)});
        db.close(); // Close the database after the operation
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Corrected SQL query string; use "ID = ?" instead of "ID + ?"
        db.delete(tablename, "ID = ?", new String[]{String.valueOf(id)});
        db.close(); // Close the database after the operation
    }

    public List<ToDoModel> getalltasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        List<ToDoModel> modellist = new ArrayList<>();
        db.beginTransaction();
        try {
            cursor = db.query(tablename, null, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        ToDoModel toDoModel = new ToDoModel();
                        toDoModel.setId(cursor.getInt(cursor.getColumnIndex(col1)));
                        toDoModel.setTask(cursor.getString(cursor.getColumnIndex(col2)));
                        toDoModel.setStatus(cursor.getInt(cursor.getColumnIndex(col3)));
                        modellist.add(toDoModel);
                    } while (cursor.moveToNext());
                }
            }
            db.setTransactionSuccessful(); // Mark the transaction as successful
        } finally {
            if (cursor != null) {
                cursor.close(); // Close the cursor
            }
            db.endTransaction(); // End the transaction
            db.close(); // Close the database after the operation
        }
        return modellist;
    }
}
