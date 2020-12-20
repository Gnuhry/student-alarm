package com.example.studentalarm.save;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.studentalarm.R;

import java.util.Locale;

import androidx.annotation.NonNull;
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
            AUTO_IMPORT = "AUTO_IMPORT",
            ALARM_TIME = "ALARM_TIME",
            SNOOZE = "SNOOZE",
            IMPORT_TIME = "IMPORT_TIME",
            LANGUAGE = "LANGUAGE",
            RINGTONE = "RINGTONE",
            DHBW_MANNHEIM_COURSE_CATEGORY = "DHBWMANNHEIMCOURSECATEGORY",
            DHBW_MANNHEIM_COURSE = "DHBWMANNHEIMCOURSE",
            WAIT_FOR_NETWORK="WAIT_FOR_NETWORK",
            IMPORT ="IMPORT",
            IMPORT_COLOR="IMPORT_COLOR",
            EVENT_DELETE_ALL="EVENT_DELETE_ALL",
            EXPORT="EXPORT",
            RESET="RESET",
            THEME="THEME",

    DEFAULT_RINGTONE = "Default",
            DEFAULT_LANGUAGE = "EN",
            DEFAULT_SNOOZE = "5",
            DEFAULT_IMPORT_TIME = "19:00";

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
        if (preferences.getString(PreferenceKeys.IMPORT_COLOR, null) == null)
            preferences.edit().putInt(PreferenceKeys.IMPORT_COLOR, R.string.red).apply();//red als default Colour
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
}
