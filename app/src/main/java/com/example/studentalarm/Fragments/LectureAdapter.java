package com.example.studentalarm.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.studentalarm.Import.Lecture_Schedule;
import com.example.studentalarm.R;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.ViewHolder> {
    private final List<Lecture_Schedule.Lecture> lecture;
    private int positionToday = -1;
    public static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);
    private static SimpleDateFormat day_of_week_name;
    private static DateFormat day, time;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView title, from, detail, until, date;
        public final LinearLayout LLEvent;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.MEventTitle);
            from = view.findViewById(R.id.MEventFrom);
            detail = view.findViewById(R.id.MEventDetail);
            until = view.findViewById(R.id.MEventUntil);
            date = view.findViewById(R.id.txVDate);
            LLEvent = view.findViewById(R.id.LLEvent);
        }
    }


    public LectureAdapter(List<Lecture_Schedule.Lecture> lecture, Context context) {
        day_of_week_name = new SimpleDateFormat("EEEE", context.getResources().getConfiguration().locale);
        day = DateFormat.getDateInstance(DateFormat.LONG, context.getResources().getConfiguration().locale);
        time = DateFormat.getTimeInstance(DateFormat.LONG, context.getResources().getConfiguration().locale);
        this.lecture = new ArrayList<>();
        String formatS = "01.01.1900", format2S;
        for (Lecture_Schedule.Lecture l : lecture) {
            format2S = format.format(l.getStart());
            if (!format2S.equals(formatS)) {
                formatS = format2S;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(l.getStart());
                if (positionToday == -1 && calendar.after(Calendar.getInstance()))
                    positionToday = this.lecture.size();
                this.lecture.add(new Lecture_Schedule.Lecture(null, null, null, l.getStart(), null));
            }
            this.lecture.add(l);
        }
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.monthly_event_fragment, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder viewHolder, final int position) {
        Lecture_Schedule.Lecture l = lecture.get(position);
        if (l.getName() != null) {
            viewHolder.LLEvent.setVisibility(View.VISIBLE);
            viewHolder.date.setVisibility(View.GONE);
            viewHolder.title.setText(l.getName());
            viewHolder.from.setText(CutTime(time.format(l.getStart())));
            boolean aa = l.getDocent() != null, ab = l.getLocation() != null;
            viewHolder.detail.setText(aa && ab ? l.getDocent() + " - " + l.getLocation() : aa ? l.getDocent() : ab ? l.getLocation() : null);
            viewHolder.until.setText(CutTime(time.format(l.getEnd())));
        } else {
            viewHolder.LLEvent.setVisibility(View.GONE);
            viewHolder.date.setVisibility(View.VISIBLE);
            viewHolder.date.setText(String.format("%s %s", day_of_week_name.format(l.getStart()), day.format(l.getStart())));
        }
    }

    @Override
    public int getItemCount() {
        return lecture.size();
    }

    public int getPositionToday() {
        return positionToday == -1 ? 0 : positionToday;
    }

    private String CutTime(String time) {
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
