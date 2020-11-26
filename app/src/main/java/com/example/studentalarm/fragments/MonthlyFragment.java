package com.example.studentalarm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.studentalarm.PreferenceKeys;
import com.example.studentalarm.R;
import com.example.studentalarm.import_.Import;
import com.example.studentalarm.import_.Lecture_Schedule;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MonthlyFragment extends Fragment implements ReloadLecture {

    private int position_today;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_montly, container, false);
        if (getContext() == null || getActivity() == null) return view;
        this.view = view;
        RefreshLectureSchedule();
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
     *
     * @param view view to display
     */
    private void LoadData(@NonNull View view) {
        if (getContext() == null) return;
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
    @Override
    public void RefreshLectureSchedule() {
        if (getContext() != null)
            if (PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(PreferenceKeys.MODE, Import.ImportFunction.NONE) != Import.ImportFunction.NONE)
                if (getActivity() != null)
                    if (Import.CheckConnection(getActivity(), getContext()))
                        new Thread(() -> {
                            Import.ImportLecture(this.getContext());
                            if (getView() != null)
                                getView().findViewById(R.id.rVEvents).post(() -> LoadData(getView().findViewById(R.id.rVEvents).getRootView()));
                        }).start();

        if (view != null)
            LoadData(view.findViewById(R.id.rVEvents).getRootView());
    }
}

