package com.example.studentalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.studentalarm.import_.Lecture_Schedule;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

public class AlarmManager {

    private static int BEFORE, WAY, AFTER;
    private static boolean ALARM_PHONE, INIT = true;
    private static final String LOG = "AlarmManager";

    /**
     * Set the next alarm
     *
     * @param context context of the application
     */
    public static void SetNextAlarm(@NonNull Context context) {
        Log.d(LOG, "set next alarm");
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.ALARM_ON, false)) {
            Log.d(LOG, "alarm on");
            Lecture_Schedule.Lecture first = Lecture_Schedule.Load(context).getNextFirstDayLecture();
            if (first != null)
                SetAlarm(first.getStart(), context);
        }
    }

    /**
     * Set the alarm at date
     *
     * @param date    date where the alarm should trigger
     * @param context context of the application
     */
    private static void SetAlarm(@NonNull Date date, @NonNull Context context) {
        Log.d(LOG, "Set alarm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        calendar.add(Calendar.MINUTE, -preferences.getInt(PreferenceKeys.BEFORE, 0));
        calendar.add(Calendar.MINUTE, -preferences.getInt(PreferenceKeys.WAY, 0));
        calendar.add(Calendar.MINUTE, -preferences.getInt(PreferenceKeys.AFTER, 0));
        if (preferences.getBoolean(PreferenceKeys.ALARM_PHONE, false))
            Alarm.setPhoneAlarm(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), context);
        else {
            CancelNextAlarm(context);
            Alarm.setAlarm(calendar, context);
        }
    }

    /**
     * Update the next alarm, if settings changed
     *
     * @param context context of the application
     */
    public static void UpdateNextAlarm(@NonNull Context context) {
        Log.d(LOG, "update alarm");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!INIT && BEFORE == preferences.getInt(PreferenceKeys.BEFORE, 0) &&
                WAY == preferences.getInt(PreferenceKeys.WAY, 0) &&
                AFTER == preferences.getInt(PreferenceKeys.AFTER, 0) &&
                ALARM_PHONE == preferences.getBoolean(PreferenceKeys.ALARM_PHONE, false)) {
            INIT = false;
            return;
        }
        BEFORE = preferences.getInt(PreferenceKeys.BEFORE, 0);
        WAY = preferences.getInt(PreferenceKeys.WAY, 0);
        AFTER = preferences.getInt(PreferenceKeys.AFTER, 0);
        ALARM_PHONE = preferences.getBoolean(PreferenceKeys.ALARM_PHONE, false);
        CancelNextAlarm(context);
        SetNextAlarm(context);
    }

    /**
     * Update the next alarm after import
     *
     * @param context context of the application
     */
    public static void UpdateNextAlarmAfterAutoImport(@NonNull Context context) {
        Log.d(LOG, "update alarm after auto import");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean(PreferenceKeys.ALARM_CHANGE, false)) return;
        SetNextAlarm(context);
    }

    /**
     * Cancel Alarm
     *
     * @param context context of the application
     */
    public static void CancelNextAlarm(@NonNull Context context) {
        Log.d(LOG, "cancel alarm");
        Alarm.cancelAlarm(context);
    }

}
