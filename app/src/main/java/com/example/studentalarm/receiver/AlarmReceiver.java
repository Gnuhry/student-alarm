package com.example.studentalarm.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.example.studentalarm.MainActivity;
import com.example.studentalarm.R;
import com.example.studentalarm.Ringtone;
import com.example.studentalarm.save.PreferenceKeys;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static com.example.studentalarm.MainActivity.ALARM_BROADCAST;

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
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0))
                .addAction(R.drawable.alarm, context.getString(R.string.snooze), PendingIntent.getBroadcast(context, 0, snoozeIntent, 0))
                .addAction(R.drawable.alarm, context.getString(R.string.alarm_off), PendingIntent.getBroadcast(context, 0, alarmOffIntent, 0))
                .setFullScreenIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0), true)
                .setAutoCancel(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            builder.setCategory(Notification.CATEGORY_ALARM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.alarm);
            String description = context.getString(R.string.alarm);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean flashLight = preferences.getBoolean(PreferenceKeys.FLASH_LIGHT, false);
            channel.enableLights(flashLight);
            if (flashLight) {
                channel.setLightColor(preferences.getInt(PreferenceKeys.FLASH_LIGHT_COLOR, PreferenceKeys.DEFAULT_FLASH_LIGHT_COLOR));
                builder.setLights(preferences.getInt(PreferenceKeys.FLASH_LIGHT_COLOR, PreferenceKeys.DEFAULT_FLASH_LIGHT_COLOR), 3000, 3000);
            }
            if (preferences.getBoolean(PreferenceKeys.VIBRATION, false))
                builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000});
            builder.setOngoing(true);
            channel.enableVibration(preferences.getBoolean(PreferenceKeys.VIBRATION, false));
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } else
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PreferenceKeys.ALARM_MODE, 1).apply();
        context.sendBroadcast(new Intent(ALARM_BROADCAST));
    }

    /**
     * Get the media player with the ringtone set in settings
     *
     * @param context context of the application
     * @return ringtone to play
     */
    private MediaPlayer getMediaPlayer(@NonNull Context context) {
        String ringtone = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.RINGTONE, PreferenceKeys.DEFAULT_RINGTONE);
        if (ringtone.startsWith("|")) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.parse(ringtone.substring(1)));
            if (mediaPlayer != null)
                return mediaPlayer;
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PreferenceKeys.RINGTONE, PreferenceKeys.DEFAULT_RINGTONE).apply();
        }
        return Ringtone.getConstantRingtone(ringtone, context, true);
    }
}
