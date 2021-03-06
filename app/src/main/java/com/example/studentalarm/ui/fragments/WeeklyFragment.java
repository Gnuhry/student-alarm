package com.example.studentalarm.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEntity;
import com.example.studentalarm.R;
import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.imports.Import;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.dialog.EventDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

public class WeeklyFragment extends Fragment implements ReloadLecture {
    private static final String LOG = "WeeklyFragment";
    @NonNull
    private final ReloadLecture lecture;
    private WeekView.SimpleAdapter<LectureSchedule.Lecture> adapter;
    @Nullable
    private ProgressDialog progress;
    private WeekView weekview;

    public WeeklyFragment() {
        lecture = this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "open");
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);
        if (getContext() == null || getActivity() == null) return view;
        weekview = view.findViewById(R.id.weekView);
        progress = new ProgressDialog(getContext());
        progress.setTitle(getString(R.string.loading));
        progress.setMessage(getString(R.string.wait_while_loading));
        progress.setCancelable(true);

        initAppBar(weekview, this.getActivity().findViewById(R.id.my_toolbar));
        initWeekView(weekview);
        new Thread(this::loadData).start();

        view.findViewById(R.id.fabAdd).setOnClickListener(view1 -> new EventDialog(null, LectureSchedule.load(getContext()), this).show(getActivity().getSupportFragmentManager(), "dialog"));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG, "resume");
        new Thread(this::loadData).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progress != null )
            progress.dismiss();
    }

    /**
     * Refresh the Lecture Schedule
     */
    public void refreshLectureSchedule() {
        Log.i(LOG, "refresh");
        if (getContext() != null &&
                PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(PreferenceKeys.MODE, Import.ImportFunction.NONE) != Import.ImportFunction.NONE &&
                getActivity() != null &&
                Import.checkConnection(getContext(), true))
            new Thread(() -> {
                Log.d(LOG, "refresh thread start");
                if (getActivity() == null) return;
                LectureFragment.animateReload(getActivity());
                Import.importLecture(this.getContext());
                if (getView() != null)
                    getView().post(() -> AlarmManager.updateNextAlarm(this.getContext()));
                loadData();
                if (getActivity() == null) return;
                LectureFragment.stopAnimateReload(getActivity());
            }).start();
        loadData();
    }

    /**
     * Load the date and display in weekView
     */
    public void loadData() {
        Log.i(LOG, "load data");
        weekview.post(() -> {
            if (progress != null)
                progress.show();
        });
        if (getContext() != null)
            adapter.submitList(LectureSchedule.load(getContext()).getAllLecture(getContext()));
        weekview.post(() -> {
            if (progress != null)
                progress.dismiss();
        });
    }

    class Adapter extends WeekView.SimpleAdapter<LectureSchedule.Lecture> {

        @Override
        public void onEventClick(@NonNull LectureSchedule.Lecture data) {
            super.onEventClick(data);
            if (getActivity() != null)
                new EventDialog(data, LectureSchedule.load(getContext()), lecture).show(getActivity().getSupportFragmentManager(), "dialog");
        }

        @NonNull
        @Override
        public WeekViewEntity onCreateEntity(@NonNull LectureSchedule.Lecture item) {
            WeekViewEntity.Style.Builder builder = new WeekViewEntity.Style.Builder();
            builder.setBackgroundColor(item.getColor());

            Calendar startCal = Calendar.getInstance();
            startCal.setTime(item.getStartWithDefaultTimeZone());

            Calendar endCal = Calendar.getInstance();
            endCal.setTime(item.getEndWithDefaultTimezone());

            WeekViewEntity.Event.Builder<LectureSchedule.Lecture> erg = new WeekViewEntity.Event.Builder<>(item);
            StringBuilder sb = new StringBuilder(item.getName());
            if (item.getDocent() != null)
                sb.append(" - ").append(item.getDocent());
            erg.setTitle(sb.toString());
            erg.setStartTime(startCal);
            erg.setEndTime(endCal);
            if (item.getLocation() != null)
                erg.setSubtitle(item.getLocation());
            erg.setStyle(builder.build());
            erg.setId(item.getId());
            erg.setAllDay(item.isAllDayEvent());
            return erg.build();
        }
    }

    /**
     * Init the weekView
     *
     * @param weekView weekView to control
     */
    private void initWeekView(@NonNull WeekView weekView) {
        Log.i(LOG, "init week view");
        Locale locale;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            locale = getResources().getConfiguration().getLocales().get(0);
        else
            locale = getResources().getConfiguration().locale;
        SimpleDateFormat format = new SimpleDateFormat("E", locale);
        DateFormat dateformat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        adapter = new Adapter();
        weekView.setAdapter(adapter);
        weekView.setTimeFormatter(hour -> locale.getLanguage().equals("en") ? (hour == 12 ? "12 pm" : (hour > 21 ? (hour - 12) + " pm" : (hour > 12 ? "0" + (hour - 12) + " pm" : (hour >= 10 ? hour + " am" : "0" + hour + " am")))) : hour >= 10 ? hour + " h" : "0" + hour + " h");
        weekView.setDateFormatter(date -> String.format("%s %s", format.format(date.getTime()), dateformat.format(date.getTime())));
    }

    /**
     * Init the app bar item
     *
     * @param weekView weekView to control
     * @param toolbar  the appbar
     */
    private void initAppBar(@NonNull WeekView weekView, @NonNull Toolbar toolbar) {
        Log.i(LOG, "init appbar");
        toolbar.getMenu().getItem(0).setOnMenuItemClickListener(menuItem -> {
            weekView.scrollToDate(Calendar.getInstance());
            return true;
        });

        toolbar.getMenu().getItem(1).setOnMenuItemClickListener(menuItem -> {
            refreshLectureSchedule();
            return true;
        });
    }


}

