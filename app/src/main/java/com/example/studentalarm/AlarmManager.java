package com.example.studentalarm;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.studentalarm.import_.Lecture_Schedule;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

public class AlarmManager {

    /**
     * Set the next alarm
     *
     * @param context context of the application
     */
    public static void SetNextAlarm(@NonNull Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.ALARM_ON, false)) {
            Calendar calendar = Calendar.getInstance();
            Lecture_Schedule.Lecture first1 = Lecture_Schedule.Load(context).getFirstLectureAtDate(calendar.getTime());
            if (first1 != null) {
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(first1.getStart());
                if (calendar2.after(calendar)) {
                    SetAlarm(first1.getStart(), context);
                    return;
                }
            }
            Lecture_Schedule.Lecture first2 = Lecture_Schedule.Load(context).getFirstLectureAtDate(getNextDay());
            if (first2 != null) {
                SetAlarm(first2.getStart(), context);
                return;
            }
            Lecture_Schedule.Lecture first3 = Lecture_Schedule.Load(context).getNextLecture(calendar.getTime());
            if (first3 != null)
                SetAlarm(first3.getStart(), context);
        }
    }

    /**
     * Set the alarm at date
     *
     * @param date    date where the alarm should trigger
     * @param context context of the application
     */
    private static void SetAlarm(@NonNull Date date, @NonNull Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        calendar.add(Calendar.MINUTE, -preferences.getInt(PreferenceKeys.BEFORE, 0));
        calendar.add(Calendar.MINUTE, -preferences.getInt(PreferenceKeys.WAY, 0));
        calendar.add(Calendar.MINUTE, -preferences.getInt(PreferenceKeys.AFTER, 0));
        if (preferences.getBoolean(PreferenceKeys.ALARM_PHONE, false))
            Alarm.setPhoneAlarm(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), context);
        else {
            CancelNextAlarm(context);
            Alarm.setAlarm(calendar, context);
        }
    }

    /**
     * Update the next alarm, if settings changed
     *
     * @param context context of the application
     */
    public static void UpdateNextAlarm(@NonNull Context context) {
        CancelNextAlarm(context);
        SetNextAlarm(context);
    }

    /**
     * Update the next alarm after import
     *
     * @param context context of the application
     */
    public static void UpdateNextAlarmAfterImport(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean(PreferenceKeys.ALARM_CHANGE, false)) return;
        SetNextAlarm(context);
    }

    /**
     * Cancel Alarm
     *
     * @param context context of the application
     */
    public static void CancelNextAlarm(@NonNull Context context) {
        Alarm.cancelAlarm(context);
    }

    /**
     * get the next day
     *
     * @return next day as date
     */
    @NonNull
    private static Date getNextDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }
}
