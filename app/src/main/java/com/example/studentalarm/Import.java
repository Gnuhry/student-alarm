package com.example.studentalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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

    public static Lecture_Schedule Import(Context context) {
        Lecture_Schedule lecture_schedule = Lecture_Schedule.Load(context);
        switch (context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE).getInt("Mode", 0)) {
            case 0:
                return lecture_schedule;
            case 1:
                return ICSImport(context, lecture_schedule);
        }
        return lecture_schedule;
    }

    /**
     * Create an ICS Import
     *
     * @param context context of the application
     * @return the new lecture schedule
     */
    private static Lecture_Schedule ICSImport(Context context, Lecture_Schedule lecture_schedule) {
        String link = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE).getString("Link", null);
        if (link == null) return lecture_schedule;
        ICS ics = new ICS(link, true);
        if (ics.isSuccessful()) {
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
        public static final List<String> imports = Arrays.asList("None", "ICS");
    }

}

