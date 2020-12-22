package com.example.studentalarm.ui.dialog;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.studentalarm.EventColor;
import com.example.studentalarm.R;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.fragments.SettingsFragment;

import java.util.List;

public class ImportColorDialog extends DialogFragment {
    @NonNull
    private static final String LOG = "ImportColorDialog";
    private List<EventColor> colors;
    private RadioGroup radioGroupColours;
    private Button cancel, save;
    private final SharedPreferences preferences;
    private SettingsFragment settingsFragment;


    public ImportColorDialog(SharedPreferences preferences, SettingsFragment settingsFragment) {
        this.preferences = preferences;
        this.settingsFragment = settingsFragment;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        settingsFragment.reload();
        super.onDismiss(dialog);
    }

    @Override
    public void onResume() {
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "open");
        View view = inflater.inflate(R.layout.dialog_import_color, container, false);
        radioGroupColours = view.findViewById(R.id.ragColor);

        Log.d(LOG, "Context is: " + getContext());
        colors = EventColor.possibleColors(getContext());
        for (EventColor color : colors) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(color.toString());
            radioButton.setTag(color);
            radioGroupColours.addView(radioButton);
            if (color.equals(new EventColor(preferences.getInt(PreferenceKeys.IMPORT_COLOR, 0)))) {
                radioButton.setChecked(true);
                Log.d(LOG, "SelectedColor: " + color.toString());
            }

        }

        cancel = view.findViewById(R.id.btnCancel);
        save = view.findViewById(R.id.btnSave);


        save.setOnClickListener(view1 -> {
            Log.i(LOG, "Save");
            int color = -1;
            for (int i = 0; i < radioGroupColours.getChildCount(); i++) {
                if (radioGroupColours.getChildAt(i) == null)
                    Log.d(LOG, "Child is Zerro ");
                if (radioGroupColours.getChildAt(i) != null && ((RadioButton) radioGroupColours.getChildAt(i)).isChecked()) {
                    color = ((EventColor) radioGroupColours.getChildAt(i).getTag()).getColor();
                    Log.d(LOG, "Color is: " + color);
                }
            }
            if (color == -1) {
                Log.d(LOG, "Colorint Error Value: " + color);
                Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            } else {
                Log.d(LOG, "Colorint: " + color);
                preferences.edit().putInt(PreferenceKeys.IMPORT_COLOR, color).apply();
                LectureSchedule lecture_schedule = LectureSchedule.load(getContext());
                lecture_schedule.changeImportedColor(color);
                lecture_schedule.save(getContext());
            }
            this.dismiss();
        });

        cancel.setOnClickListener(view1 -> {
            Log.i(LOG, "Cancel");
            this.dismiss();
        });


        return view;
    }

}
