package com.example.studentalarm.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    /**
     * triggered if it's time to play an alarm
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm Bell", "Alarm just fired");
        //TODO Ton abspielen und Notification absenden
    }
}
