package com.example.studentalarm.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

public class PersonalFragment extends Fragment {

    private static final String LOG = "PersonalFragment";

    public PersonalFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity() != null)
            LectureFragment.removeLectureMenu(getActivity());
        Log.i(LOG, "open");
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        if (getContext() == null) return view;
        view.findViewById(R.id.txVBefore).setOnClickListener(v -> numberDialog(getContext(), getString(R.string.before), PreferenceKeys.BEFORE));
        view.findViewById(R.id.txtWay).setOnClickListener(v -> numberDialog(getContext(), getString(R.string.way), PreferenceKeys.WAY));
        view.findViewById(R.id.txtAfter).setOnClickListener(v -> numberDialog(getContext(), getString(R.string.after), PreferenceKeys.AFTER));
        return view;
    }

    /**
     * Init Number Dialog
     *
     * @param context context of the application
     * @param title   title of the number dialog
     * @param key     key of preference
     */
    private void numberDialog(@NonNull Context context, String title, String key) {
        Log.i(LOG, title);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.number_input, (ViewGroup) getView(), false);
        final EditText input = viewInflated.findViewById(R.id.input);
        input.setText(String.valueOf(preferences.getInt(key, 0)));
        input.requestFocus();
        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            int value = Integer.parseInt(input.getText().toString());
            if (preferences.getInt(key, 0) != value) {
                preferences.edit().putInt(key, value).apply();
                AlarmManager.updateNextAlarm(context);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }
}