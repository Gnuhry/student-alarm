package com.example.studentalarm.alarm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.receiver.AlarmReceiver;
import com.example.studentalarm.receiver.SetAlarmLater;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.weather.BadWeatherCheck;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
                Date checkdate = new Date(date.getTime());
                try {
                    checkdate.setTime(date.getTime() - (60000 * (30 + Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.WAKE_WEATHER_TIME, "10")))));//+30 min before the Alarm happens in the worst case (Bad Weather) 60000factor second to millisecond
                    if (checkdate.before(Calendar.getInstance().getTime())) {
                        Log.d(LOG, "Bad Weather Check");
                        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.WAKE_WEATHER, true) && new BadWeatherCheck(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.ZIPCODE, "11011")).isTheWeatherBad(first.getStartWithDefaultTimeZone())) {
                            Log.d(LOG, "Bad Weather");
                            date.setTime(date.getTime() - 60000 * Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.WAKE_WEATHER_TIME, "10")));
                        }
                        setAlarm(date.getTime(), context);
                    } else {
                        setAlarm(date.getTime(), context);
                        ((android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).set(android.app.AlarmManager.RTC_WAKEUP, checkdate.getTime(), PendingIntent.getBroadcast(context, 0, new Intent(context, SetAlarmLater.class), 0));
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PreferenceKeys.WAKE_WEATHER_CHECK_TIME, checkdate.getTime()).apply();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    setAlarm(date.getTime(), context);
                }
            }
        }
    }

    /**
     * Update the next alarm, called from SetAlarmLater
     *
     * @param context context of the application
     */
    public static void updateNextAlarmFromSetAlarmLater(@NonNull Context context) {
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
        new Thread(() -> {
            Log.d(LOG, "set next alarm");
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.ALARM_ON, false)) {
                Log.d(LOG, "alarm on");
                LectureSchedule.Lecture first = LectureSchedule.load(context).getNextLecture(context);
                if (first != null) {
                    Date date = first.getStartWithDefaultTimeZone();
                    try {
                        Log.d(LOG, "Bad Weather Check");
                        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.WAKE_WEATHER, true) && new BadWeatherCheck(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.ZIPCODE, "11011")).isTheWeatherBad(first.getStartWithDefaultTimeZone())) {
                            Log.d(LOG, "Bad Weather");
                            date.setTime(date.getTime() - 60000 * Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.WAKE_WEATHER_TIME, "10")));
                        }
                        setAlarm(date.getTime(), context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        setAlarm(date.getTime(), context);
                    }
                }
            }

        }).start();
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
        new Thread(() -> AlarmManager.setNextAlarm(context)).start();
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
        new Thread(() -> AlarmManager.setNextAlarm(context)).start();
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
    private static void setAlarm(long date, @NonNull Context context) {
        Log.d(LOG, "Set alarm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        calendar.add(Calendar.MINUTE, -preferences.getInt(PreferenceKeys.BEFORE, 0));
        calendar.add(Calendar.MINUTE, -preferences.getInt(PreferenceKeys.WAY, 0));
        calendar.add(Calendar.MINUTE, -preferences.getInt(PreferenceKeys.AFTER, 0));

        Calendar time2 = Calendar.getInstance();
        time2.add(Calendar.HOUR, -TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET));
        Log.e("TIME", calendar.toString() + ", " + time2.toString());
        if (time2.before(calendar)) {
            if (preferences.getBoolean(PreferenceKeys.ALARM_PHONE, false)) {
                Alarm.setPhoneAlarm(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), context);
            } else {
                cancelNextAlarm(context);
                Alarm.setAlarm(calendar, context);
            }
        } else {
            Log.e(LOG, "Wrong Date is set for alarm. Date is before current date");
        }
    }

}


