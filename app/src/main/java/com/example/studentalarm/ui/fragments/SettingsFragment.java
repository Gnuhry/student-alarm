package com.example.studentalarm.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.example.studentalarm.EventColor;
import com.example.studentalarm.R;
import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.imports.Import;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.regular.Hours;
import com.example.studentalarm.regular.RegularLectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.dialog.DeleteLectureDialog;
import com.example.studentalarm.ui.dialog.ExportDialog;
import com.example.studentalarm.ui.dialog.ImportColorDialog;
import com.example.studentalarm.ui.dialog.ImportDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String LOG = "SettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        if (getActivity() != null) {
            RegularLectureFragment.removeRegularLectureMenu(getActivity());
            LectureFragment.removeLectureMenu(getActivity());
        }
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        Log.i(LOG, "open");
        boolean bool = getPreferenceManager().getSharedPreferences().getBoolean(PreferenceKeys.ALARM_ON, false), bool2 = bool && !getPreferenceManager().getSharedPreferences().getBoolean(PreferenceKeys.ALARM_PHONE, false);

        //---------------Init----------------------------------
        SwitchPreference alarmOn = findPreference(PreferenceKeys.ALARM_ON),
                alarmPhone = findPreference(PreferenceKeys.ALARM_PHONE),
                alarmChange = findPreference(PreferenceKeys.ALARM_CHANGE),
                autoImport = findPreference(PreferenceKeys.AUTO_IMPORT);
        Preference importPref = findPreference(PreferenceKeys.IMPORT),
                importColorPref = findPreference(PreferenceKeys.IMPORT_COLOR),
                eventDeleteAll = findPreference(PreferenceKeys.EVENT_DELETE_ALL),
                export = findPreference(PreferenceKeys.EXPORT),
                reset = findPreference(PreferenceKeys.RESET);
        EditTextPreference snooze = findPreference(PreferenceKeys.SNOOZE),
                importTime = findPreference(PreferenceKeys.IMPORT_TIME);
        ListPreference language = findPreference(PreferenceKeys.LANGUAGE),
                ringtone = findPreference(PreferenceKeys.RINGTONE),
                theme = findPreference(PreferenceKeys.THEME);

        if (alarmOn == null ||
                alarmPhone == null ||
                alarmChange == null ||
                autoImport == null ||
                importPref == null ||
                importColorPref == null ||
                eventDeleteAll == null ||
                snooze == null ||
                importTime == null ||
                reset == null ||
                ringtone == null ||
                language == null ||
                theme == null ||
                export == null)
            return;

        alarmOn.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "alarm on change to " + newValue);
            if ((Boolean) newValue) {
                if (getContext() != null && LectureSchedule.load(getContext()).getAllLecture(getContext()).size() == 0) {
                    Toast.makeText(getContext(), R.string.missing_events, Toast.LENGTH_SHORT).show();
                    return false;
                }
                getPreferenceManager().getSharedPreferences().edit().putBoolean(PreferenceKeys.ALARM_ON, (Boolean) newValue).apply();
                AlarmManager.setNextAlarm(getContext());
            } else {
                getPreferenceManager().getSharedPreferences().edit().putBoolean(PreferenceKeys.ALARM_ON, (Boolean) newValue).apply();
                if (getContext() != null)
                    AlarmManager.cancelNextAlarm(getContext());
            }
            boolean bool3 = (Boolean) newValue;
            alarmPhone.setEnabled(bool3);
            bool3 &= !getPreferenceManager().getSharedPreferences().getBoolean(PreferenceKeys.ALARM_PHONE, false);
            alarmChange.setEnabled(bool3);
            snooze.setEnabled(bool3);
            ringtone.setEnabled(bool3);
            return true;
        });

        alarmPhone.setEnabled(bool);
        alarmPhone.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "alarm phone change to " + newValue);
            if (getContext() == null) return false;
            if ((Boolean) newValue)
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.alarm_in_phone_watch_app)
                        .setMessage(R.string.if_alarm_is_set_in_phone_alarm_app_this_application_can_not_delete_it)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                            getPreferenceManager().getSharedPreferences().edit().putBoolean(PreferenceKeys.ALARM_PHONE, true).apply();
                            reload();
                            AlarmManager.updateNextAlarm(getContext());
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .setCancelable(true)
                        .show();
            else {
                alarmChange.setEnabled(true);
                snooze.setEnabled(true);
                ringtone.setEnabled(true);
                if (getContext() == null) return false;
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(PreferenceKeys.ALARM_PHONE, (Boolean) newValue).apply();
                AlarmManager.updateNextAlarm(getContext());
                return true;
            }
            return false;
        });
        alarmChange.setEnabled(bool2);

        snooze.setEnabled(bool2);
        snooze.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        snooze.setSummaryProvider(preference -> getString(R.string._min, preference.getSharedPreferences().getString(PreferenceKeys.SNOOZE, PreferenceKeys.DEFAULT_SNOOZE)));
        snooze.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "alarm snooze change to " + newValue);
            return true;
        });

        ringtone.setEnabled(bool2);
        ringtone.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "alarm ringtone change to " + newValue);
            if (getContext() != null) {
                switch ((String) newValue) {
                    case "gentle":
                        MediaPlayer.create(getContext().getApplicationContext(), R.raw.alarm_gentle).start();
                    case "DEFAULT":
                    default:
                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        MediaPlayer.create(getContext().getApplicationContext(), alarmSound).start();
                }
            }
            return true;
        });

        importPref.setSummaryProvider(preference -> {
            SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
            int mode = preferences.getInt(PreferenceKeys.MODE, Import.ImportFunction.NONE);
            Log.d("set-frag-array", "int: " + mode + " Array: " + Import.ImportFunction.IMPORTS);
            StringBuilder sb = new StringBuilder(Import.ImportFunction.IMPORTS.get(mode));
            if (mode == Import.ImportFunction.ICS)
                sb.append(" - ").append(preferences.getString(PreferenceKeys.LINK, null));
            else if (mode == Import.ImportFunction.DHBWMA)
                sb.append(" - ").append(preferences.getString(PreferenceKeys.DHBW_MANNHEIM_COURSE, null));
            return sb.toString();
        });
        importPref.setOnPreferenceClickListener(preference -> {
            if (getContext() == null) return false;
            if (Import.checkConnection(getContext(), true)) {
                ImportDialog importDialog = new ImportDialog(this.getContext(), this.getActivity());
                importDialog.setOnCancelListener(dialogInterface -> reload());
                importDialog.show();
            }
            return true;
        });

        importColorPref.setSummaryProvider(preference -> {
            if (getContext() == null) return "";
            SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
            List<EventColor> colors = EventColor.possibleColors(getContext());
            int index = colors.indexOf(new EventColor(preferences.getInt(PreferenceKeys.IMPORT_COLOR, 0)));
            Log.d(LOG, "Index Of Color " + index);
            if (index == -1)
                return null;
            EventColor color = colors.get(index);
            Log.d(LOG, "Name of Color " + getResources().getString(color.getName()));
            return getResources().getString(color.getName());
            //return getResources().getString(preferences.getInt(PreferenceKeys.IMPORT_COLOR,R.string.error));//Uses String ID to use String ini XML
        });
        importColorPref.setOnPreferenceClickListener(preference -> {
            SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
            if (getContext() != null && getActivity() != null)
                new ImportColorDialog(preferences, this).show(getActivity().getSupportFragmentManager(), "dialog");
            return true;
        });


        int mode2 = getPreferenceManager().getSharedPreferences().getInt(PreferenceKeys.MODE, Import.ImportFunction.NONE);
        if (mode2 == Import.ImportFunction.NONE || mode2 == Import.ImportFunction.PHONE)
            autoImport.setEnabled(false);
        autoImport.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "alarm import change to " + newValue);
            if (getContext() == null) return false;
            importTime.setEnabled((Boolean) newValue);
            if ((Boolean) newValue) Import.setTimer(getContext());
            else Import.stopTimer(getContext());
            return true;
        });

        importTime.setEnabled(getPreferenceManager().getSharedPreferences().getBoolean(PreferenceKeys.AUTO_IMPORT, false));
        importTime.setSummaryProvider(preference -> getString(R.string._time, preference.getSharedPreferences().getString(PreferenceKeys.IMPORT_TIME, PreferenceKeys.DEFAULT_IMPORT_TIME)));
        importTime.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_DATETIME);
            editText.setHint(R.string.hh_mm);
        });

        importTime.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "import time change to " + newValue);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.GERMAN);
            String value = (String) newValue;
            if (value.length() != 5) {
                Toast.makeText(getContext(), getString(R.string.wrong_time_format), Toast.LENGTH_SHORT).show();
                return false;
            }
            format.setLenient(false);
            try {
                format.parse(value.trim());
            } catch (ParseException pe) {
                Toast.makeText(getContext(), getString(R.string.wrong_time_format), Toast.LENGTH_SHORT).show();
                return false;
            }
            if (getContext() == null) return false;
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(PreferenceKeys.IMPORT_TIME, value.trim()).apply();
            Import.stopTimer(getContext());
            Import.setTimer(getContext());
            return true;
        });

        eventDeleteAll.setOnPreferenceClickListener(preference -> {
            if (getContext() != null)
                new DeleteLectureDialog(getContext()).show();
            return true;
        });

        export.setOnPreferenceClickListener(preference -> {
            if (getContext() != null && getActivity() != null)
                new ExportDialog(getContext(), getActivity()).show();
            return true;
        });

        language.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "language change to " + newValue);
            if (getContext() == null || getActivity() == null) return false;
            changeLanguage((String) newValue, getContext(), getActivity());
            reload();
            return true;
        });

        theme.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "theme change to " + newValue);
            int mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
            switch ((String) newValue) {
                case "Default":
                    mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                    break;
                case "Light":
                    mode = AppCompatDelegate.MODE_NIGHT_NO;
                    break;
                case "Dark":
                    mode = AppCompatDelegate.MODE_NIGHT_YES;
                    break;
            }
            AppCompatDelegate.setDefaultNightMode(mode);
            reload();
            return true;
        });

        reset.setOnPreferenceClickListener(preference -> {
            Log.i(LOG, "reset");
            if (getContext() == null || getActivity() == null) return false;
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.do_you_want_to_reset_this_application)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        Log.i(LOG, "reset - positive");
                        String lan = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(PreferenceKeys.LANGUAGE, PreferenceKeys.DEFAULT_LANGUAGE), lan2 = PreferenceKeys.reset(getContext());
                        if (!lan2.equals(lan) && getActivity() != null)
                            changeLanguage(lan2, getContext(), getActivity());
                        removeAllEventsLecture();
                        RegularLectureSchedule.clearSave(getContext());
                        Hours.clearHours(getContext());
                        reload();
                    })
                    .setNegativeButton(R.string.no, null)
                    .setCancelable(true)
                    .show();
            return true;
        });
    }

    /**
     * change the application language
     *
     * @param newValue new language code
     */
    public void changeLanguage(@NonNull String newValue, @NonNull Context context, @NonNull Activity activity) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            config.setLocale(new Locale(newValue.toLowerCase()));
        else
            config.locale = new Locale(newValue.toLowerCase());
        resources.updateConfiguration(config, dm);
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottomNav);
        bottomNav.getMenu().clear();
        bottomNav.inflateMenu(R.menu.bottom_nav_menu);
        Toolbar toolbar = activity.findViewById(R.id.my_toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.toolbar);
    }

    /**
     * Reload the fragment
     */
    public void reload() {
        if (getActivity() == null) return;
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_);
        if (navHostFragment != null)
            navHostFragment.getNavController().navigate(R.id.settingsFragment_);
    }

    /**
     * remove all import lecture events
     */
    private void removeAllEventsLecture() {
        if (getContext() != null)
            LectureSchedule.load(getContext()).clearEvents().save(getContext());
    }
}
