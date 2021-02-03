package com.example.studentalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.studentalarm.alarm.Alarm;
import com.example.studentalarm.save.PreferenceKeys;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import static com.example.studentalarm.MainActivity.ALARM_BROADCAST;

public class SnoozeReceiver extends BroadcastReceiver {
    /**
     * triggered if alarm should snooze
     */
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        Log.d("SnoozeReceiver", "snoozing");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.SNOOZE, PreferenceKeys.DEFAULT_SNOOZE)));
        Alarm.setAlarm(calendar, context);
        NotificationManagerCompat.from(context).cancel(AlarmReceiver.NOTIFICATION_ID);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PreferenceKeys.ALARM_MODE, 2).apply();
        if (AlarmReceiver.mp != null) {
            AlarmReceiver.mp.stop();
            AlarmReceiver.mp.release();
        }
        context.sendBroadcast(new Intent(ALARM_BROADCAST));
    }
}
