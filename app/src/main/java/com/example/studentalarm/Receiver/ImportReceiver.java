package com.example.studentalarm.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.studentalarm.AlarmManager;
import com.example.studentalarm.Import.Import;
import com.example.studentalarm.Import.Lecture_Schedule;

public class ImportReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        switch (preferences.getInt("Mode", 0)) {
            case Import.ImportFunction.NONE:
                break;
            case Import.ImportFunction.ICS:
                Lecture_Schedule lecture_schedule = Import.Import(context);
                lecture_schedule.Save(context);
                AlarmManager.UpdateNextAlarmAfterImport(context);
                break;
        }
    }
}
