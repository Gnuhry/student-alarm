package com.example.studentalarm.import_;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.studentalarm.PreferenceKeys;
import com.example.studentalarm.receiver.ImportReceiver;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

public class Import {

    /**
     * set a daily timer for import
     *
     * @param context context of the application
     */
    public static void SetTimer(@NonNull Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String[] time = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.IMPORT_TIME, PreferenceKeys.DEFAULT_IMPORT_TIME).split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, ImportReceiver.class), 0);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

    }

    /**
     * stops the timer
     */
    public static void StopTimer(@NonNull Context context) {
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(context, 0, new Intent(context, ImportReceiver.class), 0));
    }

    /**
     * Create the import
     *
     * @param context context of the application
     * @return the new lecture schedule
     */
    @NonNull
    public static Lecture_Schedule ImportLecture(@NonNull Context context) {
        Lecture_Schedule lecture_schedule = Lecture_Schedule.Load(context);
        switch (PreferenceManager.getDefaultSharedPreferences(context).getInt(PreferenceKeys.MODE, 0)) {
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
    @NonNull
    private static Lecture_Schedule ICSImport(@NonNull Context context, @NonNull Lecture_Schedule lecture_schedule) {
        String link = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.LINK, null);
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
        @NonNull
        public static final List<String> imports = Arrays.asList("None", "ICS");
    }

}

