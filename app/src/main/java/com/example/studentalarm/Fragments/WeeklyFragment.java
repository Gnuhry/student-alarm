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

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class WeeklyFragment extends Fragment implements ReloadLecture {
    private WeekView.SimpleAdapter<Lecture_Schedule.Lecture> adapter;
    private ReloadLecture lecture;

    public WeeklyFragment() {
        lecture = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);
        if (getContext() == null || getActivity() == null) return view;
        WeekView weekview = view.findViewById(R.id.weekView);

        InitAppBar(weekview, this.getActivity().findViewById(R.id.my_toolbar));
        InitWeekView(weekview);
        RefreshLectureSchedule();

        view.findViewById(R.id.fabAdd).setOnClickListener(view1 -> new EventDialogFragment(null, Lecture_Schedule.Load(getContext()), (ReloadLecture) this).show(getActivity().getSupportFragmentManager(), "dialog"));
        return view;
    }

    /**
     * Init the weekView
     *
     * @param weekView weekView to control
     */
    private void InitWeekView(WeekView weekView) {
        SimpleDateFormat format = new SimpleDateFormat("E", getResources().getConfiguration().locale);
        DateFormat dateformat = DateFormat.getDateInstance(DateFormat.SHORT, getResources().getConfiguration().locale);
        adapter = new Adapter();
        weekView.setAdapter(adapter);
        weekView.setTimeFormatter(hour -> getResources().getConfiguration().locale.getLanguage().equals("en") ? (hour == 12 ? "12 pm" : (hour > 21 ? (hour - 12) + " pm" : (hour > 12 ? "0" + (hour - 12) + " pm" : (hour >= 10 ? hour + " am" : "0" + hour + " am")))) : hour >= 10 ? hour + " h" : "0" + hour + " h");
        weekView.setDateFormatter(date -> String.format("%s %s", format.format(date.getTime()), dateformat.format(date.getTime())));
        if (getContext() == null) return;
        adapter.submit(Lecture_Schedule.Load(getContext()).getAllLecture());
    }

    /**
     * Init the app bar item
     *
     * @param weekView weekView to control
     * @param toolbar  the appbar
     */
    private void InitAppBar(WeekView weekView, Toolbar toolbar) {
        toolbar.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            weekView.goToDate(Calendar.getInstance());
            return true;
        });

        toolbar.getMenu().getItem(1).setOnMenuItemClickListener(menuItem -> {
            RefreshLectureSchedule();
            return true;
        });
    }

    /**
     * Refresh the Lecture Schedule
     */
    public void RefreshLectureSchedule() {
        if (getContext() != null)
            new Thread(() -> adapter.submit(Import.ImportLecture(this.getContext()).getAllLecture())).start();
    }

    class Adapter extends WeekView.SimpleAdapter<Lecture_Schedule.Lecture> {

        @Override
        public void onEventClick(Lecture_Schedule.Lecture data) {
            super.onEventClick(data);
            if (getActivity() != null)
                new EventDialogFragment(data, Lecture_Schedule.Load(getContext()), (ReloadLecture) lecture).show(getActivity().getSupportFragmentManager(), "dialog");
        }

    }


}

