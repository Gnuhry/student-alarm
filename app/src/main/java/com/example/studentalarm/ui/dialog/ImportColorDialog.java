package com.example.studentalarm.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentalarm.EventColor;
import com.example.studentalarm.PossibleColors;
import com.example.studentalarm.R;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.adapter.ExportAdapter;
import com.example.studentalarm.ui.fragments.SettingsFragment;

import java.util.List;

public class ImportColorDialog extends DialogFragment {
    @NonNull
    private static final String LOG = "ImportColorDialog";
    private List<EventColor> colors;
    private Spinner spinner;
    private Button cancel,save;
    private final SharedPreferences preferences;
    private SettingsFragment settingsFragment;



    public ImportColorDialog(SharedPreferences preferences, SettingsFragment settingsFragment) {
        this.preferences=preferences;
        this.settingsFragment=settingsFragment;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        settingsFragment.reload();
        Toast.makeText(getContext(), getString(R.string.color_change_after_reloade), Toast.LENGTH_SHORT).show();
        super.onDismiss(dialog);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "open");
        View view = inflater.inflate(R.layout.dialog_import_color, container, false);
        spinner = view.findViewById(R.id.spnColor);

        Log.d(LOG, "Context is: "+getContext());
        colors= new PossibleColors(getContext()).colorList();
        ArrayAdapter<EventColor> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.addAll(colors);
        spinner.setAdapter(adapter);
        //spinner.setSelection(colors.indexOf(new EventColor(Color.BLUE,getContext())));
        spinner.setSelection(colors.indexOf(new EventColor(preferences.getInt(PreferenceKeys.IMPORT_COLOR,0),getContext())));
        cancel=view.findViewById(R.id.btnCancel);
        save=view.findViewById(R.id.btnSave);


        save.setOnClickListener(view1 -> {
            Log.i(LOG, "Save");
            int color = ((EventColor) spinner.getSelectedItem()).getColor();
            Log.d(LOG, "Colorint: "+color);
            //Log.d(LOG, "colorName: ");
            preferences.edit().putInt(PreferenceKeys.IMPORT_COLOR,color).apply();
            this.dismiss();
        });

        cancel.setOnClickListener(view1 -> {
            Log.i(LOG, "Cancel");
            this.dismiss();
        });


        return view;
    }

}
