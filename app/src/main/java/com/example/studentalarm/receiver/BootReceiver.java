package com.example.studentalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.studentalarm.AlarmManager;
import com.example.studentalarm.import_.Import;
import com.example.studentalarm.PreferenceKeys;

import androidx.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {

    /**
     * triggered if phone has booted
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getBoolean(PreferenceKeys.AUTO_IMPORT, false))
                Import.SetTimer(context);
            AlarmManager.SetNextAlarm(context);
        }
    }
}
