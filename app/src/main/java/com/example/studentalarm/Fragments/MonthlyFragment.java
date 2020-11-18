package com.example.studentalarm.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.studentalarm.Import.Lecture_Schedule;
import com.example.studentalarm.R;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MonthlyFragment extends Fragment {

    private static LectureAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_montly, container, false);
        if (getContext() == null || getActivity() == null) return view;
        LoadData(view);
        InitAppBar(this.getActivity().findViewById(R.id.my_toolbar), (RecyclerView) view.findViewById(R.id.rVEvents));
        return view;
    }

    /**
     * Init the app bar items
     *
     * @param toolbar the appbar
     * @param rv      the recyclerview to manage
     */
    private void InitAppBar(Toolbar toolbar, RecyclerView rv) {
        toolbar.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            rv.scrollToPosition(adapter.getPositionToday());
            return true;
        });
        toolbar.getMenu().getItem(1).setOnMenuItemClickListener(menuItem -> LoadData(rv.getRootView()));
    }

    /**
     * Load the date and display in recyclerview
     *
     * @param view view to display
     * @return {true} if context is not null {false} if context is null
     */
    private boolean LoadData(View view) {
        if (getContext() == null) return false;
        RecyclerView rv = view.findViewById(R.id.rVEvents);
        adapter = new LectureAdapter(Lecture_Schedule.Load(getContext()).getAllLecture(), getContext(), getActivity());
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        rv.scrollToPosition(adapter.getPositionToday());
        return true;
    }
}

