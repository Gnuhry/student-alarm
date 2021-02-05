package com.example.studentalarm.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.imports.Import;
import com.example.studentalarm.save.PreferenceKeys;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;

public class BootReceiver extends BroadcastReceiver {

    /**
     * triggered if phone has booted
     */
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        Log.d("BootReceiver", "boot done");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.AUTO_IMPORT, false))
                Import.setTimer(context);
            new Thread(() -> AlarmManager.setNextAlarm(context)).start();
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.WAKE_WEATHER, false))
            {
                Date wakeWeatherCheckTime = new Date(PreferenceManager.getDefaultSharedPreferences(context).getLong(PreferenceKeys.WAKE_WEATHER_CHECK_TIME, 0));
                if (wakeWeatherCheckTime.before(Calendar.getInstance().getTime())) {
                    ((android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(context, 1, new Intent(context, SetAlarmLater.class), 0));
                    ((android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).set(android.app.AlarmManager.RTC_WAKEUP, wakeWeatherCheckTime.getTime(), PendingIntent.getBroadcast(context, 1, new Intent(context, SetAlarmLater.class), 0));
                } else {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PreferenceKeys.WAKE_WEATHER_CHECK_TIME, 0).apply();
                }
            }
        }
    }
}
