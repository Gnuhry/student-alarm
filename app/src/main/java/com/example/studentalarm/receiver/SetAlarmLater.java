package com.example.studentalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import static com.example.studentalarm.alarm.AlarmManager.updateNextAlarmFromSetAlarmLater;

public class SetAlarmLater extends BroadcastReceiver {

    private static final String LOG = "SetAlarmLater";

    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        Log.d(LOG,"Alarm will be set now");
        updateNextAlarmFromSetAlarmLater(context);
    }
}
