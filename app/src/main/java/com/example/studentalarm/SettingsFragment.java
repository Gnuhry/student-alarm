package com.example.studentalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public String getOldLink() {
        return oldLink;
    }

    public boolean IsLinkIncorrect() {
        if (getContext() == null) return true;
        SharedPreferences preferences = getContext().getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        return oldLink != null && !isValidLink && !oldLink.equals(preferences.getString("Link", null));
    }


    private SharedPreferences preferences;
    private String oldLink, lastValidString;
    private boolean isLink, isValidLink;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        if (getContext() == null) return view;
        preferences = getContext().getSharedPreferences("SETTINGS", Context.MODE_PRIVATE);
        context = getContext();
        oldLink = preferences.getString("Link", null);
        if (oldLink != null)
            isLink = isValidLink = true;
        initSwitch(view, R.id.swAlarmOn, "Alarm_On");
        initSwitch(view, R.id.swAlarm, "Alarm_App");
        initSwitch(view, R.id.swImportAuto, "Import_Auto");
        initSwitch(view, R.id.swAlarmChange, "Alarm_Change");
        EditText editText = view.findViewById(R.id.edTLink);
        editText.setText(preferences.getString("Link", null));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals(oldLink) || editable.toString().equals(lastValidString)) {
                    editText.setError(null);
                    return;
                }
                isValidLink = false;
                if (URLUtil.isValidUrl(editable.toString())) {
                    editText.setError(null);
                    isLink = true;
                } else {
                    isLink = false;
                    editText.requestFocus();
                    editText.setError(getString(R.string.string_is_not_a_valid_url));
                }
                preferences.edit().putString("Link", editable.toString()).apply();
            }
        });
        Spinner spinner = view.findViewById(R.id.spImport);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, Import.ImportFunction.imports);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(preferences.getInt("Mode", 0));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preferences.edit().putInt("Mode", i).apply();
                if (view.getRootView().findViewById(R.id.swImportAuto) == null) return;
                if (i == 1) {
                    view.getRootView().findViewById(R.id.swImportAuto).setEnabled(true);
                    view.getRootView().findViewById(R.id.edTLink).setEnabled(true);
                } else {
                    view.getRootView().findViewById(R.id.swImportAuto).setEnabled(false);
                    view.getRootView().findViewById(R.id.edTLink).setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        view.findViewById(R.id.btnCheckLink).setOnClickListener(view1 -> {
            if (!isLink) {
                isValidLink = false;
                Toast.makeText(context, getString(R.string.string_is_not_a_valid_url), Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(() -> {
                if (getActivity() == null) {
                    isValidLink = false;
                    getActivity().runOnUiThread(() -> Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show());
                    Log.e("SettingsFragment", "Activity is null");
                }
                if (new ICS(preferences.getString("Link", null), true).isSuccessful()) {
                    getActivity().runOnUiThread(() -> Toast.makeText(context, getString(R.string.string_is_valid_url_to_an_ics_file), Toast.LENGTH_SHORT).show());
                    isValidLink = true;
                    lastValidString = preferences.getString("Link", null);
                } else {
                    isValidLink = false;
                    getActivity().runOnUiThread(() -> Toast.makeText(context, getString(R.string.string_is_no_link_to_an_ics_file), Toast.LENGTH_SHORT).show());

                }
            }).start();
        });
        view.findViewById(R.id.txVImportDelete).setOnClickListener(view12 -> new MaterialAlertDialogBuilder(getContext())
                .setTitle(getString(R.string.delete_import_events))
                .setMessage(getString(R.string.do_you_want_to_delete_all_the_import_events))
                .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                    Lecture_Schedule lecture_schedule = Lecture_Schedule.Load(getContext());
                    lecture_schedule.deleteAllImportEvents();
                    lecture_schedule.Save(getContext());
                })
                .setNegativeButton(getString(R.string.no), null)
                .setCancelable(true)
                .show());
        return view;
    }

    private void initSwitch(View view, int id, String key) {
        SwitchCompat switchcompat = view.findViewById(id);
        switchcompat.setChecked(preferences.getBoolean(key, false));
        switchcompat.setOnCheckedChangeListener((compoundButton, b) -> preferences.edit().putBoolean(key, b).apply());
    }
}