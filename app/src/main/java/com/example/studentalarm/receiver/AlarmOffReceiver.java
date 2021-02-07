package com.example.studentalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.save.PreferenceKeys;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import static com.example.studentalarm.MainActivity.ALARM_BROADCAST;

public class AlarmOffReceiver extends BroadcastReceiver {
    /**
     * triggered when alarm should be turn off
     */
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        Log.d("ALARM", "OFF");
        AlarmManager.setNextAlarm(context);
        NotificationManagerCompat.from(context).cancel(AlarmReceiver.NOTIFICATION_ID);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PreferenceKeys.ALARM_MODE, 0).apply();
        AlarmReceiver.stopRingtone();
        context.sendBroadcast(new Intent(ALARM_BROADCAST));
    }
}
