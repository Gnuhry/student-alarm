package com.example.studentalarm.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.preference.PreferenceManager;

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("AUTO_IMPORT", false))
            new ImportReceiver().onReceive(context, intent);
    }
}
