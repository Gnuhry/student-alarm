package com.example.studentalarm.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.studentalarm.AlarmManager;
import com.example.studentalarm.Import.Import;
import com.example.studentalarm.Import.Lecture_Schedule;
import com.example.studentalarm.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

public class SettingsFragment extends PreferenceFragmentCompat {

    SwitchPreference alarm_on, alarm_phone, alarm_change, auto_import;
    Preference import_, import_delete_all;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        boolean bool = getPreferenceManager().getSharedPreferences().getBoolean("ALARM_ON", false);

        //---------------Init----------------------------------
        alarm_on = findPreference("ALARM_ON");
        alarm_phone = findPreference("ALARM_PHONE");
        alarm_change = findPreference("ALARM_CHANGE");
        auto_import = findPreference("AUTO_IMPORT");
        import_ = findPreference("IMPORT");
        import_delete_all = findPreference("IMPORT_DELETE_ALL");

        if (alarm_on == null || alarm_phone == null || alarm_change == null || auto_import == null || import_ == null || import_delete_all == null)
            return;

        alarm_on.setOnPreferenceChangeListener((preference, newValue) -> {
            alarm_phone.setEnabled((Boolean) newValue);
            alarm_change.setEnabled((Boolean) newValue);
            if ((Boolean) newValue) AlarmManager.SetNextAlarm(getContext());
            else AlarmManager.CancelNextAlarm(getContext());
            return true;
        });

        alarm_phone.setEnabled(bool);
        alarm_phone.setOnPreferenceChangeListener((preference, newValue) -> {
            if (getContext() == null) return false;
            if ((Boolean) newValue)
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.alarm_in_phone_app)
                        .setMessage(R.string.if_alarm_is_set_in_phone_alarm_app_this_application_can_not_delete_it)
                        .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                            getPreferenceManager().getSharedPreferences().edit().putBoolean("ALARM_PHONE", true).apply();
                            Reload();
                            AlarmManager.UpdateNextAlarm(getContext());
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .setCancelable(true)
                        .show();
            else {
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean("ALARM_PHONE", (Boolean) newValue).apply();
                AlarmManager.UpdateNextAlarm(getContext());
                return false;
            }
            return false;
        });

        alarm_change.setEnabled(bool);

        import_.setSummaryProvider(preference -> {
            SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
            int mode = preferences.getInt("Mode", Import.ImportFunction.NONE);
            StringBuilder sb = new StringBuilder(Import.ImportFunction.imports.get(mode));
            if (mode == Import.ImportFunction.ICS)
                sb.append(" - ").append(preferences.getString("Link", null));
            return sb.toString();
        });
        import_.setOnPreferenceClickListener(preference -> {
            if (getContext() == null) return false;
            ImportDialog importDialog = new ImportDialog(this.getContext());
            importDialog.setOnCancelListener(dialogInterface -> Reload());
            importDialog.show();
            return true;
        });

        if (getPreferenceManager().getSharedPreferences().getInt("Mode", Import.ImportFunction.NONE) == Import.ImportFunction.NONE)
            auto_import.setEnabled(false);
        auto_import.setOnPreferenceChangeListener((preference, newValue) -> {
            if (getContext() == null) return false;
            if ((Boolean) newValue) Import.SetTimer(getContext());
            else Import.StopTimer(getContext());
            return true;
        });

        import_delete_all.setOnPreferenceClickListener(preference -> {
            if (getContext() == null) return false;
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.delete_all)
                    .setMessage(R.string.do_you_want_to_delete_all_import_events)
                    .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                        Lecture_Schedule l = Lecture_Schedule.Load(getContext());
                        l.deleteAllImportEvents();
                        l.Save(getContext());
                    })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setCancelable(true)
                    .show();
            return true;
        });
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