package com.example.studentalarm;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.studentalarm.Import.Lecture_Schedule;

import java.util.Calendar;
import java.util.Date;

import androidx.preference.PreferenceManager;

public class AlarmManager {

    /**
     * Set the next alarm
     *
     * @param context context of the application
     */
    public static void SetNextAlarm(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("ALARM_ON", false)) {
            Calendar calendar = Calendar.getInstance();
            Lecture_Schedule.Lecture firstToday = Lecture_Schedule.Load(context).getFirstLectureAtDate(calendar.getTime());
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(firstToday.getStart());
            if (calendar2.before(calendar))
                firstToday = Lecture_Schedule.Load(context).getFirstLectureAtDate(getNextDay());
            calendar.setTime(firstToday.getStart());
            calendar.add(Calendar.MINUTE, -preferences.getInt("BEFORE", 0));
            calendar.add(Calendar.MINUTE, -preferences.getInt("WAY", 0));
            calendar.add(Calendar.MINUTE, -preferences.getInt("AFTER", 0));
            if (preferences.getBoolean("ALARM_PHONE", false)) {
                Alarm.setPhoneAlarm(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), context);
            } else {
                CancelNextAlarm(context);
                Alarm.setAlarm(calendar, context);
            }
        }
    }

    /**
     * Update the next alarm, if settings changed
     *
     * @param context context of the application
     */
    public static void UpdateNextAlarm(Context context) {
        CancelNextAlarm(context);
        SetNextAlarm(context);
    }

    /**
     * Update the next alarm after import
     *
     * @param context context of the application
     */
    public static void UpdateNextAlarmAfterImport(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean("ALARM_CHANGE", false)) return;
        SetNextAlarm(context);
    }

    /**
     * Cancel Alarm
     *
     * @param context context of the application
     */
    public static void CancelNextAlarm(Context context) {
        Alarm.cancelAlarm(context);
    }

    /**
     * get the next day
     *
     * @return next day as date
     */
    private static Date getNextDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }
}
