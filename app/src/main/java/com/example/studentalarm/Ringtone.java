package com.example.studentalarm;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Ringtone {

    /**
     * get media player for constant ringtones
     *
     * @param ringtone   ringtone key
     * @param context    context of app
     * @param hasDefault if {true} returning a default value, if {false} return null
     * @return media player
     */
    @Nullable
    public static MediaPlayer getConstantRingtone(@NonNull String ringtone, @NonNull Context context, boolean hasDefault) {
        switch (ringtone.toUpperCase()) {
            case "GENTLE":
                return MediaPlayer.create(context.getApplicationContext(), R.raw.alarm_gentle);
            case "DEFAULT":
                return MediaPlayer.create(context.getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            default:
                return hasDefault ? MediaPlayer.create(context.getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) : null;
        }
    }
}
