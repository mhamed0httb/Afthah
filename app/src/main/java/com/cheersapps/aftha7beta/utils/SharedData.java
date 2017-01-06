package com.cheersapps.aftha7beta.utils;

import android.content.Context;
import android.os.StrictMode;


import com.cheersapps.aftha7beta.entity.Message;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mohamed on 12/28/2016.
 */

public class SharedData {

    Date now;

    public SharedData(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Request request = new Request.Builder()
                    .url("http://www.timeapi.org/utc/now")
                    .build();
            Response responses = null;
            try {
                responses = new OkHttpClient().newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String stringValue = null; // returned String value
            try {
                stringValue = responses.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date date = null;
            try {
                date = sdf.parse(stringValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long timeInMilliseconds = date.getTime(); // converted time milis

         this.daten = stringValue;



    }

    public static List<String> currentWhatHeSaid=new ArrayList<>();
    public static List<String> currentWhatISaid=new ArrayList<>();
    public static List<Message> old=new ArrayList<>();
    public static Context context;
    public static String daten;
}
