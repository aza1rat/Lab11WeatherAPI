package com.example.lab11_weatherapikashitsin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DBHistory extends SQLiteOpenHelper {
    public DBHistory(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        String sql = "CREATE TABLE WeatherHistory (id INT primary key, date TEXT, city TEXT, tempc REAL, feels REAL, condText TEXT);";
        sqlDB.execSQL(sql);
    }

    public int getMaxId()
    {
        String sql = "SELECT MAX(id) FROM  WeatherHistory;";
        SQLiteDatabase sqlDB  = getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst())
            return cursor.getInt(0);
        return 0;
    }

    public void addHistory(int id, Date date, String city, float temp, float feel, String condText)//Кашицын,393
    {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy HH:mm:ss", Locale.getDefault());
        String sql = "INSERT INTO WeatherHistory VALUES (" + String.valueOf(id) + ", '" + dateFormat.format(date) + "'," +
                "'"+ city + "', " + String.valueOf(temp) +", " + String.valueOf(feel) +", '" + condText +"');";
        SQLiteDatabase sqlDB = getWritableDatabase();
        sqlDB.execSQL(sql);
    }

    public void deleteHistory()
    {
        String sql = "DELETE FROM WeatherHistory";
        SQLiteDatabase sqlDB = getWritableDatabase();
        sqlDB.execSQL(sql);
    }


    @SuppressLint("SimpleDateFormat")
    public void getAllHistory(ArrayList<History> list)//Кашицын,393
    {
        String sql = "SELECT * FROM WeatherHistory;";
        SQLiteDatabase sqlDB = getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery(sql,null);
        if (cursor.moveToFirst() == true)
        {
            do {
                History history = new History();
                try {
                    history.date = new SimpleDateFormat("dd.MM.yyy HH:mm:ss").parse(cursor.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                history.city = cursor.getString(2);
                history.temp = cursor.getFloat(3);
                history.feels = cursor.getFloat(4);
                history.condText = cursor.getString(5);
                list.add(history);
            }while (cursor.moveToNext());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
