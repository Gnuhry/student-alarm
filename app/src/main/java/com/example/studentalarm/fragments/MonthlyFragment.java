package com.example.studentalarm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.studentalarm.AlarmManager;
import com.example.studentalarm.PreferenceKeys;
import com.example.studentalarm.R;
import com.example.studentalarm.import_.Import;
import com.example.studentalarm.import_.Lecture_Schedule;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_montly, container, false);
        if (getContext() == null || getActivity() == null) return view;
        this.view = view;
        LoadData();
        InitAppBar(this.getActivity().findViewById(R.id.my_toolbar), view.findViewById(R.id.rVEvents));
        view.findViewById(R.id.fabAdd).setOnClickListener(view1 -> new EventDialogFragment(null, Lecture_Schedule.Load(getContext()), this).show(getActivity().getSupportFragmentManager(), "dialog"));
        return view;
    }

    /**
     * Init the app bar items
     *
     * @param toolbar the appbar
     * @param rv      the recyclerview to manage
     */
    private void InitAppBar(@NonNull Toolbar toolbar, @NonNull RecyclerView rv) {
        toolbar.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            rv.scrollToPosition(position_today);
            return true;
        });
        toolbar.getMenu().getItem(1).setOnMenuItemClickListener(menuItem -> {
            RefreshLectureSchedule();
            return true;
        });
    }

    /**
     * Load the date and display in recyclerview
     */
    @Override
    public void LoadData() {
        if (getContext() == null) return;
        if (view == null && getView() == null) return;
        if (view == null && getView() != null) view = getView();
        RecyclerView rv = view.findViewById(R.id.rVEvents);
        LectureAdapter adapter = new LectureAdapter(Lecture_Schedule.Load(getContext()), getContext(), getActivity(), this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        rv.scrollToPosition(adapter.getPositionToday());
        position_today = adapter.getPositionToday();
    }

    /**
     * Refresh the Lecture Schedule
     */
    public void RefreshLectureSchedule() {
        if (getContext() != null &&
                PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(PreferenceKeys.MODE, Import.ImportFunction.NONE) != Import.ImportFunction.NONE &&
                getActivity() != null &&
                Import.CheckConnection(getActivity(), getContext()))
            new Thread(() -> {
                if (getActivity() == null) return;
                LectureFragment.AnimateReload(getActivity());
                Import.ImportLecture(this.getContext());
                if (getView() != null)
                    getView().post(() -> {
                        AlarmManager.UpdateNextAlarm(this.getContext());
                        LoadData();
                    });
                if (getActivity() == null) return;
                LectureFragment.StopAnimateReload(getActivity());
            }).start();

        LoadData();
    }
}

