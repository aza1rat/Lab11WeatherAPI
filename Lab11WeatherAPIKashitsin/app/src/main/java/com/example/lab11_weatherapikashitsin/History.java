package com.example.lab11_weatherapikashitsin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class History { //Кашицын,393
    public  Date date;
    public  String city;
    public  float temp;
    public  float feels;
    public  String condText;

    public String toString()
    {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy HH:mm:ss", Locale.getDefault());
        return "[" + dateFormat.format(date) + "] " + city + " - " + temp + "C (" + feels + ") " + condText;
    }
}
