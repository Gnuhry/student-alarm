package com.example.studentalarm.fragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.studentalarm.R;
import com.example.studentalarm.import_.Lecture_Schedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.ViewHolder> {
    @NonNull
    private final List<Lecture_Schedule.Lecture> lecture;
    private int positionToday = -1;
    @NonNull
    public static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
    private static SimpleDateFormat day_of_week_name;
    private static DateFormat day, time;
    private static FragmentActivity activity;
    private static ReloadLecture reloadLecture;
    private static final String LOG = "LectureAdapter";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, from, detail, until, date;
        private final TableLayout TLEvent;
        private final View barrier, colorLine;

        public ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.MEventTitle);
            from = view.findViewById(R.id.MEventFrom);
            detail = view.findViewById(R.id.MEventDetail);
            until = view.findViewById(R.id.MEventUntil);
            date = view.findViewById(R.id.txVDate);
            TLEvent = view.findViewById(R.id.TLEvent);
            barrier = view.findViewById(R.id.barrier);
            colorLine = view.findViewById(R.id.colorLine);
        }
    }


    public LectureAdapter(@NonNull Lecture_Schedule lecture_schedule, @NonNull Context context, FragmentActivity ac, ReloadLecture reloadLecture_) {
        reloadLecture = reloadLecture_;
        activity = ac;
        day_of_week_name = new SimpleDateFormat("EEEE", context.getResources().getConfiguration().locale);
        day = DateFormat.getDateInstance(DateFormat.LONG, context.getResources().getConfiguration().locale);
        time = DateFormat.getTimeInstance(DateFormat.LONG, context.getResources().getConfiguration().locale);
        this.lecture = new ArrayList<>();
        String formatS = "01.01.1900", format2S;
        for (Lecture_Schedule.Lecture l : lecture_schedule.getAllLecture()) {
            format2S = format.format(l.getStart());
            if (!format2S.equals(formatS)) {
                formatS = format2S;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(l.getStart());
                if (positionToday == -1 && calendar.after(Calendar.getInstance()))
                    positionToday = this.lecture.size();
                this.lecture.add(new Lecture_Schedule.Lecture(false, l.getStart(), new Date()));
                Log.d(LOG, "add Time: " + l.getStart().toString());
            }
            this.lecture.add(l);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.monthly_event_fragment, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Lecture_Schedule.Lecture l = lecture.get(position);
        if (!l.getName().equals("")) {
            viewHolder.TLEvent.setVisibility(View.VISIBLE);
            viewHolder.barrier.setVisibility(View.GONE);
            viewHolder.date.setVisibility(View.GONE);
            viewHolder.title.setText(l.getName());
            viewHolder.from.setText(CutTime(time.format(l.getStart())));
            boolean aa = l.getDocent() != null, ab = l.getLocation() != null;
            viewHolder.detail.setText(aa && ab ? l.getDocent() + " - " + l.getLocation() : aa ? l.getDocent() : ab ? l.getLocation() : null);
            viewHolder.until.setText(CutTime(time.format(l.getEnd())));
            viewHolder.colorLine.setBackgroundColor(l.getColor());
            viewHolder.TLEvent.setOnClickListener(view -> new EventDialogFragment(l, Lecture_Schedule.Load(view.getContext()), reloadLecture).show(activity.getSupportFragmentManager(), "dialog"));
        } else {
            viewHolder.TLEvent.setVisibility(View.GONE);
            viewHolder.barrier.setVisibility(View.VISIBLE);
            viewHolder.date.setVisibility(View.VISIBLE);
            viewHolder.date.setText(String.format("%s %s", day_of_week_name.format(l.getStart()), day.format(l.getStart())));
        }
    }


    /**
     * Get the amount of lecture
     *
     * @return lecture amount
     */
    @Override
    public int getItemCount() {
        return lecture.size();
    }

    /**
     * Get the position, where the element with the date of today is
     *
     * @return position of today
     */
    public int getPositionToday() {
        return positionToday == -1 ? 0 : positionToday;
    }

    /**
     * Format time to am/pm format
     *
     * @param time time to format
     * @return time format to am/pm format
     */
    @NonNull
    private String CutTime(@NonNull String time) {
        StringBuilder erg = new StringBuilder();
        String[] help = time.split(":");
        erg.append(help[0]).append(":").append(help[1]);
        if (time.contains("AM"))
            erg.append(" AM");
        else if (time.contains("PM"))
            erg.append(" PM");
        return erg.toString();
    }
}
