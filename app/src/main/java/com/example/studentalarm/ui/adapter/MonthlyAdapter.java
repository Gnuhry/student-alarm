package com.example.studentalarm.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.studentalarm.R;
import com.example.studentalarm.ui.dialog.EventDialog;
import com.example.studentalarm.ui.fragments.ReloadLecture;
import com.example.studentalarm.imports.LectureSchedule;

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

public class MonthlyAdapter extends RecyclerView.Adapter<MonthlyAdapter.ViewHolder> {
    @NonNull
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
    private static final String LOG = "LectureAdapter";
    private static SimpleDateFormat dayOfWeekName;
    private static DateFormat day, time;
    private static FragmentActivity activity;
    private static ReloadLecture reloadLecture;
    @NonNull
    private final List<LectureSchedule.Lecture> lecture;
    private int positionToday = -1;

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


    public MonthlyAdapter(@NonNull LectureSchedule lecture_schedule, @NonNull Context context, FragmentActivity ac, ReloadLecture reloadLecture_) {
        reloadLecture = reloadLecture_;
        activity = ac;
        dayOfWeekName = new SimpleDateFormat("EEEE", context.getResources().getConfiguration().locale);
        day = DateFormat.getDateInstance(DateFormat.LONG, context.getResources().getConfiguration().locale);
        time = DateFormat.getTimeInstance(DateFormat.LONG, context.getResources().getConfiguration().locale);
        this.lecture = new ArrayList<>();
        String formatS = "01.01.1900", format2S;
        for (LectureSchedule.Lecture l : lecture_schedule.getAllLecture(context)) {
            format2S = FORMAT.format(l.getStart());
            if (!format2S.equals(formatS)) {
                formatS = format2S;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(l.getStart());
                if (positionToday == -1 && calendar.after(Calendar.getInstance()))
                    positionToday = this.lecture.size();
                this.lecture.add(new LectureSchedule.Lecture(false, l.getStart(), new Date()));
                Log.d(LOG, "add Time: " + l.getStart().toString());
            }
            this.lecture.add(l);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_monthly, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        LectureSchedule.Lecture l = lecture.get(position);
        if (!l.getName().equals("")) {
            viewHolder.TLEvent.setVisibility(View.VISIBLE);
            viewHolder.barrier.setVisibility(View.GONE);
            viewHolder.date.setVisibility(View.GONE);
            viewHolder.title.setText(l.getName());
            viewHolder.from.setText(cutTime(time.format(l.getStart())));
            boolean aa = l.getDocent() != null, ab = l.getLocation() != null;
            viewHolder.detail.setText(aa && ab ? l.getDocent() + " - " + l.getLocation() : aa ? l.getDocent() : ab ? l.getLocation() : null);
            viewHolder.until.setText(cutTime(time.format(l.getEnd())));
            viewHolder.colorLine.setBackgroundColor(l.getColor());
            viewHolder.TLEvent.setOnClickListener(view -> new EventDialog(l, LectureSchedule.load(view.getContext()), reloadLecture).show(activity.getSupportFragmentManager(), "dialog"));
        } else {
            viewHolder.TLEvent.setVisibility(View.GONE);
            viewHolder.barrier.setVisibility(View.VISIBLE);
            viewHolder.date.setVisibility(View.VISIBLE);
            viewHolder.date.setText(String.format("%s %s", dayOfWeekName.format(l.getStart()), day.format(l.getStart())));
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
    private String cutTime(@NonNull String time) {
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
