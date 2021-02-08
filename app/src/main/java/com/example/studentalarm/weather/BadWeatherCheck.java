package com.example.studentalarm.weather;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.example.studentalarm.imports.Import.runSynchronous;

public class BadWeatherCheck {
    private static final String LOG = "BadWeatherCheck", API_STRING_PART_ONE = "http://api.openweathermap.org/data/2.5/forecast?zip=", API_STRING_PART_TWO = ",DE&lang=de&units=metric&appid=12bad8fdc76717005759481358561d3b";
    private static final int BAD_WEATHER_CONSTANT = 800;//>800 BadWeather definition: https://openweathermap.org/weather-conditions
    private static final int minTemp = 10;
    public static final int DELTA_ALARM_BEFORE = 30;//+30 min before the Alarm happens in the worst case (Bad Weather) 60000factor second to millisecond
    private final String zipCode;

    public BadWeatherCheck(String zipCode) {
        Log.d(LOG, "Initialised");
        this.zipCode = zipCode;
    }

//    public BadWeatherCheck(int minTemp, String zipCode){
//        this.minTemp=minTemp;
//        this.zipCode=zipCode;
//    }

    /**
     * Checks if Weather is bad (matches given criteria) in the given region
     *
     * @param time in UTC
     * @return Boolean {true} if the weather is bad or an error occurred, {false} if is good
     */
    @NonNull
    public Boolean isTheWeatherBad(Date time) {
        Log.d(LOG, "Check started");
        Log.d(LOG, "zip code: " + zipCode);
        if (zipCode.length() == 5) { //TODO validating zip code
            JSONArray optJSONArray;
            try {
                String apiReturn = runSynchronous(API_STRING_PART_ONE + zipCode + API_STRING_PART_TWO);
                if (apiReturn != null)
                    optJSONArray = new JSONObject(apiReturn).optJSONArray("list");
                else
                    return true;
            } catch (JSONException e) {
                e.printStackTrace();
                return true;
            }
            if (optJSONArray != null) {
                for (int pos = 0; pos < optJSONArray.length(); pos++) {
                    JSONObject firstTime = JSONObjectFromArray(optJSONArray, pos);
                    Log.d(LOG, "JsonObject1" + firstTime);
                    JSONObject secondTime = JSONObjectFromArray(optJSONArray, pos + 1);
                    Log.d(LOG, "JsonObject2" + secondTime);
                    if ((firstTime != null && secondTime == null) ||
                            (firstTime != null && new Date(secondTime.optLong("dt") * 1000).after(time))) {
                        JSONObject main = firstTime.optJSONObject("main"),
                                weather = JSONObjectFromArray(firstTime.optJSONArray("weather"),0);
                        Log.d(LOG, "Found the object: " + main +"  "+weather);
                        if (main != null && weather != null) {
                            return main.optLong("feels_like") < minTemp || weather.optLong("id") < BAD_WEATHER_CONSTANT;
                        }else
                            Log.w(LOG, "Found the object but without the correct attributes");
                    }
                }
            }
        }
        return true;
    }

    @Nullable
    private static JSONObject JSONObjectFromArray(@NonNull JSONArray jsonArray, int position) {
        try {
            return jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            Log.e(LOG,"Json object array out of range");
            //e.printStackTrace();
        }
        return null;
    }

}
