package com.example.studentalarm.ui.fragments;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEntity;
import com.example.studentalarm.R;
import com.example.studentalarm.regular.Hours;
import com.example.studentalarm.regular.RegularLectureSchedule;
import com.example.studentalarm.ui.adapter.RegularLectureAdapter;
import com.example.studentalarm.ui.dialog.RegularLectureSettingDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RegularLectureFragment extends Fragment {
    private static final String LOG = "RegularLectureFragment";
    @NonNull
    private final PersonalFragment fragment;
    private RegularLectureSchedule regularLectureSchedule;
    private RegularLectureAdapter regularLectureAdapter;
    private Adapter adapter;
    private WeekView weekView;
    private RecyclerView rv;
    private boolean changes = false;

    public RegularLectureFragment(@NonNull PersonalFragment fragment) {
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

    @NonNull
    public PersonalFragment getFragmentParent() {
        return fragment;
    }

    /**
     * fragment has no changes
     *
     * @return {true} if no changes {false} if changes occurs
     */
    public boolean hasNoChanges() {
        return !changes;
    }

    public void setChanges(boolean changes) {
        this.changes |= changes;
    }

    /**
     * Remove menu item
     */
    public static void removeRegularLectureMenu(@NonNull Activity activity) {
        Log.i(LOG, "remove app bar");
        Toolbar toolbar = activity.findViewById(R.id.my_toolbar);
        if (toolbar != null) {
            toolbar.getMenu().getItem(2).setVisible(false);
            toolbar.getMenu().getItem(3).setVisible(false);
            toolbar.getMenu().getItem(4).setVisible(false);
        }
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
     * init the appbar
     */
    private void initAppBar() {
        Log.i(LOG, "initAppBar");
        if (getActivity() == null) return;
        Toolbar toolbar = getActivity().findViewById(R.id.my_toolbar);
        toolbar.getMenu().getItem(2).setVisible(true);
        toolbar.getMenu().getItem(2).setOnMenuItemClickListener(menuItem -> {
            if (getContext() != null) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.delete_all_events)
                        .setMessage(R.string.do_you_want_to_delete_this_events)
                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                            RegularLectureSchedule.clearSave(getContext());
                            changes = false;
                            loadRecyclerView();
                        })
                        .setNegativeButton(R.string.no, null)
                        .setCancelable(true)
                        .show();
            }
            return true;
        });
        toolbar.getMenu().getItem(3).setVisible(true);
        toolbar.getMenu().getItem(3).setOnMenuItemClickListener(menuItem -> {
            if (getContext() != null) {
                regularLectureSchedule.save(getContext());
                changes = false;
            }
            return true;
        });
        toolbar.getMenu().getItem(4).setVisible(true);
        toolbar.getMenu().getItem(4).setOnMenuItemClickListener(menuItem -> {
            new RegularLectureSettingDialog(getContext(), getActivity(), this).show(getActivity().getSupportFragmentManager(), "dialog");
            return true;
        });
    }

    /**
     * init the weekView
     */
    private void initWeekView() {
        Log.i(LOG, "init weekView");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 5, 1, 0, 0, 0);
        weekView.scrollToDate(calendar);
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            locale = getContext().getResources().getConfiguration().getLocales().get(0);
        else
            locale = getContext().getResources().getConfiguration().locale;
        SimpleDateFormat format = new SimpleDateFormat("E", locale);
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
     * loading the week view elements
     */
    private void loadDataWeekView() {
        Log.i(LOG, "load weekView");
        for (Iterator<RegularLectureSchedule.RegularLecture.RegularLectureTime> iterator = regularLectureSchedule.getRegularLectures().iterator(); iterator.hasNext(); ) {
            RegularLectureSchedule.RegularLecture.RegularLectureTime time = iterator.next();
            if (!regularLectureSchedule.getLectures().contains(time.lecture) || time.day > regularLectureSchedule.getDays() || time.hour >= regularLectureSchedule.getHours())
                iterator.remove();

        }
        //TODO save? delete all elements outside of the scope?
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
            changes = true;
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
            if (item.getActiveRoom() != null)
                erg.setSubtitle(item.getActiveRoom());
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
            if (Hours.load(getContext()).isEmpty()) {
                Toast.makeText(getContext(), "Missing hour settings", Toast.LENGTH_LONG).show();
                return;
            }
            regularLectureSchedule.addTime(time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.HOUR), selected);
            loadDataWeekView();
            changes = true;
        }
    }

}