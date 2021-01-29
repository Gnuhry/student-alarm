package com.example.studentalarm.ui.dialog;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.studentalarm.EventColor;
import com.example.studentalarm.R;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.fragments.SettingsFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class ImportColorDialog extends DialogFragment {
    @NonNull
    private static final String LOG = "ImportColorDialog";
    private RadioGroup radioGroupColours;
    private final SharedPreferences preferences;
    private final SettingsFragment settingsFragment;


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
        if (getDialog() != null && getDialog().getWindow() != null)
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
        if (getContext() == null)
            return view;
        List<EventColor> colors = EventColor.possibleColors(getContext());
        for (EventColor color : colors) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(color.toString());
            radioButton.setTag(color);
            radioGroupColours.addView(radioButton);
            if (Build.VERSION.SDK_INT >= 21) {
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_enabled},
                                new int[]{android.R.attr.state_enabled}
                        },
                        new int[]{Color.BLACK, color.getColor()}
                );
                radioButton.setButtonTintList(colorStateList);
                radioButton.invalidate();
            }
            if (color.equals(new EventColor(preferences.getInt(PreferenceKeys.IMPORT_COLOR, 0)))) {
                radioButton.setChecked(true);
                Log.d(LOG, "SelectedColor: " + color.toString());
            }

        }


        view.findViewById(R.id.btnSave).setOnClickListener(view1 -> {
            Log.i(LOG, "Save");
            int color = -1;
            for (int i = 0; i < radioGroupColours.getChildCount(); i++) {
                if (radioGroupColours.getChildAt(i) != null && ((RadioButton) radioGroupColours.getChildAt(i)).isChecked()) {
                    color = ((EventColor) radioGroupColours.getChildAt(i).getTag()).getColor();
                    Log.d(LOG, "Color is: " + color);
                }
            }
            if (color == -1) {
                Log.d(LOG, "Color int Error Value: " + color);
                Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            } else {
                Log.d(LOG, "Color int: " + color);
                preferences.edit().putInt(PreferenceKeys.IMPORT_COLOR, color).apply();
                LectureSchedule.load(getContext()).changeImportedColor(color).save(getContext());
            }
            this.dismiss();
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(view1 -> {
            Log.i(LOG, "Cancel");
            this.dismiss();
        });


        return view;
    }

}
