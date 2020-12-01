package com.example.studentalarm.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.R;
import com.example.studentalarm.adapter.MonthlyAdapter;
import com.example.studentalarm.dialog.EventDialog;
import com.example.studentalarm.imports.Import;
import com.example.studentalarm.imports.LectureSchedule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MonthlyFragment extends Fragment implements ReloadLecture {

    private int position_today;
    @Nullable
    private View view;
    private static final String LOG = "MonthlyFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "open");
        View view = inflater.inflate(R.layout.fragment_montly, container, false);
        if (getContext() == null || getActivity() == null) return view;
        this.view = view;
        loadData();
        initAppBar(this.getActivity().findViewById(R.id.my_toolbar), view.findViewById(R.id.rVEvents));
        view.findViewById(R.id.fabAdd).setOnClickListener(view1 -> new EventDialog(null, LectureSchedule.load(getContext()), this).show(getActivity().getSupportFragmentManager(), "dialog"));
        return view;
    }

    /**
     * Init the app bar items
     *
     * @param toolbar the appbar
     * @param rv      the recyclerview to manage
     */
    private void initAppBar(@NonNull Toolbar toolbar, @NonNull RecyclerView rv) {
        Log.i(LOG, "init appbar");
        toolbar.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            rv.scrollToPosition(position_today);
            return true;
        });
        toolbar.getMenu().getItem(1).setOnMenuItemClickListener(menuItem -> {
            refreshLectureSchedule();
            return true;
        });
    }

    /**
     * Load the date and display in recyclerview
     */
    @Override
    public void loadData() {
        Log.i(LOG, "load data to display");
        if (getContext() == null) return;
        if (view == null && getView() == null) return;
        if (view == null && getView() != null) view = getView();
        RecyclerView rv = view.findViewById(R.id.rVEvents);
        MonthlyAdapter adapter = new MonthlyAdapter(LectureSchedule.load(getContext()), getContext(), getActivity(), this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        rv.scrollToPosition(adapter.getPositionToday());
        position_today = adapter.getPositionToday();
    }

    /**
     * Refresh the Lecture Schedule
     */
    public void refreshLectureSchedule() {
        Log.i(LOG, "refresh");
        if (getContext() != null &&
                PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(PreferenceKeys.MODE, Import.ImportFunction.NONE) != Import.ImportFunction.NONE &&
                getActivity() != null &&
                Import.checkConnection(getContext()))
            new Thread(() -> {
                Log.d(LOG, "start thread");
                if (getActivity() == null) return;
                LectureFragment.animateReload(getActivity());
                Import.importLecture(this.getContext());
                if (getView() != null)
                    getView().post(() -> {
                        AlarmManager.updateNextAlarm(this.getContext());
                        loadData();
                    });
                if (getActivity() == null) return;
                LectureFragment.stopAnimateReload(getActivity());
            }).start();

        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG, "resume");
        loadData();
    }
}

