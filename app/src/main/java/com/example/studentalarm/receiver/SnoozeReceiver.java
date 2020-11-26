package com.example.studentalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.studentalarm.Alarm;
import com.example.studentalarm.PreferenceKeys;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

public class SnoozeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        Log.d("ALARM", "Snooze");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.SNOOZE, PreferenceKeys.DEFAULT_SNOOZE)));
        Alarm.setAlarm(calendar, context);
        NotificationManagerCompat.from(context).cancel(AlarmReceiver.NOTIFICATION_ID);
        if (AlarmReceiver.mp != null) {
            AlarmReceiver.mp.stop();
            AlarmReceiver.mp.release();
        }
    }
}
