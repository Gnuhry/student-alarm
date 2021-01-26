package com.example.studentalarm.ui.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.studentalarm.R;
import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.adapter.AlarmShutdownAdapter;
import com.example.studentalarm.ui.fragments.AlarmFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmShutdownDialog extends DialogFragment {
    private static final String LOG = "AlarmShutdownDialog";
    private final AlarmFragment alarmFragment;
    private final List<LectureSchedule.Lecture> lectureSchedule;

    public AlarmShutdownDialog(AlarmFragment alarmFragment, List<LectureSchedule.Lecture> lectureSchedule) {
        this.alarmFragment = alarmFragment;
        this.lectureSchedule = lectureSchedule;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        alarmFragment.reload();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "open");
        View view = inflater.inflate(R.layout.dialog_alarm_shutdown, container, false);

        RecyclerView rv = view.findViewById(R.id.rVLectures);
        if (getContext() != null) {
            AlarmShutdownAdapter adapter = new AlarmShutdownAdapter(lectureSchedule, getContext(), getActivity(), this);
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(adapter);
        }
        view.findViewById(R.id.btnNoAlarmShutdown).setOnClickListener(view2 -> {
            if (getContext() != null) {
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putLong(PreferenceKeys.ALARM_SHUTDOWN, 0).apply();
                AlarmManager.updateNextAlarm(getContext());
                this.dismiss();
            }
        });
        alarmFragment.stopLoad();// from Progress Show
        return view;
    }

}
