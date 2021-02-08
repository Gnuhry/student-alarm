package com.example.studentalarm.ui.fragments;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.example.studentalarm.EventColor;
import com.example.studentalarm.Formatter;
import com.example.studentalarm.MainActivity;
import com.example.studentalarm.R;
import com.example.studentalarm.alarm.Alarm;
import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.imports.Import;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.receiver.SetAlarmLater;
import com.example.studentalarm.regular.Hours;
import com.example.studentalarm.regular.RegularLectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.dialog.ColorDialog;
import com.example.studentalarm.ui.dialog.DeleteLectureDialog;
import com.example.studentalarm.ui.dialog.ExportDialog;
import com.example.studentalarm.ui.dialog.ImportDialog;
import com.example.studentalarm.ui.dialog.RingtoneDialog;
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
                autoImport = findPreference(PreferenceKeys.AUTO_IMPORT),
                vibration = findPreference(PreferenceKeys.VIBRATION),
                flashLight = findPreference(PreferenceKeys.FLASH_LIGHT);
        Preference importPref = findPreference(PreferenceKeys.IMPORT),
                importColorPref = findPreference(PreferenceKeys.IMPORT_COLOR),
                eventDeleteAll = findPreference(PreferenceKeys.EVENT_DELETE_ALL),
                ringtone = findPreference(PreferenceKeys.RINGTONE),
                export = findPreference(PreferenceKeys.EXPORT),
                flashLightColor = findPreference(PreferenceKeys.FLASH_LIGHT_COLOR),
                reset = findPreference(PreferenceKeys.RESET),
                wakeWeather = findPreference(PreferenceKeys.WAKE_WEATHER);
        EditTextPreference snooze = findPreference(PreferenceKeys.SNOOZE),
                importTime = findPreference(PreferenceKeys.IMPORT_TIME),
                wakeWeatherTime = findPreference(PreferenceKeys.WAKE_WEATHER_TIME),
                zipCode = findPreference(PreferenceKeys.ZIP_CODE);
        ListPreference language = findPreference(PreferenceKeys.LANGUAGE),
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
                vibration == null ||
                flashLightColor == null ||
                flashLight == null ||
                export == null ||
                wakeWeather == null ||
                wakeWeatherTime == null ||
                zipCode == null)
            return;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            vibration.setVisible(false);
            flashLight.setVisible(false);
            flashLightColor.setVisible(false);
        }

        alarmOn.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "alarm on change to " + newValue);
            if ((Boolean) newValue) {
                if (getContext() != null && LectureSchedule.load(getContext()).getAllLecture(getContext()).size() == 0) {
                    Toast.makeText(getContext(), R.string.missing_events, Toast.LENGTH_SHORT).show();
                    return false;
                }
                getPreferenceManager().getSharedPreferences().edit().putBoolean(PreferenceKeys.ALARM_ON, (Boolean) newValue).apply();
                Context context = getContext();
                AlarmManager.setNextAlarm(context);
            } else {
                getPreferenceManager().getSharedPreferences().edit().putBoolean(PreferenceKeys.ALARM_ON, (Boolean) newValue).apply();
                if (getContext() != null)
                    Alarm.cancelAlarm(getContext());
            }
            boolean bool3 = (Boolean) newValue;
            alarmPhone.setEnabled(bool3);
            bool3 &= !getPreferenceManager().getSharedPreferences().getBoolean(PreferenceKeys.ALARM_PHONE, false);
            alarmChange.setEnabled(bool3);
            snooze.setEnabled(bool3);
            ringtone.setEnabled(bool3);
            vibration.setEnabled(bool3);
            flashLight.setEnabled(bool3);
            wakeWeather.setEnabled(bool3);
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
                flashLight.setEnabled(true);
                vibration.setEnabled(true);
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
        ringtone.setOnPreferenceClickListener(preference -> {
            if (getContext() == null) return false;
            RingtoneDialog dialog = new RingtoneDialog(getContext(), getActivity());
            dialog.setOnDismissListener(dialogInterface -> {
                dialog.cancel();
                reload();
            });
            dialog.show();
            return true;
        });
        ringtone.setSummaryProvider(preference -> {
            SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
            String ringtoneHelp = preferences.getString(PreferenceKeys.RINGTONE, PreferenceKeys.DEFAULT_RINGTONE);
            if (ringtoneHelp.startsWith("|")) {
                return new StringBuilder(getString(R.string.custom)).append(" ").append(Uri.parse(ringtoneHelp.substring(1)).getLastPathSegment());
            } else return ringtoneHelp;
        });

        vibration.setEnabled(bool2);
        flashLight.setEnabled(bool2);
        flashLight.setOnPreferenceChangeListener((preference, newValue) -> {
            flashLightColor.setEnabled((boolean) newValue);
            return true;
        });

        flashLightColor.setEnabled(flashLight.isChecked());
        flashLightColor.setSummaryProvider(preference -> {
            if (getContext() == null) return "";
            SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
            List<EventColor> colors = EventColor.possibleColors(getContext());
            int index = colors.indexOf(new EventColor(preferences.getInt(PreferenceKeys.FLASH_LIGHT_COLOR, 0)));
            if (index == -1)
                return getString(R.string.custom);
            EventColor color = colors.get(index);
            return getString(color.getName());
        });
        flashLightColor.setOnPreferenceClickListener(preference -> {
            if (getContext() != null && getActivity() != null)
                new ColorDialog(this, PreferenceKeys.FLASH_LIGHT_COLOR, getContext(), PreferenceKeys.DEFAULT_FLASH_LIGHT_COLOR).show(getActivity().getSupportFragmentManager(), "dialog");
            return true;
        });

        wakeWeather.setEnabled(bool2);
        wakeWeather.setSummaryProvider(preference -> getPreferenceManager().getSharedPreferences().getBoolean(PreferenceKeys.WAKE_WEATHER, false) ? getString(R.string.enabled) : getString(R.string.disabled));
        wakeWeather.setOnPreferenceClickListener(preference -> {
            Log.i(LOG, "wakeWeather");
            if (getContext() == null || getActivity() == null) return false;
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.bad_weather_alarm)
                    .setMessage(R.string.wake_up_earlier_weather)
                    .setPositiveButton(R.string.enable, (dialogInterface, i) -> {
                        Log.i(LOG, "wakeWeather - enable");
                        getPreferenceManager().getSharedPreferences().edit().putBoolean(PreferenceKeys.WAKE_WEATHER, true).apply();
                        wakeWeather.setSummaryProvider(preference2 -> getString(R.string.enabled));
                        wakeWeatherTime.setEnabled(false);
                        zipCode.setEnabled(false);
                        if (getContext() == null) return;
                        AlarmManager.updateNextAlarm(getContext());
                        reload();
                    })
                    .setNegativeButton(R.string.disable, (dialogInterface, i) -> {
                        Log.i(LOG, "wakeWeather - disable");
                        getPreferenceManager().getSharedPreferences().edit().putBoolean(PreferenceKeys.WAKE_WEATHER, false).apply();
                        wakeWeather.setSummaryProvider(preference2 -> getString(R.string.disabled));
                        wakeWeatherTime.setEnabled(false);
                        zipCode.setEnabled(false);
                        if (getContext() == null) return;
                        AlarmManager.updateNextAlarm(getContext());
                        ((android.app.AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(getContext(), 1, new Intent(getContext(), SetAlarmLater.class), 0));
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putLong(PreferenceKeys.WAKE_WEATHER_CHECK_TIME, 0).apply();
                        reload();
                    })
                    .setCancelable(true)
                    .show();
            return true;
        });
        boolean bool3 = getPreferenceManager().getSharedPreferences().getBoolean(PreferenceKeys.WAKE_WEATHER, false);

        wakeWeatherTime.setEnabled(bool3);
        wakeWeatherTime.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        wakeWeatherTime.setSummaryProvider(preference -> getString(R.string._min, preference.getSharedPreferences().getString(PreferenceKeys.WAKE_WEATHER_TIME, getString(R.string.error))));
        wakeWeatherTime.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "wakeWeatherTime set to " + newValue);
            if (getContext() == null) return false;
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(PreferenceKeys.WAKE_WEATHER_TIME, (String) newValue).apply();
            AlarmManager.updateNextAlarm(getContext());
            return true;
        });
        zipCode.setEnabled(bool3);
        zipCode.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(5)});
        });
        zipCode.setSummaryProvider(preference -> preference.getSharedPreferences().getString(PreferenceKeys.ZIP_CODE, getString(R.string.error)));
        zipCode.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "wakeWeatherTime set to " + newValue);
            if (getContext() == null) return false;
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString(PreferenceKeys.ZIP_CODE, (String) newValue).apply();
            AlarmManager.updateNextAlarm(getContext());
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
            if (index == -1)
                return getString(R.string.custom);
            EventColor color = colors.get(index);
            return getString(color.getName());
        });
        importColorPref.setOnPreferenceClickListener(preference -> {
            if (getContext() != null && getActivity() != null)
                new ColorDialog(this, PreferenceKeys.IMPORT_COLOR, getContext(), PreferenceKeys.DEFAULT_IMPORT_EVENT_COLOR).show(getActivity().getSupportFragmentManager(), "dialog");
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
            SimpleDateFormat format = Formatter.timeFormatter();
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
            ((MainActivity) getActivity()).checkLanguage();
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
                        changeLanguage(PreferenceKeys.reset(getContext()), getContext(), getActivity());
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
        int help = bottomNav.getSelectedItemId();
        bottomNav.getMenu().clear();
        bottomNav.inflateMenu(R.menu.bottom_nav_menu);
        bottomNav.setSelectedItemId(help);
        Toolbar toolbar = activity.findViewById(R.id.my_toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.toolbar);
    }

    /**
     * Reload the fragment
     */
    public void reload() {
        if (getActivity() == null) return;
        Log.d(LOG, "reload");
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
