package com.example.studentalarm.ui.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentalarm.R;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.ui.adapter.AlarmShutdownAdapter;
import com.example.studentalarm.ui.fragments.AlarmFragment;

public class AlarmShutdownDialog extends DialogFragment {
    private static final String LOG = "AlarmShutdownDialog";

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AlarmFragment.reloade();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "open");
        View view = inflater.inflate(R.layout.dialog_alarm_shutdown, container, false);

        RecyclerView rv = view.findViewById(R.id.rVLectures);
        if (getContext() != null) {
            AlarmShutdownAdapter adapter = new AlarmShutdownAdapter(LectureSchedule.load(getContext()), getContext(), getActivity(), this);
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(adapter);
        }
        AlarmFragment.stopload();

        return view;
    }

}
