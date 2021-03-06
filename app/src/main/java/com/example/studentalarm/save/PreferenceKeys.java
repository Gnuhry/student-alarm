package com.example.studentalarm.save;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.example.studentalarm.R;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

public class PreferenceKeys {
    @NonNull
    public static final String
            MODE = "Mode",
            LINK = "Link",
            BEFORE = "BEFORE",
            WAY = "WAY",
            AFTER = "AFTER",
            ALARM_ON = "ALARM_ON",
            ALARM_PHONE = "ALARM_PHONE",
            ALARM_CHANGE = "ALARM_CHANGE",
            ALARM_SHUTDOWN = "ALARM_SHUTDOWN",
            AUTO_IMPORT = "AUTO_IMPORT",
            ALARM_TIME = "ALARM_TIME",
            SNOOZE = "SNOOZE",
            IMPORT_TIME = "IMPORT_TIME",
            LANGUAGE = "LANGUAGE",
            RINGTONE = "RINGTONE",
            DHBW_MANNHEIM_COURSE_CATEGORY = "DHBW_MANNHEIM_COURSE_CATEGORY",
            DHBW_MANNHEIM_COURSE = "DHBW_MANNHEIM_COURSE",
            WAIT_FOR_NETWORK = "WAIT_FOR_NETWORK",
            IMPORT = "IMPORT",
            IMPORT_COLOR = "IMPORT_COLOR",
            EVENT_DELETE_ALL = "EVENT_DELETE_ALL",
            EXPORT = "EXPORT",
            RESET = "RESET",
            THEME = "THEME",
            VIBRATION = "VIBRATION",
            FLASH_LIGHT = "FLASH_LIGHT",
            FLASH_LIGHT_COLOR = "FLASH_LIGHT_COLOR",
            ALARM_MODE="ALARM_MODE",
            ZIP_CODE ="ZIP_CODE",
            APP_FIRST_TIME = "APP_FIRST_TIME",
            WAKE_WEATHER="WAKE_WEATHER",
            WAKE_WEATHER_TIME="WAKE_WEATHER_TIME",
            WAKE_WEATHER_CHECK_TIME="WAKE_WEATHER_CHECK_TIME",

    DEFAULT_RINGTONE = "Default",
            DEFAULT_LANGUAGE = "EN",
            DEFAULT_SNOOZE = "5",
            DEFAULT_IMPORT_TIME = "19:00",
            DEFAULT_WAKE_WEATHER_TIME="10",
            DEFAULT_ZIP_CODE ="11011";

    public static final int
            DEFAULT_EVENT_COLOR = Color.RED,
            DEFAULT_IMPORT_EVENT_COLOR = Color.BLUE,
            DEFAULT_REGULAR_EVENT_COLOR = Color.GREEN,
            DEFAULT_FLASH_LIGHT_COLOR = Color.BLUE;

    /**
     * Reset the settings
     *
     * @param context context of application
     * @return language
     */
    @NonNull
    public static String reset(@NonNull Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        return setDefault(context);
    }

    /**
     * set the settings default
     *
     * @param context context of application
     * @return language
     */
    @NonNull
    public static String setDefault(@NonNull Context context) {
        String erg = defaultLanguage(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getString(PreferenceKeys.RINGTONE, null) == null)
            preferences.edit().putString(PreferenceKeys.RINGTONE, DEFAULT_RINGTONE).apply();
        if (preferences.getString(PreferenceKeys.LANGUAGE, null) == null)
            preferences.edit().putString(PreferenceKeys.LANGUAGE, erg).apply();
        if (preferences.getString(PreferenceKeys.SNOOZE, null) == null)
            preferences.edit().putString(PreferenceKeys.SNOOZE, DEFAULT_SNOOZE).apply();
        if (preferences.getString(PreferenceKeys.IMPORT_TIME, null) == null)
            preferences.edit().putString(PreferenceKeys.IMPORT_TIME, DEFAULT_IMPORT_TIME).apply();
        if (preferences.getInt(PreferenceKeys.IMPORT_COLOR, -1) == -1)
            preferences.edit().putInt(PreferenceKeys.IMPORT_COLOR, PreferenceKeys.DEFAULT_IMPORT_EVENT_COLOR).apply();
        if (preferences.getInt(PreferenceKeys.FLASH_LIGHT_COLOR, -1) == -1)
            preferences.edit().putInt(PreferenceKeys.FLASH_LIGHT_COLOR, PreferenceKeys.DEFAULT_FLASH_LIGHT_COLOR).apply();
        if (preferences.getLong(PreferenceKeys.ALARM_SHUTDOWN, -1) == -1)
            preferences.edit().putLong(PreferenceKeys.ALARM_SHUTDOWN, 0).apply();
        if (!preferences.getBoolean(PreferenceKeys.WAKE_WEATHER, false))
            preferences.edit().putBoolean(PreferenceKeys.WAKE_WEATHER, false).apply();
        if (preferences.getString(PreferenceKeys.WAKE_WEATHER_TIME, null) == null)
            preferences.edit().putString(PreferenceKeys.WAKE_WEATHER_TIME, PreferenceKeys.DEFAULT_WAKE_WEATHER_TIME).apply();
        if (preferences.getString(PreferenceKeys.ZIP_CODE, null) == null)
            preferences.edit().putString(PreferenceKeys.ZIP_CODE, PreferenceKeys.DEFAULT_ZIP_CODE).apply();
        if (preferences.getLong(PreferenceKeys.WAKE_WEATHER_CHECK_TIME, -1) == -1)
            preferences.edit().putLong(PreferenceKeys.WAKE_WEATHER_CHECK_TIME, 0).apply();
        if (preferences.getInt(PreferenceKeys.ALARM_MODE, -1) == -1)
            preferences.edit().putInt(PreferenceKeys.ALARM_MODE, 0).apply();
        return erg;
    }

    /**
     * Get default language
     *
     * @param context context of application
     * @return default language
     */
    @NonNull
    public static String defaultLanguage(@NonNull Context context) {
        String s = Locale.getDefault().getLanguage();
        Log.d("LANGUAGE", s);
        for (String language : context.getResources().getStringArray(R.array.language)) {
            if (s.toUpperCase().equals(language.toUpperCase())) {
                return s.toUpperCase();
            }
        }
        return DEFAULT_LANGUAGE;
    }

    /**
     * get locale
     *
     * @param context context of app
     * @return locale object
     */
    public static Locale getLocale(@Nullable Context context) {
        if (context == null)
            return Locale.getDefault();
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            locale = context.getResources().getConfiguration().getLocales().get(0);
        else
            locale = context.getResources().getConfiguration().locale;
        return locale;
    }
}
