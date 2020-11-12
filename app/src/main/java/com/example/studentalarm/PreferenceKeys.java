package com.example.studentalarm;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class PreferenceKeys {
    public static final String MODE = "Mode";
    public static final String LINK = "Link";
    public static final String BEFORE = "BEFORE";
    public static final String WAY = "WAY";
    public static final String AFTER = "AFTER";
    public static final String ALARM_ON = "ALARM_ON";
    public static final String ALARM_PHONE = "ALARM_PHONE";
    public static final String ALARM_CHANGE = "ALARM_CHANGE";
    public static final String AUTO_IMPORT = "AUTO_IMPORT";
    public static final String ALARM_TIME = "ALARM_TIME";
    public static final String SNOOZE = "SNOOZE";
    public static final String IMPORT_DELETE_ALL = "IMPORT_DELETE_ALL";
    public static final String IMPORT_TIME = "IMPORT_TIME";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String RINGTONE = "RINGTONE";

    public static final String DEFAULT_RINGTONE = "Default";
    public static final String DEFAULT_LANGUAGE = "EN";
    public static final String DEFAULT_SNOOZE = "5";
    public static final String DEFAULT_IMPORT_TIME = "19:00";

    public static void Reset(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
        Default(context);
    }

    public static void Default(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getString(PreferenceKeys.RINGTONE, null) == null)
            preferences.edit().putString(PreferenceKeys.RINGTONE, DEFAULT_RINGTONE).apply();
        if (preferences.getString(PreferenceKeys.LANGUAGE, null) == null)
            preferences.edit().putString(PreferenceKeys.LANGUAGE, DEFAULT_LANGUAGE).apply();
        if (preferences.getString(PreferenceKeys.SNOOZE, null) == null)
            preferences.edit().putString(PreferenceKeys.SNOOZE, DEFAULT_SNOOZE).apply();
        if (preferences.getString(PreferenceKeys.IMPORT_TIME, null) == null)
            preferences.edit().putString(PreferenceKeys.IMPORT_TIME, DEFAULT_IMPORT_TIME).apply();
    }
}
