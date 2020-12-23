package com.example.studentalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.imports.Import;
import com.example.studentalarm.save.PreferenceKeys;

public class BootReceiver extends BroadcastReceiver {

    /**
     * triggered if phone has booted
     */
    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        Log.d("BootReceiver", "boot done");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.AUTO_IMPORT, false))
                Import.setTimer(context);
            AlarmManager.setNextAlarm(context);
        }
    }
}
