package com.example.studentalarm.alarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

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
            if (first != null)
                setAlarm(first.getStartWithDefaultTimeZone().getTime(), context);
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
        setNextAlarm(context);
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
        setNextAlarm(context);
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
        if (preferences.getBoolean(PreferenceKeys.ALARM_PHONE, false)) {
            Alarm.setPhoneAlarm(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), context);
        } else {
            cancelNextAlarm(context);
            Alarm.setAlarm(calendar, context);
        }
    }

}
