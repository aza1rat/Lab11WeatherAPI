package com.example.lab11_weatherapikashitsin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Trace;
import android.util.JsonReader;
import android.util.Log;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventListener;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    EditText inputAPI;
    EditText inputCity;
    TextView tvTemp;
    ImageView imgWeather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db.dbHistory = new DBHistory(this, "weatherHty.db", null, 1);
        db.dbImages = new DBImages(this, "weatherImg.db", null, 1);
        inputAPI = findViewById(R.id.input_api);
        inputCity = findViewById(R.id.input_city);
        tvTemp = findViewById(R.id.tv_temp);
        imgWeather = findViewById(R.id.img_weather);
    }

    public void onQueryClick(View v)//Кашицын,393
    {

        String key = inputAPI.getText().toString();
        String city = inputCity.getText().toString();
        Thread t = new Thread(() ->
        {
           try {
               Bitmap bmp;
               URL url = new URL("http://api.weatherapi.com/v1/current.json?key=" + key + "&q=" + city + "&aqi=no");
               HttpURLConnection con = (HttpURLConnection) url.openConnection();
               if (con.getResponseCode() != 200)
               {
                   if (con.getResponseCode() == 400)
                       throw new Exception("По вашему запросу ничего не найдено");
                   throw new Exception("Ошибка подключения");
               }
               InputStream is = con.getInputStream();
               byte[] buf = new byte[1024];
               String res = "";
               while (true)
               {
                   int len = is.read(buf, 0, buf.length);
                   if (len < 0) break;
                   res = res + new String(buf, 0, len);
               }
               con.disconnect();
               JSONObject current = convertToJSON(new JSONObject(res), "current");//Кашицын,393
               JSONObject condition = convertToJSON(current, "condition");
               JSONObject location = convertToJSON(new JSONObject(res), "location");

               if (db.dbImages.isPlacedInDB(condition.getInt("code")) && isFileExist(condition.getString("code") + ".png"))
               {
                   String directory = Environment.getExternalStorageDirectory().toString() + "/weatherImages/" +
                           condition.getString("code") + ".png";
                   bmp = BitmapFactory.decodeFile(directory);
               }
               else
               {
                   String icon = condition.getString("icon");
                   URL url1 = new URL("http:" + icon);
                   HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
                   InputStream is1 = con1.getInputStream();
                   bmp = BitmapFactory.decodeStream(is1);
                   con1.disconnect();
                   saveImage(bmp, condition.getString("code") + ".png");
                   db.dbImages.addHistory(condition.getInt("code"));
               }

               runOnUiThread(() -> {
                   imgWeather.setImageBitmap(bmp); //Кашицын, 393
               });
               Intent intent = new Intent(this, QueryResult.class);
               intent.putExtra("city", location.getString("name"));
               intent.putExtra("condText", condition.getString("text"));
               intent.putExtra("condCode", condition.getInt("code"));
               intent.putExtra("country", location.getString("country"));
               intent.putExtra("localTime", location.getString("localtime"));
               intent.putExtra("lastUpdate", current.getString("last_updated"));
               intent.putExtra("temp", (float) current.getDouble("temp_c"));
               intent.putExtra("windSpeed", (float) current.getDouble("wind_kph"));
               intent.putExtra("pressure", (float) current.getDouble("pressure_mb"));
               intent.putExtra("feels", (float) current.getDouble("feelslike_c"));
               startActivity(intent);
           }
           catch (Exception ex)
           {
               ex.printStackTrace();
               runOnUiThread(() -> {
                   Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
               });
           }
        });
        t.start();

    }

    public void onHistoryClick(View v)//Кашицын,393
    {
        AlertDialog dlg = makeDialog("История запросов");
        makeChoiceLayout(dlg);
        dlg.show();
    }

    public void onClearTable(View v)
    {
        db.dbHistory.deleteHistory();
    }

    public JSONObject convertToJSON(JSONObject res, String select)//Кашицын,393
    {
        JSONObject doc = null;
        try {
            JSONObject current = res.getJSONObject(select);
            return current;
        }
        catch (Exception e)
        {
            return doc;
        }
    }

    public void saveImage(Bitmap bitmap, String name)
    {
        String directory = Environment.getExternalStorageDirectory().toString() + "/weatherImages";
        File imgDir = new File(directory);
        if (!imgDir.exists())
            imgDir.mkdirs();
        File img = new File(imgDir, name);
        try{
            FileOutputStream outputStream = new FileOutputStream(img);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isFileExist(String str)
    {
        try {
            String directory = Environment.getExternalStorageDirectory().toString() + "/weatherImages/" + str;
            File img = new File(directory);
            if (img.exists())
                return true;
        }
        catch (Exception e){
            return false;
        }
        return false;
    }


    AlertDialog makeDialog(String str)//Кашицын,393
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dlg = builder.create();
        dlg.setTitle(str);
        return  dlg;
    }

    void makeChoiceLayout(AlertDialog dlg)
    {
        LinearLayout layout = new LinearLayout(getBaseContext());
        layout.setOrientation(layout.VERTICAL);
        ListView lw = new ListView(getBaseContext());
        ArrayList<History> arrayList = new ArrayList<History>();
        db.dbHistory.getAllHistory(arrayList);

        ArrayAdapter<History> arrayAdapter = new ArrayAdapter<History>(this, android.R.layout.simple_list_item_1, arrayList);
        lw.setAdapter(arrayAdapter);
        layout.addView(lw);
        dlg.setView(layout);
    }


}