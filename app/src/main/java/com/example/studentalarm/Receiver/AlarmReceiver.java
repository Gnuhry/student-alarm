package com.example.studentalarm.Receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.example.studentalarm.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class AlarmReceiver extends BroadcastReceiver {

    private final String CHANNEL_ID = "123456";
    public static final int NOTIFICATION_ID = 123456;
    public static MediaPlayer mp;

    /**
     * triggered if it's time to play an alarm
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Alarm Bell", "Alarm just fired");
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); //TODO set sound
        mp = MediaPlayer.create(context.getApplicationContext(), alarmSound);
        mp.setLooping(true);
        setNotification(context);
        mp.start();
    }

    private void setNotification(Context context) {

        Intent snoozeIntent = new Intent(context, SnoozeReceiver.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);

        Intent alarmOffIntent = new Intent(context, AlarmOffReceiver.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            alarmOffIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle("Test")
                .setContentText("HALLO HALLO HALLO")
//                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK), 0))
                .addAction(R.drawable.alarm, "Snooze", PendingIntent.getBroadcast(context, 0, snoozeIntent, 0))
                .addAction(R.drawable.alarm, "Alarm Off", PendingIntent.getBroadcast(context, 0, alarmOffIntent, 0))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        createNotificationChannel(context);
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alarm";//context.getString(R.string.channel_name);
            String description = "Alarm";//context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
