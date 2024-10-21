package org.o7planning.myapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mp3Manager";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_MP3 = "mp3_files";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PATH = "path";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MP3_TABLE = "CREATE TABLE " + TABLE_MP3 + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PATH + " TEXT" + ")";
        db.execSQL(CREATE_MP3_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MP3);
        onCreate(db);
    }

    // Thêm file mp3
    public void addMp3File(String name, String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PATH, path);
        db.insert(TABLE_MP3, null, values);
        db.close();
    }

    // Xoá file mp3
    public void deleteMp3File(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MP3, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Lấy danh sách file mp3
    public List<String> getAllMp3Files() {
        List<String> mp3List = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MP3;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                mp3List.add(cursor.getString(1));  // Cột tên file
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mp3List;
    }
}