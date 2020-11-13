package com.example.studentalarm.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.studentalarm.Import.Lecture_Schedule;
import com.example.studentalarm.R;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MonthlyFragment extends Fragment {

    private static LectureAdapter adapter;

    public MonthlyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_montly, container, false);
        if (getContext() == null) return view;
        LoadData(view);

        view.findViewById(R.id.fabRefresh).setOnClickListener(view1 -> LoadData(view1.getRootView()));
        view.findViewById(R.id.fabToday).setOnClickListener(view1 -> ((RecyclerView) view.findViewById(R.id.rVEvents)).scrollToPosition(adapter.getPositionToday()));
        return view;
    }

    private void LoadData(View view) {
        if (getContext() == null) return;
        RecyclerView rv = view.findViewById(R.id.rVEvents);
        adapter = new LectureAdapter(Lecture_Schedule.Load(getContext()).getAllLecture(), getContext());
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        rv.scrollToPosition(adapter.getPositionToday());
    }
}

