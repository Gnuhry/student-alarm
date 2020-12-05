package com.example.studentalarm.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEntity;
import com.example.studentalarm.R;
import com.example.studentalarm.regular.RegularLectureSchedule;
import com.example.studentalarm.ui.adapter.RegularLectureAdapter;
import com.example.studentalarm.ui.dialog.RegularLectureSettingDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RegularLectureFragment extends Fragment {
    private static final String LOG = "RegularLectureFragment";
    private final PersonalFragment fragment;
    private RegularLectureSchedule regularLectureSchedule;
    private RegularLectureAdapter regularLectureAdapter;
    private Adapter adapter;
    private WeekView weekView;
    private RecyclerView rv;

    public RegularLectureFragment(@Nullable PersonalFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_regular_lecture, container, false);
        if (getContext() == null || getActivity() == null) return view;

        weekView = view.findViewById(R.id.regularWeekView);
        rv = view.findViewById(R.id.rVRegularLecture);
        regularLectureSchedule = RegularLectureSchedule.load(getContext());

        initAppBar();
        initWeekView();
        loadRecyclerView();

        return view;
    }

    @NonNull
    public RegularLectureSchedule getRegularLectureSchedule() {
        return regularLectureSchedule;
    }

    public PersonalFragment getFragmentParent() {
        return fragment;
    }

    /**
     * fragment has no changes
     *
     * @return {true} if no changes {false} if changes occurs
     */
    public boolean hasNoChanges() {
        //TODO return false if changes true if not
        return false;
    }

    /**
     * init the appbar
     */
    private void initAppBar() {
        Log.i(LOG, "initAppBar");
        if (getActivity() == null) return;
        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.getMenu().getItem(2).setVisible(true);
        toolbar.getMenu().getItem(2).setOnMenuItemClickListener(menuItem -> {
            if (getContext() != null)
                regularLectureSchedule.save(getContext());
            return true;
        });
        toolbar.getMenu().getItem(3).setVisible(true);
        toolbar.getMenu().getItem(3).setOnMenuItemClickListener(menuItem -> {
            new RegularLectureSettingDialog(getContext(), getActivity(), this).show(getActivity().getSupportFragmentManager(), "dialog");
            return true;
        });
    }


    /**
     * Remove menu item
     */
    public static void removeRegularLectureMenu(@NonNull Activity activity) {
        Toolbar toolbar = activity.findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            toolbar.getMenu().getItem(2).setVisible(false);
            toolbar.getMenu().getItem(3).setVisible(false);
        }
    }

    /**
     * init the weekView
     */
    private void initWeekView() {
        Log.i(LOG, "init weekView");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 5, 1, 0, 0, 0);
        weekView.scrollToDate(calendar);
        SimpleDateFormat format = new SimpleDateFormat("E", getResources().getConfiguration().locale);
        weekView.setTimeFormatter(hour -> hour + 1 + getString(R.string.hour));
        weekView.setDateFormatter(date -> format.format(date.getTime()));
        weekView.setMinHour(0);
        weekView.setMaxHour(regularLectureSchedule.getHours()); //min 6
        weekView.setNumberOfVisibleDays(regularLectureSchedule.getDays());
        adapter = new Adapter();
        weekView.setAdapter(adapter);


        loadDataWeekView();
    }

    /**
     * loading the recycler view elements
     */
    public void loadRecyclerView() {
        Log.i(LOG, "load recyclerView");
        regularLectureAdapter = new RegularLectureAdapter(regularLectureSchedule, this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(regularLectureAdapter);
        loadDataWeekView();
    }

    /**
     * loading the week view elements
     */
    private void loadDataWeekView() {
        Log.i(LOG, "load weekView");
        for (Iterator<RegularLectureSchedule.RegularLecture.RegularLectureTime> iterator = regularLectureSchedule.getRegularLectures().iterator(); iterator.hasNext(); ) {
            RegularLectureSchedule.RegularLecture.RegularLectureTime time = iterator.next();
            if (!regularLectureSchedule.getLectures().contains(time.lecture) || time.day >= regularLectureSchedule.getDays() || time.hour >= regularLectureSchedule.getHours())
                iterator.remove();
        }
        adapter.submitList(regularLectureSchedule.getRegularLectures());
    }


    class Adapter extends WeekView.SimpleAdapter<RegularLectureSchedule.RegularLecture.RegularLectureTime> {

        public int counter = 0;

        @Override
        public void onEventClick(@NonNull RegularLectureSchedule.RegularLecture.RegularLectureTime data) {
            super.onEventClick(data);
            Log.i(LOG, "adapter-eventClick");
            RegularLectureSchedule.RegularLecture selected = regularLectureAdapter.getSelected();
            if (selected == null) return;
            if (selected.equals(data.lecture))
                regularLectureSchedule.removeTime(data.day, data.hour);
            else
                regularLectureSchedule.addTime(data.day, data.hour, selected);
            loadDataWeekView();
        }

        @NonNull
        @Override
        public WeekViewEntity onCreateEntity(@NonNull RegularLectureSchedule.RegularLecture.RegularLectureTime item) {
            WeekViewEntity.Event.Builder<RegularLectureSchedule.RegularLecture.RegularLectureTime> erg = new WeekViewEntity.Event.Builder<>(item);
            StringBuilder sb = new StringBuilder(item.lecture.getName());
            if (item.lecture.getDocent() != null)
                sb.append(" - ").append(item.lecture.getDocent());
            erg.setTitle(sb.toString());
            Calendar calendar = Calendar.getInstance(), calendar1 = Calendar.getInstance();
            calendar.set(2020, 5, item.day, item.hour, 0, 0);
            erg.setStartTime(calendar);
            calendar1.set(2020, 5, item.day, item.hour, 59, 59);
            erg.setEndTime(calendar1);
            if (item.lecture.getActiveRoom() != null)
                erg.setSubtitle(item.lecture.getRooms().get(item.room_id));
            erg.setStyle(new WeekViewEntity.Style.Builder().setBackgroundColor(item.lecture.getColor()).build());
            erg.setId(counter++);
            return erg.build();
        }

        @Override
        public void onEmptyViewClick(@NonNull Calendar time) {
            super.onEmptyViewClick(time);
            Log.i(LOG, "adapter-emptyClick");
            RegularLectureSchedule.RegularLecture selected = regularLectureAdapter.getSelected();
            if (selected == null) return;
            regularLectureSchedule.addTime(time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.HOUR), selected);
            loadDataWeekView();
        }
    }

}