package com.example.studentalarm.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.studentalarm.R;
import com.example.studentalarm.regular.RegularLectureSchedule;
import com.example.studentalarm.ui.adapter.SettingsHourAdapter;
import com.example.studentalarm.ui.fragments.RegularLectureFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RegularLectureSettingDialog extends DialogFragment {

    @NonNull
    private final SettingsHourAdapter adapter;
    @NonNull
    private final RegularLectureSchedule schedule;
    @NonNull
    private final Context context;
    @NonNull
    private final RegularLectureFragment fragment;

    public RegularLectureSettingDialog(@NonNull Context context, @NonNull Activity activity, @NonNull RegularLectureFragment fragment) {
        adapter = new SettingsHourAdapter(context, activity);
        this.fragment = fragment;
        schedule = RegularLectureSchedule.load(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_lecture_setting, container, false);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        arrayAdapter.add(getString(R.string.monday_friday));
        arrayAdapter.add(getString(R.string.monday_saturday));
        arrayAdapter.add(getString(R.string.monday_sunday));
        Spinner spinner = view.findViewById(R.id.spDays);
        spinner.setAdapter(arrayAdapter);
        int days = schedule.getDays();
        if (days < 0)
            spinner.setSelection(0);
        else
            spinner.setSelection(days - 5);


        RecyclerView rv = view.findViewById(R.id.rVHours);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        view.findViewById(R.id.txVCancel).setOnClickListener(view1 -> cancel());
        view.findViewById(R.id.txVSave).setOnClickListener(view1 -> {
            int hour = adapter.save();
            if (hour <= 0) {
                Toast.makeText(getContext(), R.string.wrong_inputs, Toast.LENGTH_LONG).show();
                return;
            }
            schedule.setDays(spinner.getSelectedItemPosition() + 5);
            schedule.setHours(hour);
            schedule.save(context);
            fragment.getFragmentParent().openFragment(fragment.getFragmentParent().getRegularFragment());
            this.dismiss();
        });
        return view;
    }

    /**
     * cancel play error
     */
    private void cancel() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(R.string.dismiss)
                .setMessage(R.string.do_you_want_to_dismiss_all_your_changes)
                .setPositiveButton(R.string.dismiss, (dialogInterface, i) -> dismiss())
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(true)
                .show();
    }
}
