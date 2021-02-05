package com.example.studentalarm.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.Toast;

import com.example.studentalarm.R;
import com.example.studentalarm.receiver.AlarmReceiver;
import com.example.studentalarm.save.PreferenceKeys;

import java.util.Calendar;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

public class Alarm {

    private static final String LOG = "Alarm";

    /**
     * create an alarm, which going to trigger the AlarmReceiver class
     *
     * @param time    the time, the receiver should be triggered
     * @param context context to show the toast
     */
    public static void setAlarm(@NonNull Calendar time, @NonNull Context context) {
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0));
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PreferenceKeys.ALARM_TIME, time.getTimeInMillis()).apply();
        Handler mainHandler = new Handler(context.getMainLooper());
        Runnable myRunnable = () -> Toast.makeText(context, R.string.alarm_is_set, Toast.LENGTH_SHORT).show();
        mainHandler.post(myRunnable);
        Log.d(LOG, "Set alarm to " + time.getTimeInMillis());
    }

    /**
     * cancel the alarm
     */
    public static void cancelAlarm(@NonNull Context context) {
        Log.d(LOG, "cancel");
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PreferenceKeys.ALARM_TIME, 0).apply();
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0));
    }

    /**
     * create an alarm in the phone alarm app
     *
     * @param hour    hour when the alarm should trigger
     * @param minute  minutes when the alarm should trigger
     * @param context context to show the toast
     */
    public static void setPhoneAlarm(int hour, int minute, @NonNull Context context) {
        Log.d(LOG, "Set phone alarm");
        if (hour <= 24 && minute <= 60)
            context.startActivity(new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR, hour).putExtra(AlarmClock.EXTRA_MINUTES, minute).putExtra(AlarmClock.EXTRA_SKIP_UI, true));
    }


}
