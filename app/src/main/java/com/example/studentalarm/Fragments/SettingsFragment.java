package com.example.studentalarm.Fragments;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.example.studentalarm.AlarmManager;
import com.example.studentalarm.Import.Import;
import com.example.studentalarm.Import.Lecture_Schedule;
import com.example.studentalarm.MainActivity;
import com.example.studentalarm.PreferenceKeys;
import com.example.studentalarm.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.fragment.app.FragmentTransaction;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

public class SettingsFragment extends PreferenceFragmentCompat {

    SwitchPreference alarm_on, alarm_phone, alarm_change, auto_import;
    Preference import_, import_delete_all, reset;
    EditTextPreference snooze, import_time;
    ListPreference language;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        boolean bool = getPreferenceManager().getSharedPreferences().getBoolean(PreferenceKeys.ALARM_ON, false);

        //---------------Init----------------------------------
        alarm_on = findPreference(PreferenceKeys.ALARM_ON);
        alarm_phone = findPreference(PreferenceKeys.ALARM_PHONE);
        alarm_change = findPreference(PreferenceKeys.ALARM_CHANGE);
        auto_import = findPreference(PreferenceKeys.AUTO_IMPORT);
        import_ = findPreference("IMPORT");
        import_delete_all = findPreference(PreferenceKeys.IMPORT_DELETE_ALL);
        snooze = findPreference(PreferenceKeys.SNOOZE);
        import_time = findPreference(PreferenceKeys.IMPORT_TIME);
        language = findPreference(PreferenceKeys.LANGUAGE);
        reset = findPreference("RESET");

        if (alarm_on == null || alarm_phone == null || alarm_change == null || auto_import == null || import_ == null || import_delete_all == null || snooze == null || import_time == null || reset == null)
            return;

        alarm_on.setOnPreferenceChangeListener((preference, newValue) -> {
            alarm_phone.setEnabled((Boolean) newValue);
            alarm_change.setEnabled((Boolean) newValue);
            snooze.setEnabled((Boolean) newValue);
            getPreferenceManager().getSharedPreferences().edit().putBoolean(PreferenceKeys.ALARM_ON, (Boolean) newValue).apply();
            if ((Boolean) newValue) {
                if (getContext() != null && Lecture_Schedule.Load(getContext()).getAllLecture().size() == 0) {
                    Toast.makeText(getContext(), R.string.missing_events, Toast.LENGTH_SHORT).show();
                    return false;
                }
                AlarmManager.SetNextAlarm(getContext());
            } else AlarmManager.CancelNextAlarm(getContext());
            return true;
        });

        alarm_phone.setEnabled(bool);
        alarm_phone.setOnPreferenceChangeListener((preference, newValue) -> {
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
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(PreferenceKeys.ALARM_PHONE, (Boolean) newValue).apply();
                AlarmManager.UpdateNextAlarm(getContext());
                return false;
            }
            return false;
        });
        alarm_change.setEnabled(bool);

        snooze.setEnabled(bool);
        snooze.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_CLASS_NUMBER));
        snooze.setSummaryProvider(preference -> getString(R.string._min, preference.getSharedPreferences().getString(PreferenceKeys.SNOOZE, "5")));


        import_.setSummaryProvider(preference -> {
            SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
            int mode = preferences.getInt(PreferenceKeys.MODE, Import.ImportFunction.NONE);
            StringBuilder sb = new StringBuilder(Import.ImportFunction.imports.get(mode));
            if (mode == Import.ImportFunction.ICS)
                sb.append(" - ").append(preferences.getString(PreferenceKeys.LINK, null));
            return sb.toString();
        });
        import_.setOnPreferenceClickListener(preference -> {
            if (getContext() == null) return false;
            ImportDialog importDialog = new ImportDialog(this.getContext());
            importDialog.setOnCancelListener(dialogInterface -> Reload());
            importDialog.show();
            return true;
        });

        if (getPreferenceManager().getSharedPreferences().getInt(PreferenceKeys.MODE, Import.ImportFunction.NONE) == Import.ImportFunction.NONE)
            auto_import.setEnabled(false);
        auto_import.setOnPreferenceChangeListener((preference, newValue) -> {
            if (getContext() == null) return false;
            import_time.setEnabled((Boolean) newValue);
            if ((Boolean) newValue) Import.SetTimer(getContext());
            else Import.StopTimer(getContext());
            return true;
        });

        import_time.setEnabled(getPreferenceManager().getSharedPreferences().getBoolean(PreferenceKeys.AUTO_IMPORT, false));
        import_time.setSummaryProvider(preference -> getString(R.string._time, preference.getSharedPreferences().getString(PreferenceKeys.IMPORT_TIME, "19:00")));
        import_time.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_DATETIME);
            editText.setHint(R.string.hh_mm);
        });

        import_time.setOnPreferenceChangeListener((preference, newValue) -> {
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
            if (getContext() == null) return false;
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.delete_all)
                    .setMessage(R.string.do_you_want_to_delete_all_import_events)
                    .setPositiveButton(R.string.delete, (dialogInterface, i) -> removeImportLecture())
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setCancelable(true)
                    .show();
            return true;
        });

        language.setOnPreferenceChangeListener((preference, newValue) -> {
            Resources resources = getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            Configuration config = resources.getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(new Locale(((String) newValue).toLowerCase()));
            } else {
                config.locale = new Locale(((String) newValue).toLowerCase());
            }
            resources.updateConfiguration(config, dm);
            MainActivity.bottomNav.getMenu().clear();
            MainActivity.bottomNav.inflateMenu(R.menu.bottom_nav_menu);
            MainActivity.bottomNav.setSelectedItemId(R.id.setting);
            return true;
        });

        reset.setOnPreferenceClickListener(preference -> {
            if (getContext() == null) return false;
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.do_you_want_to_reset_this_application)
                    .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                        PreferenceManager.getDefaultSharedPreferences(getContext()).edit().clear().apply();
                        removeImportLecture();
                        Reload();
                    })
                    .setNegativeButton(R.string.no, null)
                    .setCancelable(true)
                    .show();
            return true;
        });
    }

    private void removeImportLecture() {
        if (getContext() == null) return;
        Lecture_Schedule l = Lecture_Schedule.Load(getContext());
        l.deleteAllImportEvents();
        l.Save(getContext());
    }

    /**
     * Reload the fragment
     */
    private void Reload() {
        if (getActivity() == null) return;
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new SettingsFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}