package core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Дмитрий on 10.12.2016.
 */

public class LocalStorage extends SQLiteOpenHelper {

    private static final String name = "javalearn.sqlite";
    private static final int version = 1;
    private String error;
    public String getError() {
        return error;
    }

    public LocalStorage(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createSectionsTable = "CREATE TABLE sections (" +
                "_id VARCHAR (24) NOT NULL PRIMARY KEY," +
                "title TEXT NOT NULL," +
                "description TEXT NOT NULL," +
                "roworder INT NOT NULL )";
        String createChaptersTable = "CREATE TABLE chapters (" +
                "_id VARCHAR (24) PRIMARY KEY NOT NULL," +
                "title TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "id_section VARCHAR (24) REFERENCES sections (_id) ON DELETE RESTRICT ON UPDATE RESTRICT NOT NULL," +
                "roworder INT NOT NULL )";
        String createChangesTable = "CREATE TABLE changes (" +
                "_id VARCHAR (24) PRIMARY KEY NOT NULL," +
                "guid TEXT NOT NULL )";
        try {
            sqLiteDatabase.execSQL(createSectionsTable);
            sqLiteDatabase.execSQL(createChaptersTable);
            sqLiteDatabase.execSQL(createChangesTable);
        }
        catch (SQLiteException ex) {
            error = ex.getMessage();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    public ArrayList<ArrayList<Object>> load (String [] fields, String table) {
        ArrayList<ArrayList<Object>> items = new ArrayList<ArrayList<Object>>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(table, fields, null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                ArrayList<Object> currentItem = new ArrayList<Object>();
                for(int i = 0; i < fields.length; i++)
                    currentItem.add(cursor.getString(i));
                items.add(currentItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }

    public ArrayList<Object> loadFirst (String [] fields, String table) {
        ArrayList<Object> currentItem = new ArrayList<Object>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(table, fields, null, null, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            for(int i = 0; i < fields.length; i++)
                currentItem.add(cursor.getString(i));
        }
        cursor.close();
        db.close();
        return currentItem;
    }

    public boolean insert (ContentValues values, String table) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.insert(table, null, values);
            db.close();
            return true;
        } catch (Exception ex) {
            error = ex.getMessage();
            db.close();
            return false;
        }
    }

    public boolean update (ContentValues values, String table, KeyValuePair<String, String> keyItem) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.update(table, values, keyItem.getKey() + " = ?", new String[]{keyItem.getValue()});
            db.close();
            return true;
        } catch (Exception ex) {
            error = ex.getMessage();
            db.close();
            return false;
        }
    }

    public boolean remove (String table, KeyValuePair<String, String> keyItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(table, keyItem.getKey() + " = ?", new String[]{ keyItem.getValue() });
            db.close();
            return true;
        }
        catch (Exception ex) {
            error = ex.getMessage();
            return false;
        }
    }

    public boolean clear (String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(table, null, null);
        db.close();
        return true;
    }

    public boolean tableExist (String table) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + table + "'", null);
        if(cursor == null) {
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();
        Cursor cursorRowsCount = db.rawQuery("SELECT count(*) FROM " + table, null);
        if(cursorRowsCount == null) {
            cursorRowsCount.close();
            db.close();
            return false;
        }
        cursorRowsCount.moveToFirst();
        int result = cursorRowsCount.getInt(0);
        if(result == 0) {
            cursorRowsCount.close();
            db.close();
            return false;
        }
        cursorRowsCount.close();
        db.close();
        return true;
    }
}
