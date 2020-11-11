package com.example.studentalarm.Import;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.studentalarm.Receiver.ImportReceiver;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.preference.PreferenceManager;

public class Import {

    /**
     * set a daily timer for import
     *
     * @param context context of the application
     */
    public static void SetTimer(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 19);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, ImportReceiver.class), 0);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

    }

    /**
     * stops the timer
     */
    public static void StopTimer(Context context){
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(context, 0, new Intent(context, ImportReceiver.class), 0));
    }

    /**
     * Create the import
     * @param context context of the application
     * @return the new lecture schedule
     */
    public static Lecture_Schedule ImportLecture(Context context) {
        Lecture_Schedule lecture_schedule = Lecture_Schedule.Load(context);
        switch (PreferenceManager.getDefaultSharedPreferences(context).getInt("Mode", 0)) {
            case ImportFunction.NONE:
                return lecture_schedule;
            case ImportFunction.ICS: case ImportFunction.DHBWMa:
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
        String link = PreferenceManager.getDefaultSharedPreferences(context).getString("Link", null);
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
        public static final int DHBWMa = 2;
        public static final List<String> imports = Arrays.asList("None", "ICS", "DHBW Mannheim");
    }

}

