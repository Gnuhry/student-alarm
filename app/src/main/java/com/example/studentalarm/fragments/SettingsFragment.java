package com.example.studentalarm.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.example.studentalarm.AlarmManager;
import com.example.studentalarm.PreferenceKeys;
import com.example.studentalarm.R;
import com.example.studentalarm.import_.Import;
import com.example.studentalarm.import_.Lecture_Schedule;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        Log.i(LOG, "open");
        boolean bool = getPreferenceManager().getSharedPreferences().getBoolean(PreferenceKeys.ALARM_ON, false), bool2 = bool && !getPreferenceManager().getSharedPreferences().getBoolean(PreferenceKeys.ALARM_PHONE, false);

        //---------------Init----------------------------------
        SwitchPreference alarm_on = findPreference(PreferenceKeys.ALARM_ON),
                alarm_phone = findPreference(PreferenceKeys.ALARM_PHONE),
                alarm_change = findPreference(PreferenceKeys.ALARM_CHANGE),
                auto_import = findPreference(PreferenceKeys.AUTO_IMPORT);
        Preference import_ = findPreference("IMPORT"),
                import_delete_all = findPreference("EVENT_DELETE_ALL"),
                export = findPreference("EXPORT"),
                reset = findPreference("RESET");
        EditTextPreference snooze = findPreference(PreferenceKeys.SNOOZE),
                import_time = findPreference(PreferenceKeys.IMPORT_TIME);
        ListPreference language = findPreference(PreferenceKeys.LANGUAGE),
                ringtone = findPreference(PreferenceKeys.RINGTONE),
                theme = findPreference("THEME");

        if (alarm_on == null ||
                alarm_phone == null ||
                alarm_change == null ||
                auto_import == null ||
                import_ == null ||
                import_delete_all == null ||
                snooze == null ||
                import_time == null ||
                reset == null ||
                ringtone == null ||
                language == null ||
                theme == null ||
                export == null)
            return;

        alarm_on.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "alarm on change to " + newValue);
            if ((Boolean) newValue) {
                if (getContext() != null && Lecture_Schedule.Load(getContext()).getAllLecture().size() == 0) {
                    Toast.makeText(getContext(), R.string.missing_events, Toast.LENGTH_SHORT).show();
                    return false;
                }
                getPreferenceManager().getSharedPreferences().edit().putBoolean(PreferenceKeys.ALARM_ON, (Boolean) newValue).apply();
                AlarmManager.SetNextAlarm(getContext());
            } else {
                getPreferenceManager().getSharedPreferences().edit().putBoolean(PreferenceKeys.ALARM_ON, (Boolean) newValue).apply();
                if (getContext() != null)
                    AlarmManager.CancelNextAlarm(getContext());
            }
            boolean bool3 = (Boolean) newValue;
            alarm_phone.setEnabled(bool3);
            bool3 &= !getPreferenceManager().getSharedPreferences().getBoolean(PreferenceKeys.ALARM_PHONE, false);
            alarm_change.setEnabled(bool3);
            snooze.setEnabled(bool3);
            ringtone.setEnabled(bool3);
            return true;
        });

        alarm_phone.setEnabled(bool);
        alarm_phone.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "alarm phone change to " + newValue);
            if (getContext() == null) return false;
            if ((Boolean) newValue)
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.alarm_in_phone_watch_app)
                        .setMessage(R.string.if_alarm_is_set_in_phone_alarm_app_this_application_can_not_delete_it)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                            getPreferenceManager().getSharedPreferences().edit().putBoolean(PreferenceKeys.ALARM_PHONE, true).apply();
                            Reload();
                            AlarmManager.UpdateNextAlarm(getContext());
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .setCancelable(true)
                        .show();
            else {
                alarm_change.setEnabled(true);
                snooze.setEnabled(true);
                ringtone.setEnabled(true);
                if (getContext() == null) return false;
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(PreferenceKeys.ALARM_PHONE, (Boolean) newValue).apply();
                AlarmManager.UpdateNextAlarm(getContext());
                return true;
            }
            return false;
        });
        alarm_change.setEnabled(bool2);

        snooze.setEnabled(bool2);
        snooze.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        snooze.setSummaryProvider(preference -> getString(R.string._min, preference.getSharedPreferences().getString(PreferenceKeys.SNOOZE, PreferenceKeys.DEFAULT_SNOOZE)));
        snooze.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "alarm snooze change to " + newValue);
            return true;
        })
        ;

        ringtone.setEnabled(bool2);
        ringtone.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "alarm ringtone change to " + newValue);
            return true;
        });

        import_.setSummaryProvider(preference -> {
            SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
            int mode = preferences.getInt(PreferenceKeys.MODE, Import.ImportFunction.NONE);
            Log.d("set-frag-array", "int: " + mode + " Array: " + Import.ImportFunction.imports);
            StringBuilder sb = new StringBuilder(Import.ImportFunction.imports.get(mode));
            if (mode == Import.ImportFunction.ICS)
                sb.append(" - ").append(preferences.getString(PreferenceKeys.LINK, null));
            else if (mode == Import.ImportFunction.DHBWMa)
                sb.append(" - ").append(preferences.getString(PreferenceKeys.DHBW_MANNHEIM_COURSE, null));//if Impot Dialog Correct should never use defValue, better save than sorry !!!PreferenceKeys.DHBWMANNHEIMCOURSE funktioniert nicht
            return sb.toString();
        });
        import_.setOnPreferenceClickListener(preference -> {
            if (getContext() == null) return false;
            ImportDialog importDialog = new ImportDialog(this.getContext(), getActivity());
            importDialog.setOnCancelListener(dialogInterface -> Reload());
            importDialog.show();
            return true;
        });

        if (getPreferenceManager().getSharedPreferences().getInt(PreferenceKeys.MODE, Import.ImportFunction.NONE) == Import.ImportFunction.NONE)
            auto_import.setEnabled(false);
        auto_import.setOnPreferenceChangeListener((preference, newValue) -> {
            Log.i(LOG, "alarm import change to " + newValue);
            if (getContext() == null) return false;
            import_time.setEnabled((Boolean) newValue);
            if ((Boolean) newValue) Import.SetTimer(getContext());
            else Import.StopTimer(getContext());
            return true;
        });

        import_time.setEnabled(getPreferenceManager().getSharedPreferences().getBoolean(PreferenceKeys.AUTO_IMPORT, false));
        import_time.setSummaryProvider(preference -> getString(R.string._time, preference.getSharedPreferences().getString(PreferenceKeys.IMPORT_TIME, PreferenceKeys.DEFAULT_IMPORT_TIME)));
        import_time.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_DATETIME);
            editText.setHint(R.string.hh_mm);
        });

        import_time.setOnPreferenceChangeListener((preference, newValue) -> {
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
            return true;
        });

        import_delete_all.setOnPreferenceClickListener(preference -> {
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
            ChangeLanguage((String) newValue, getContext(), getActivity());
            Reload();
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
                        String lan = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(PreferenceKeys.LANGUAGE, PreferenceKeys.DEFAULT_LANGUAGE), lan2 = PreferenceKeys.Reset(getContext());
                        if (!lan2.equals(lan) && getActivity() != null)
                            ChangeLanguage(lan2, getContext(), getActivity());
                        removeAllEventsLecture();
                        Reload();
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
    public void ChangeLanguage(@NonNull String newValue, @NonNull Context context, @NonNull Activity activity) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(newValue.toLowerCase()));
        } else {
            config.locale = new Locale(newValue.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottomNav);
        bottomNav.getMenu().clear();
        bottomNav.inflateMenu(R.menu.bottom_nav_menu);
        Toolbar toolbar = activity.findViewById(R.id.my_toolbar);
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.lecture);
    }

    /**
     * remove all import lecture events
     */
    private void removeAllEventsLecture() {
        if (getContext() != null)
            Lecture_Schedule.Load(getContext()).clearEvents().Save(getContext());
    }

    /**
     * Reload the fragment
     */
    private void Reload() {
        if (getActivity() == null) return;
        NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_);
        if (navHostFragment != null)
            navHostFragment.getNavController().navigate(R.id.settingsFragment_);
    }

}
