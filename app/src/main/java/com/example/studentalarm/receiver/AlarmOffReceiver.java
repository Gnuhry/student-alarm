package com.example.studentalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.studentalarm.AlarmManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

public class AlarmOffReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        Log.d("ALARM", "OFF");
        AlarmManager.SetNextAlarm(context);
        NotificationManagerCompat.from(context).cancel(AlarmReceiver.NOTIFICATION_ID);
        if (AlarmReceiver.mp != null) {
            AlarmReceiver.mp.stop();
            AlarmReceiver.mp.release();
        }
    }
}
