package com.example.studentalarm.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.studentalarm.Alarm;

import java.util.Calendar;

import androidx.core.app.NotificationManagerCompat;

public class SnoozeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ALARM","Snooze");
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MINUTE,5);
        Alarm.setAlarm(calendar, context);
        NotificationManagerCompat.from(context).cancel(AlarmReceiver.NOTIFICATION_ID);
        if(AlarmReceiver.mp!=null){
            AlarmReceiver.mp.stop();
            AlarmReceiver.mp.release();
        }
    }
}
