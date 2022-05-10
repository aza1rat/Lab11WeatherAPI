package com.example.lab11_weatherapikashitsin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class QueryResult extends AppCompatActivity {
    TextView labelCity;
    TextView labelCondText;
    TextView labelCountry;
    TextView labelLocalTime;
    TextView labelLastUpd;
    TextView labelTemp;
    TextView labelFeels;
    TextView labelWind;
    TextView labelPress;
    ImageView imgWeather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {//Кашицын,393
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_result);

        labelCondText = findViewById(R.id.tv_condText);
        labelCountry = findViewById(R.id.tv_country);
        labelLocalTime = findViewById(R.id.tv_time);
        labelCity = findViewById(R.id.tv_currentCity);
        labelTemp = findViewById(R.id.tv_tempc);
        labelFeels = findViewById(R.id.tv_feelstemp);
        labelWind = findViewById(R.id.tv_windSpeed);
        labelPress = findViewById(R.id.tv_pressure);
        labelLastUpd = findViewById(R.id.tv_lastUpdated);
        imgWeather = findViewById(R.id.img_weather2);

        Intent intent = getIntent();

        labelCondText.setText("Состояние погоды: " + intent.getStringExtra("condText"));
        labelCountry.setText("Страна: " + intent.getStringExtra("country"));
        labelLocalTime.setText("Время: " + intent.getStringExtra("localTime"));
        labelCity.setText("Город: " + intent.getStringExtra("city"));
        labelTemp.setText("Температура по C:" + String.valueOf(intent.getFloatExtra("temp", 0.0f)));
        labelFeels.setText("Чувствуется как: " + String.valueOf(intent.getFloatExtra("feels", 0.0f)));
        labelWind.setText("Скорость ветра: " + String.valueOf(intent.getFloatExtra("windSpeed", 0.0f)));
        labelPress.setText("Давление (мм р.с.): " + String.valueOf(intent.getFloatExtra("pressure", 0.0f)));
        labelLastUpd.setText("Актуальные данные на: " + intent.getStringExtra("lastUpdate"));
        String directory = Environment.getExternalStorageDirectory().toString() + "/weatherImages/" +
                intent.getIntExtra("condCode", 0) + ".png";
        Bitmap bmp = BitmapFactory.decodeFile(directory);
        imgWeather.setImageBitmap(bmp); //Кашицын,393
        db.dbHistory.addHistory(db.dbHistory.getMaxId() + 1, Calendar.getInstance().getTime(), intent.getStringExtra("city"),
                intent.getFloatExtra("temp", 0.0f), intent.getFloatExtra("feels", 0.0f),
                intent.getStringExtra("condText"));
    }
}