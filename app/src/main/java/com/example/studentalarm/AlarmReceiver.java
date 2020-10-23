package com.example.studentalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm Bell", "Alarm just fired");
        //TODO Ton abspielen und Notification absenden
    }
}
