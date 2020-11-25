package com.example.studentalarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.studentalarm.AlarmManager;
import com.example.studentalarm.import_.Import;
import com.example.studentalarm.import_.Lecture_Schedule;

public class ImportReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Lecture_Schedule lecture_schedule = Import.ImportLecture(context);
        lecture_schedule.Save(context);
        AlarmManager.UpdateNextAlarmAfterImport(context);
    }
}
