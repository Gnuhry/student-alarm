package com.example.studentalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.studentalarm.PreferenceKeys;
import com.example.studentalarm.imports.Import;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

public class NetworkReceiver extends BroadcastReceiver {
    /**
     * triggered if phone has connection
     */
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.WAIT_FOR_NETWORK, false))
            return;
        Log.d("NetworkReceiver", "network change");
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.AUTO_IMPORT, false))
            new ImportReceiver().onReceive(context, intent);
        else
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PreferenceKeys.WAIT_FOR_NETWORK, false).apply();
    }
}
