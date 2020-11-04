package com.example.studentalarm;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.WeekView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.fragment.app.Fragment;

public class LectureFragment extends Fragment {
    WeekView.SimpleAdapter<Lecture_Schedule.Lecture> adapter;
    SimpleDateFormat format = new SimpleDateFormat("EEE dd.MM", Locale.getDefault());

    public LectureFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lecture, container, false);
        WeekView weekview = view.findViewById(R.id.weekView);
        adapter = new WeekView.SimpleAdapter<>();
        weekview.setAdapter(adapter);
        weekview.setTimeFormatter(hour -> {
            if (hour < 10)
                return "0" + hour + " h";
            return "" + hour + " h";
        });
        weekview.setDateFormatter(date -> format.format(date.getTime()));
        if (getContext() != null)
            adapter.submit(Lecture_Schedule.Load(getContext()).getAllLecture());
        return view;
    }

    private void RefreshLectureSchedule() {
        if (getContext() != null)
            new Thread(() -> adapter.submit(Import.Import(this.getContext()).getAllLecture())).start();
    }
}