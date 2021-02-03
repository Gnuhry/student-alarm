package com.example.studentalarm.alarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.weather.BadWeatherCheck;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import org.json.JSONException;

public class AlarmManager {

    private static final String LOG = "AlarmManager";
    private static int before, way, after;
    private static boolean alarmPhone, init = true;

    /**
     * Set the next alarm
     *
     * @param context context of the application
     */
    public static void setNextAlarm(@NonNull Context context) {
        Log.d(LOG, "set next alarm");
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.ALARM_ON, false)) {
            Log.d(LOG, "alarm on");
            LectureSchedule.Lecture first = LectureSchedule.load(context).getNextLecture(context);
            if (first != null) {
                Date date = first.getStartWithDefaultTimeZone();
                try {
                    Log.d(LOG, "Bad Weather Check");
                    if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.WAKE_WEATHER,true) && new BadWeatherCheck(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.ZIPCODE,"11011")).isTheWeatherBad(first.getStartWithDefaultTimeZone())) {
                        Log.d(LOG, "Bad Weather");
                        date.setTime(date.getTime() - 60000 * Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.WAKE_WEATHER_TIME,"10")));
                    }
                    setAlarm(date, context);
                } catch (JSONException e) {
                    e.printStackTrace();
                    setAlarm(date, context);
                }
            }
        }
    }

    /**
     * Update the next alarm, if settings changed
     *
     * @param context context of the application
     */
    public static void updateNextAlarm(@NonNull Context context) {
        Log.d(LOG, "update alarm");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!init && before == preferences.getInt(PreferenceKeys.BEFORE, 0) &&
                way == preferences.getInt(PreferenceKeys.WAY, 0) &&
                after == preferences.getInt(PreferenceKeys.AFTER, 0) &&
                alarmPhone == preferences.getBoolean(PreferenceKeys.ALARM_PHONE, false)) {
            init = false;
            return;
        }
        before = preferences.getInt(PreferenceKeys.BEFORE, 0);
        way = preferences.getInt(PreferenceKeys.WAY, 0);
        after = preferences.getInt(PreferenceKeys.AFTER, 0);
        alarmPhone = preferences.getBoolean(PreferenceKeys.ALARM_PHONE, false);
        cancelNextAlarm(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                AlarmManager.setNextAlarm(context);
            }
        }).start();
    }

    /**
     * Update the next alarm after import
     *
     * @param context context of the application
     */
    public static void updateNextAlarmAfterAutoImport(@NonNull Context context) {
        Log.d(LOG, "update alarm after auto import");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean(PreferenceKeys.ALARM_CHANGE, false)) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                AlarmManager.setNextAlarm(context);
            }
        }).start();
    }

    /**
     * Cancel Alarm
     *
     * @param context context of the application
     */
    public static void cancelNextAlarm(@NonNull Context context) {
        Log.d(LOG, "cancel alarm");
        Alarm.cancelAlarm(context);
    }

    /**
     * Set the alarm at date
     *
     * @param date    date where the alarm should trigger
     * @param context context of the application
     */
    private static void setAlarm(@NonNull Date date, @NonNull Context context) {
        Log.d(LOG, "Set alarm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        calendar.add(Calendar.MINUTE, -preferences.getInt(PreferenceKeys.BEFORE, 0));
        calendar.add(Calendar.MINUTE, -preferences.getInt(PreferenceKeys.WAY, 0));
        calendar.add(Calendar.MINUTE, -preferences.getInt(PreferenceKeys.AFTER, 0));
        if (preferences.getBoolean(PreferenceKeys.ALARM_PHONE, false)) {
            Alarm.setPhoneAlarm(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), context);
        } else {
            cancelNextAlarm(context);
            Alarm.setAlarm(calendar, context);
        }
    }

}
