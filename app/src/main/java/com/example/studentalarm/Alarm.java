package com.example.studentalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class Alarm {

    /**
     * create an alarm, which going to trigger the alarmreceiver class
     * @param time the time, the receiver should be triggerd
     * @param context context to show the toast
     */
    public void setAlarm(Calendar time, Context context) {
        if (Calendar.getInstance().before(time)) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            assert alarmManager != null;
            alarmManager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
            Toast.makeText(context, "Alarm is set", Toast.LENGTH_SHORT).show(); //TODO show Time
            Log.d("ALARM", "Set alarm to "+time.getTimeInMillis());
        } else {
            Log.e("ALARM", "Wrong Date is set for alarm. Date is before current date");
        }
//            Intent intent=new Intent(AlarmClock.ACTION_SET_ALARM);
//            intent.putExtra(AlarmClock.EXTRA_HOUR,hour);
//            intent.putExtra(AlarmClock.EXTRA_MINUTES,minute);
//            intent.putExtra(AlarmClock.EXTRA_SKIP_UI,true);
//            context.startActivity(intent);
    }


}
