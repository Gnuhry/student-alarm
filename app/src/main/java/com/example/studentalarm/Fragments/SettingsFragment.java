package com.example.studentalarm.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.studentalarm.AlarmManager;
import com.example.studentalarm.Import.Import;
import com.example.studentalarm.Import.Lecture_Schedule;
import com.example.studentalarm.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        boolean bool = getPreferenceManager().getSharedPreferences().getBoolean("ALARM_ON", false);
        findPreference("ALARM_PHONE").setEnabled(bool);
        findPreference("ALARM_CHANGE").setEnabled(bool);
        findPreference("IMPORT").setSummaryProvider(preference -> {
            SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
            int mode = preferences.getInt("Mode", Import.ImportFunction.NONE);
            StringBuilder sb = new StringBuilder(Import.ImportFunction.imports.get(mode));
            if (mode == Import.ImportFunction.ICS)
                sb.append(" - ").append(preferences.getString("Link", null));
            return sb.toString();
        });
        if (getPreferenceManager().getSharedPreferences().getInt("Mode", Import.ImportFunction.NONE) == Import.ImportFunction.NONE)
            findPreference("AUTO_IMPORT").setEnabled(false);
        findPreference("IMPORT").setOnPreferenceClickListener(preference -> {
            ImportDialog importDialog = new ImportDialog(this.getContext());
            importDialog.setOnCancelListener(dialogInterface -> Reload());
            importDialog.show();
            return true;
        });

        findPreference("IMPORT_DELETE_ALL").setOnPreferenceClickListener(preference -> {
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
        findPreference("ALARM_PHONE").setOnPreferenceChangeListener((preference, newValue) -> {
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
        findPreference("ALARM_ON").setOnPreferenceChangeListener((preference, newValue) -> {
            findPreference("ALARM_PHONE").setEnabled((Boolean) newValue);
            findPreference("ALARM_CHANGE").setEnabled((Boolean) newValue);
            if ((Boolean) newValue) AlarmManager.SetNextAlarm(getContext());
            else AlarmManager.CancelNextAlarm(getContext());
            return true;
        });
        findPreference("AUTO_IMPORT").setOnPreferenceChangeListener((preference, newValue) -> {
            if ((Boolean) newValue) new Import().SetTimer(getContext());
            else Import.StopTimer(getContext());
            return true;
        });
    }

    /**
     * Reload the fragment
     */
    private void Reload() {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, new SettingsFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}