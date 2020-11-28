package com.example.studentalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.studentalarm.AlarmManager;
import com.example.studentalarm.imports.Import;
import com.example.studentalarm.PreferenceKeys;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

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
