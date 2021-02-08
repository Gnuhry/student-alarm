package com.example.studentalarm.alarm;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.receiver.SetAlarmLater;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.weather.BadWeatherCheck;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import static com.example.studentalarm.weather.BadWeatherCheck.DELTA_ALARM_BEFORE;

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
            LectureSchedule.Lecture first = LectureSchedule.load(context).getNextLecture(context, true);
            if (first != null)
                new Thread(() -> setAlarm(checkBadWeather(context, first).getTime(), context)).start();
            else
                Alarm.cancelAlarm(context);
        }
    }

    /**
     * Update the next alarm, called from SetAlarmLater
     *
     * @param context context of the application
     */
    public static void updateNextAlarmFromSetAlarmLater(@NonNull Context context) {
        Log.d(LOG, "update alarm from set alarm later");
        Alarm.cancelAlarm(context);
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.ALARM_ON, false)) {
            Log.d(LOG, "alarm on");
            LectureSchedule.Lecture first = LectureSchedule.load(context).getNextLecture(context, true);
            if (first != null) {
                Log.d(LOG, "Bad Weather Check");
                if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.WAKE_WEATHER, true))
                    new Thread(() -> setAlarm(addBadWeatherTimeIfWeatherIsBad(context, first).getTime(), context));
                else setAlarm(first.getStartWithDefaultTimeZone().getTime(), context);
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
        AlarmManager.setNextAlarm(context);
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
        AlarmManager.setNextAlarm(context);
    }

    /**
     * get the sum of before way and after time
     *
     * @param context context of app
     * @return sum of time before event
     */
    public static int getSumTimeBefore(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(PreferenceKeys.BEFORE, 0) +
                preferences.getInt(PreferenceKeys.WAY, 0) +
                preferences.getInt(PreferenceKeys.AFTER, 0);
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
        Log.d(LOG, "Alarm before subtracting: " + calendar.toString());
        calendar.add(Calendar.MINUTE, -getSumTimeBefore(context));
        Log.d(LOG, "Alarm after subtracting: " + calendar.toString());
        Log.d(LOG, "Check date: " + Calendar.getInstance().getTimeInMillis() + ", " + calendar.getTimeInMillis());
        if (Calendar.getInstance().before(calendar)) {
            if (preferences.getBoolean(PreferenceKeys.ALARM_PHONE, false))
                Alarm.setPhoneAlarm(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), context);
            else {
                Alarm.setAlarm(calendar, context);
            }
        } else {
            Log.e(LOG, "Wrong Date is set for alarm. Date is before current date");
            LectureSchedule.Lecture lecture = LectureSchedule.load(context).getNextLecture(context, false);
            if (lecture != null)
                setAlarm(lecture.getStartWithDefaultTimeZone().getTime(), context);
        }
    }

    /**
     * check bad wather
     *
     * @param context context of app
     * @param first   lecture to set alarm
     * @return date to set alarm
     */
    private static Date checkBadWeather(@NonNull Context context, LectureSchedule.Lecture first) {
        Date date = first.getStartWithDefaultTimeZone();
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.WAKE_WEATHER, false)) {
            Date checkDate = new Date(date.getTime());
            checkDate.setTime(date.getTime() - (60000 * (DELTA_ALARM_BEFORE +
                    Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.WAKE_WEATHER_TIME, PreferenceKeys.DEFAULT_WAKE_WEATHER_TIME)) +
                    getSumTimeBefore(context))));
            if (checkDate.before(Calendar.getInstance().getTime())) { //potentially there could be an instant alarm if the alarm should have happened in the past because of changed weather.
                return addBadWeatherTimeIfWeatherIsBad(context, first);
            } else {
                Log.d(LOG, "Bad Weather Check Later");
                android.app.AlarmManager manager = ((android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
                PendingIntent intent = PendingIntent.getBroadcast(context, 1, new Intent(context, SetAlarmLater.class), 0);
                manager.cancel(intent);
                manager.set(android.app.AlarmManager.RTC_WAKEUP, checkDate.getTime(), intent);
                PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PreferenceKeys.WAKE_WEATHER_CHECK_TIME, checkDate.getTime()).apply();
            }
        }
        return date;
    }

    /**
     * add bad weather alarm time, if weather is bad
     *
     * @param context context of app
     * @param first   lecture to set alarm
     * @return date to set alarm
     */
    private static Date addBadWeatherTimeIfWeatherIsBad(@NonNull Context context, LectureSchedule.Lecture first) {
        Date date = first.getStartWithDefaultTimeZone();
        if (new BadWeatherCheck(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.ZIP_CODE, PreferenceKeys.DEFAULT_ZIP_CODE)).isTheWeatherBad(first.getStart())) {
            Log.d(LOG, "Bad Weather");
            date.setTime(date.getTime() - 60000 * Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.WAKE_WEATHER_TIME, PreferenceKeys.DEFAULT_WAKE_WEATHER_TIME)));
        }
        return date;
    }

}
