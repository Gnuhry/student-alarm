package com.example.studentalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class PreferenceKeys {
    @NonNull public static final String MODE = "Mode";
    @NonNull public static final String LINK = "Link";
    @NonNull public static final String BEFORE = "BEFORE";
    @NonNull public static final String WAY = "WAY";
    @NonNull public static final String AFTER = "AFTER";
    @NonNull  public static final String ALARM_ON = "ALARM_ON";
    @NonNull public static final String ALARM_PHONE = "ALARM_PHONE";
    @NonNull  public static final String ALARM_CHANGE = "ALARM_CHANGE";
    @NonNull  public static final String AUTO_IMPORT = "AUTO_IMPORT";
    @NonNull   public static final String ALARM_TIME = "ALARM_TIME";
    @NonNull  public static final String SNOOZE = "SNOOZE";
    @NonNull  public static final String IMPORT_TIME = "IMPORT_TIME";
    @NonNull  public static final String LANGUAGE = "LANGUAGE";
    @NonNull  public static final String RINGTONE = "RINGTONE";
    public static final String DHBWMANNHEIMCOURSECATEGORY = "";
    public static final String DHBWMANNHEIMCOURSE = "";


    @NonNull  public static final String DEFAULT_RINGTONE = "Default";
    @NonNull  public static final String DEFAULT_LANGUAGE = "EN";
    @NonNull  public static final String DEFAULT_SNOOZE = "5";
    @NonNull  public static final String DEFAULT_IMPORT_TIME = "19:00";

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
