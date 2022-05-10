package com.example.lab11_weatherapikashitsin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DBImages extends SQLiteOpenHelper {
    public DBImages(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        String sql = "CREATE TABLE WeatherImages (id INT primary key);";
        sqlDB.execSQL(sql);
    }

    public boolean isPlacedInDB(int id) //Кашицын,393
    {
        String sql = "SELECT id FROM  WeatherImages WHERE id = "+ String.valueOf(id) +";";
        SQLiteDatabase sqlDB  = getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst())
            return true;
        return false;
    }

    public void addHistory(int id)
    {
        String sql = "INSERT INTO WeatherImages VALUES (" + String.valueOf(id) + ");";
        SQLiteDatabase sqlDB = getWritableDatabase();
        sqlDB.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
