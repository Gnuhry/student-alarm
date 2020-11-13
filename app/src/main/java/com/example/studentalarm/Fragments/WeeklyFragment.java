package com.example.studentalarm.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.WeekView;
import com.example.studentalarm.Import.Import;
import com.example.studentalarm.Import.Lecture_Schedule;
import com.example.studentalarm.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class WeeklyFragment extends Fragment {
    WeekView.SimpleAdapter<Lecture_Schedule.Lecture> adapter;
    SimpleDateFormat format;
    DateFormat date;

    public WeeklyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);
        WeekView weekview = view.findViewById(R.id.weekView);
        format = new SimpleDateFormat("E", getResources().getConfiguration().locale);
        date = DateFormat.getDateInstance(DateFormat.SHORT, getResources().getConfiguration().locale);
        adapter = new Adapter();
        weekview.setAdapter(adapter);
        weekview.setTimeFormatter(hour -> getResources().getConfiguration().locale.getLanguage().equals("en") ? (hour == 12 ? "12 pm" : (hour > 21 ? (hour - 12) + " pm" : (hour > 12 ? "0" + (hour - 12) + " pm" : (hour >= 10 ? hour + " am" : "0" + hour + " am")))) : hour >= 10 ? hour + " h" : "0" + hour + " h");
        weekview.setDateFormatter(date -> String.format("%s %s", format.format(date.getTime()), this.date.format(date.getTime())));
        if (getContext() != null)
            adapter.submit(Lecture_Schedule.Load(getContext()).getAllLecture());
        RefreshLectureSchedule();
        view.findViewById(R.id.fabRefresh).setOnClickListener(view1 -> RefreshLectureSchedule());
        view.findViewById(R.id.fabToday).setOnClickListener(view1 -> weekview.goToDate(Calendar.getInstance()));
        return view;
    }

    /**
     * Refresh the Lecture Schedule
     */
    private void RefreshLectureSchedule() {
        if (getContext() != null)
            new Thread(() -> adapter.submit(Import.ImportLecture(this.getContext()).getAllLecture())).start();
    }

    class Adapter extends WeekView.SimpleAdapter<Lecture_Schedule.Lecture> {

        @Override
        public void onEventClick(Lecture_Schedule.Lecture data) {
            super.onEventClick(data);
            if (getActivity() != null)
                new EventDialogFragment(data).show(getActivity().getSupportFragmentManager(), "dialog");
        }

    }
}

