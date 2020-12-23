package com.example.studentalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.example.studentalarm.alarm.AlarmManager;

public class AlarmOffReceiver extends BroadcastReceiver {
    /**
     * triggered when alarm should be turn off
     */
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        Log.d("ALARM", "OFF");
        AlarmManager.setNextAlarm(context);
        NotificationManagerCompat.from(context).cancel(AlarmReceiver.NOTIFICATION_ID);
        if (AlarmReceiver.mp != null) {
            AlarmReceiver.mp.stop();
            AlarmReceiver.mp.release();
        }
    }
}
