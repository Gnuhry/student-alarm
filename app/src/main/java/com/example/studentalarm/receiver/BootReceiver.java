package com.example.studentalarm.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.imports.Import;
import com.example.studentalarm.save.PreferenceKeys;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {

    /**
     * triggered if phone has booted
     */
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        Log.d("BootReceiver", "boot done");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getBoolean(PreferenceKeys.AUTO_IMPORT, false))
                Import.setTimer(context);
            AlarmManager.setNextAlarm(context);
            if (preferences.getBoolean(PreferenceKeys.WAKE_WEATHER, false)) {
                Date wakeWeatherCheckTime = new Date(preferences.getLong(PreferenceKeys.WAKE_WEATHER_CHECK_TIME, 0));
                if (wakeWeatherCheckTime.before(Calendar.getInstance().getTime())) {
                    android.app.AlarmManager manager = ((android.app.AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, new Intent(context, SetAlarmLater.class), 0);
                    manager.cancel(pendingIntent);
                    manager.set(android.app.AlarmManager.RTC_WAKEUP, wakeWeatherCheckTime.getTime(), pendingIntent);
                } else {
                    preferences.edit().putLong(PreferenceKeys.WAKE_WEATHER_CHECK_TIME, 0).apply();
                }
            }
        }
    }
}
