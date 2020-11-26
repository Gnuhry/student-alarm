package com.example.studentalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
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
    public static final String IMPORT_TIME = "IMPORT_TIME";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String RINGTONE = "RINGTONE";

    public static final String DEFAULT_RINGTONE = "Default";
    public static final String DEFAULT_LANGUAGE = "EN";
    public static final String DEFAULT_SNOOZE = "5";
    public static final String DEFAULT_IMPORT_TIME = "19:00";

    /**
     * Reset the settings
     *
     * @param context context of application
     * @return language
     */
    @NonNull
    public static String Reset(@NonNull Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        return Default(context);
    }

    /**
     * set the settings default
     *
     * @param context context of application
     * @return language
     */
    @NonNull
    public static String Default(@NonNull Context context) {
        String erg = DEFAULT_LANGUAGE(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getString(PreferenceKeys.RINGTONE, null) == null)
            preferences.edit().putString(PreferenceKeys.RINGTONE, DEFAULT_RINGTONE).apply();
        if (preferences.getString(PreferenceKeys.LANGUAGE, null) == null)
            preferences.edit().putString(PreferenceKeys.LANGUAGE, erg).apply();
        if (preferences.getString(PreferenceKeys.SNOOZE, null) == null)
            preferences.edit().putString(PreferenceKeys.SNOOZE, DEFAULT_SNOOZE).apply();
        if (preferences.getString(PreferenceKeys.IMPORT_TIME, null) == null)
            preferences.edit().putString(PreferenceKeys.IMPORT_TIME, DEFAULT_IMPORT_TIME).apply();
        return erg;
    }

    /**
     * Get default language
     *
     * @param context context of application
     * @return default language
     */
    @NonNull
    public static String DEFAULT_LANGUAGE(@NonNull Context context) {
        String s = Locale.getDefault().getLanguage();
        Log.d("LANGUAGE", s);
        for (String language : context.getResources().getStringArray(R.array.language)) {
            if (s.toUpperCase().equals(language.toUpperCase())) {
                return s.toUpperCase();
            }
        }
        return DEFAULT_LANGUAGE;
    }
}
