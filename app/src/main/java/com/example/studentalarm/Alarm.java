package com.example.studentalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.Toast;

import com.example.studentalarm.receiver.AlarmReceiver;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

public class Alarm {

    /**
     * create an alarm, which going to trigger the AlarmReceiver class
     *
     * @param time    the time, the receiver should be triggered
     * @param context context to show the toast
     */
    public static void setAlarm(@NonNull Calendar time, @NonNull Context context) {
        if (Calendar.getInstance().before(time)) {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), alarmIntent);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PreferenceKeys.ALARM_TIME, time.getTimeInMillis()).apply();
            Toast.makeText(context, R.string.alarm_is_set, Toast.LENGTH_SHORT).show();
            Log.d("ALARM", "Set alarm to " + time.getTimeInMillis());
        } else {
            Log.e("ALARM", "Wrong Date is set for alarm. Date is before current date");
        }
    }

    /**
     * cancel the alarm
     */
    public static void cancelAlarm(@NonNull Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PreferenceKeys.ALARM_TIME, 0).apply();
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0));
    }

    /**
     * create an alarm in the phone alarm app
     *
     * @param hour    hour when the alarm should trigger
     * @param minute  minutes when the alarm should trigger
     * @param context context to show the toast
     */
    public static void setPhoneAlarm(int hour, int minute, @NonNull Context context) {
        if (hour <= 24 && minute <= 60)
            context.startActivity(new Intent(AlarmClock.ACTION_SET_ALARM).putExtra(AlarmClock.EXTRA_HOUR, hour).putExtra(AlarmClock.EXTRA_MINUTES, minute).putExtra(AlarmClock.EXTRA_SKIP_UI, true));
    }


}
