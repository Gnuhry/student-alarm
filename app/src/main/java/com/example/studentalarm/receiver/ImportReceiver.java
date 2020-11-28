package com.example.studentalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.studentalarm.AlarmManager;
import com.example.studentalarm.import_.Import;

import androidx.annotation.NonNull;

public class ImportReceiver extends BroadcastReceiver {

    /**
     * triggered if file should import
     */
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        Log.d("ImportReceiver", "import");
        new Thread(() -> {
            Import.ImportLecture(context);
            AlarmManager.UpdateNextAlarmAfterAutoImport(context);
        }).start();
    }
}
