package com.example.studentalarm.ui.dialog;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentalarm.EventColor;
import com.example.studentalarm.R;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.fragments.SettingsFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

public class ColorDialog extends DialogFragment {
    @NonNull
    private static final String LOG = "ImportColorDialog";
    private final SettingsFragment settingsFragment;
    private final int colorPreference;
    private String preferenceKey;
    private CallColorDialog callColorDialog;
    private RadioGroup radioGroupColours;

    public ColorDialog(int color, @NonNull CallColorDialog callColorDialog) {
        settingsFragment = null;
        this.callColorDialog = callColorDialog;
        colorPreference = color;
    }


    public ColorDialog(@NonNull SettingsFragment settingsFragment, @NonNull String preferenceKey, int pDefault) {
        this.settingsFragment = settingsFragment;
        this.preferenceKey = preferenceKey;

        colorPreference = getContext() == null ? pDefault : PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(preferenceKey, pDefault);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if (settingsFragment != null)
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
        View view = inflater.inflate(R.layout.dialog_color, container, false);
        radioGroupColours = view.findViewById(R.id.ragColor);

        if (getContext() == null) return view;

        if (settingsFragment == null || preferenceKey.equals(PreferenceKeys.FLASH_LIGHT_COLOR))
            ((TextView) view.findViewById(R.id.txVColorTitle)).setText(R.string.choose_color);

        boolean isCustomColor = true;

        Log.d(LOG, "Context is: " + getContext());

        List<EventColor> colors = EventColor.possibleColors(getContext());
        for (EventColor color : colors) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(color.toString());
            radioGroupColours.addView(radioButton);
            initRadioButtonColor(color.getColor(), radioButton);
            if (color.equals(new EventColor(colorPreference))) {
                radioButton.setChecked(true);
                isCustomColor = false;
                Log.d(LOG, "SelectedColor: " + color.toString());
            }
        }

        RadioButton custom = new RadioButton(getContext());
        custom.setText(getString(R.string.custom));
        radioGroupColours.addView(custom);
        if (isCustomColor) {
            initRadioButtonColor(colorPreference, custom);
            ((EditText) view.findViewById(R.id.edTHex)).setText(String.format("%06X", (0xFFFFFF & colorPreference)));
            view.findViewById(R.id.llCustomColor).setVisibility(View.VISIBLE);
            view.findViewById(R.id.viewColor).setBackgroundColor(colorPreference);
            custom.setChecked(true);
        } else
            custom.setTag(null);
        custom.setOnCheckedChangeListener((compoundButton, b) -> view.findViewById(R.id.llCustomColor).setVisibility(b ? View.VISIBLE : View.GONE));

        ((EditText) view.findViewById(R.id.edTHex)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    if (editable.length() == 6 || editable.length() == 8) {
                        int color = Color.parseColor("#" + editable.toString());
                        initRadioButtonColor(color, custom);
                        view.findViewById(R.id.viewColor).setBackgroundColor(color);
                    } else
                        custom.setTag(null);
                } catch (IllegalArgumentException ignored) {
                    custom.setTag(null);
                }
            }
        });


        view.findViewById(R.id.txVSave).setOnClickListener(view1 -> {
            Log.i(LOG, "Save");
            Object checkedColor = radioGroupColours.findViewById(radioGroupColours.getCheckedRadioButtonId()).getTag();
            if (checkedColor == null) {
                Toast.makeText(getContext(), R.string.no_color_selected, Toast.LENGTH_SHORT).show();
                ((EditText) view.findViewById(R.id.edTHex)).setError(getString(R.string.not_a_hey_code));
                return;
            }
            if (settingsFragment == null) callColorDialog.setColorHelp((int) checkedColor);
            else {
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putInt(preferenceKey, (int) checkedColor).apply();
                LectureSchedule.load(getContext()).changeImportedColor((int) checkedColor).save(getContext());
            }
            this.dismiss();
        });

        view.findViewById(R.id.txVCancel).setOnClickListener(view1 -> {
            Log.i(LOG, "Cancel");
            this.dismiss();
        });
        return view;
    }

    public void initRadioButtonColor(int color, RadioButton radioButton) {
        radioButton.setTag(color);
        if (Build.VERSION.SDK_INT >= 21) {
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_enabled},
                            new int[]{android.R.attr.state_enabled}
                    },
                    new int[]{Color.BLACK, color}
            );
            radioButton.setButtonTintList(colorStateList);
            radioButton.invalidate();
        }
    }

}
