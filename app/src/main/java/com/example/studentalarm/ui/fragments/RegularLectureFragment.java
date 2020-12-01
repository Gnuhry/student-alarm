package com.example.studentalarm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.WeekView;
import com.example.studentalarm.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.fragment.app.Fragment;

public class RegularLectureFragment extends Fragment {


    public RegularLectureFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regular_lecture, container, false);
        WeekView weekView = view.findViewById(R.id.regularWeekView);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 5, 1, 0, 0, 0);
        weekView.scrollToDate(calendar);

        SimpleDateFormat format = new SimpleDateFormat("E", getResources().getConfiguration().locale);
        weekView.setTimeFormatter(hour -> hour + 1 + " Stunde");
        weekView.setMinHour(0);
        weekView.setMaxHour(10);
        weekView.setDateFormatter(date -> format.format(date.getTime()));

        

        return view;
    }
}