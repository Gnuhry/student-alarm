package com.example.studentalarm.receiver;

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

import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.R;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

public class AlarmReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 123456;
    @NonNull
    private static final String CHANNEL_ID = "123456";
    public static MediaPlayer mp;

    /**
     * triggered if it's time to play an alarm
     */
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        Log.d("Alarm Bell", "Alarm just fired");
        mp = getMediaPlayer(context);
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            setNotification(context);
            mp.setLooping(true);
        }
        mp.start();
    }

    /**
     * create notification to publish alarm
     *
     * @param context context of application
     */
    private void setNotification(@NonNull Context context) {
        Intent snoozeIntent = new Intent(context, SnoozeReceiver.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);

        Intent alarmOffIntent = new Intent(context, AlarmOffReceiver.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            alarmOffIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.alarm)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.alarm))
//                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK), 0))
                .addAction(R.drawable.alarm, context.getString(R.string.snooze), PendingIntent.getBroadcast(context, 0, snoozeIntent, 0))
                .addAction(R.drawable.alarm, context.getString(R.string.alarm_off), PendingIntent.getBroadcast(context, 0, alarmOffIntent, 0))
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        createNotificationChannel(context);
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Create notification channel to publish
     *
     * @param context context of the application
     */
    private void createNotificationChannel(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.alarm);
            String description = context.getString(R.string.alarm);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Get the media player with the ringtone set in settings
     *
     * @param context context of the application
     * @return ringtone to play
     */
    private MediaPlayer getMediaPlayer(@NonNull Context context) {
        switch (PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.RINGTONE, PreferenceKeys.DEFAULT_RINGTONE)) {
            case "didudeldudu":
                return MediaPlayer.create(context.getApplicationContext(), R.raw.didudeldudu);
            case "DEFAULT":
            default:
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                return MediaPlayer.create(context.getApplicationContext(), alarmSound);
        }
    }
}
