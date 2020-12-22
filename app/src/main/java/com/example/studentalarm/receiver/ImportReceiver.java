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

public class ImportReceiver extends BroadcastReceiver {

    /**
     * triggered if file should import
     */
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        Log.d("ImportReceiver", "import");
        new Thread(() -> {
            if (Import.checkConnection(context, true))
                Import.importLecture(context);
            else
                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PreferenceKeys.WAIT_FOR_NETWORK, true).apply();
            AlarmManager.updateNextAlarmAfterAutoImport(context);
        }).start();
    }
}
