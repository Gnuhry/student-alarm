package com.example.studentalarm.weather;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class BadWeatherCheck {
    private static final String LOG = "BadWeatherCheck";
    int minTemp=10;
    String zipCode= "11011";

    public BadWeatherCheck(String zipCode){
        Log.d(LOG, "Initialised");
        this.zipCode= zipCode;
    }

    public BadWeatherCheck(int minTemp, String zipCode){
        this.minTemp=minTemp;
        this.zipCode=zipCode;
    }

    /**
     * Checks if Weather is bad (matches given criteria) in the given region
     *
     * @return Boolean True if the weather is bad or an error occurred, false if is good
     */
    public Boolean isTheWeatherBad(Date time) throws JSONException {
        Log.d(LOG, "Check started");
        Log.d(LOG,"Zipcode: " + zipCode);
        if (zipCode.length()==5){
            String queryString = "http://api.openweathermap.org/data/2.5/forecast?zip=" + zipCode +
                    ",DE&lang=de&units=metric&appid=12bad8fdc76717005759481358561d3b";
            Log.i(LOG, "Query: " + queryString);
            JSONObject weatherJson = null;
            try {
                String apiReturn = runSynchronous(queryString);
                if (apiReturn != null) {
                    weatherJson = new JSONObject(apiReturn);
                } else
                    return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (weatherJson != null) {
                JSONArray optJSONArray = weatherJson.optJSONArray("list");
                if (optJSONArray != null) {
                    for (int pos = 0; pos < optJSONArray.length(); pos++) {
                        JSONObject firstTime = optJSONArray.getJSONObject(pos);
                        Log.d(LOG, "JsonObject1" + firstTime);
                        JSONObject secondTime = JSONObjectfromArray(optJSONArray, pos + 1);
                        Log.d(LOG, "JsonObject2" + secondTime);
                        if (firstTime != null && secondTime == null || firstTime != null && new Date((Integer) firstTime.get("dt")).before(time) && new Date((Integer) secondTime.get("dt")).after(time)) {
                            return firstTime.optJSONObject("main").optLong("feels_like") < 10 || firstTime.optJSONObject("weather").optLong("id") < 800;
                        }
                    }
                }
            }
        }
        return false;
    }

    private JSONObject JSONObjectfromArray(JSONArray jsonArray,int position){
        try {
            return jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get file synchronous from the internet
     *
     * @param link web link
     */
    @Nullable
    public static String runSynchronous(@NonNull String link) {
        Log.d(LOG, "runSynchronous: " + link);
        Request request = new Request.Builder()
                .url(link)
                .build();

        try (Response response = new OkHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful())
                Log.e("Synchronous", "Unexpected code " + response);
            ResponseBody body = response.body();
            if (body != null) {
                return body.string();
            } else
                Log.e("Synchronous", "No body");
        } catch (IOException e) {
            Log.e("Synchronous", "failed");
        }
        return null;
    }

}
