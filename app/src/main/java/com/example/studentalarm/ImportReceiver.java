package com.example.studentalarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ImportReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences=context.getSharedPreferences("IMPORT", Context.MODE_PRIVATE);
        switch (preferences.getInt("MODE", 0)){
            case Import.ImportFunction.NONE: break;
            case Import.ImportFunction.ICS:
                Lecture_Schedule lecture_schedule=Import.ICSImport(preferences.getString("LINK",null), context);
                //TODO check alarm
                break;
        }

        //TODO https://www.tutorialspoint.com/android/android_shared_preferences.htm (https://stackoverflow.com/questions/10962344/how-to-save-data-in-an-android-app)
    }
}