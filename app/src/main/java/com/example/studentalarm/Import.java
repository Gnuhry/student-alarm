package com.example.studentalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

public class Import {
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    /**
     * set a daily timer for import
     *
     * @param context contex of the application
     */
    public void SetTimer(Context context) {
        if (alarmMgr != null) return;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 19);

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, ImportReceiver.class), 0);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

    }

    /**
     * stops the timer
     */
    public void StopTimer() {
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
    }

    /**
     * Create an ICS Import
     * @param link linkt to the ics file
     * @param context context of the application
     * @return the new lecture schedule
     */
    public static Lecture_Schedule ICSImport(String link, Context context) {
        Lecture_Schedule lecture_schedule=Lecture_Schedule.Load(context);
        if(link==null) return null;
        SharedPreferences.Editor editor = context.getSharedPreferences("IMPORT", Context.MODE_PRIVATE).edit();
        editor.putInt("MODE", ImportFunction.ICS);
        editor.putString("LINK", link);
        editor.apply();
        ICS ics=new ICS(link,true);
        if(ics.isSuccessful()){
            lecture_schedule.ImportICS(ics);
            lecture_schedule.Save(context);
        }
        return lecture_schedule;
    }

    /**
     * Display the different import possibilities
     */
    public static class ImportFunction {
        public static final int NONE = 0;
        public static final int ICS = 1;
    }

}
