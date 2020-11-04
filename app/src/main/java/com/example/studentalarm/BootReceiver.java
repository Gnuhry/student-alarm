package com.example.studentalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    /**
     * triggered if phone has booted
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //TODO Set the alarm here.
            //TODO Check daily import settings
        }

    }
}
