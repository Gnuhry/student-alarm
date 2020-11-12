package com.example.studentalarm.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.studentalarm.Import.Lecture_Schedule;
import com.example.studentalarm.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MonthlyFragment extends Fragment {


    public MonthlyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_montly, container, false);
        if (getContext() == null) return view;
        CustomAdapter adapter = new CustomAdapter(Lecture_Schedule.Load(getContext()).getAllLecture());
        RecyclerView rv = view.findViewById(R.id.rVEvents);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        rv.scrollToPosition(adapter.getPositionToday());
        return view;
    }
}

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private final List<Lecture_Schedule.Lecture> lecture;
    private int positionToday = -1;
    public static final SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.GERMAN), format2 = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN);

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


    public CustomAdapter(List<Lecture_Schedule.Lecture> lecture) {
        this.lecture = new ArrayList<>();
        String formatS = "01.01.1900", format2S;
        for (Lecture_Schedule.Lecture l : lecture) {
            format2S = format2.format(l.getStart());
            if (!format2S.equals(formatS)) {
                formatS = format2S;
                Calendar calendar=Calendar.getInstance();
                calendar.setTime(l.getStart());
                if (positionToday==-1&&calendar.after(Calendar.getInstance()))
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
            viewHolder.from.setText(format.format(l.getStart()));
            StringBuilder sb = new StringBuilder();
            if (l.getDocent() != null)
                sb.append(l.getDocent());
            if (l.getLocation() != null)
                sb.append(" - ").append(l.getLocation());
            viewHolder.detail.setText(sb.toString());
            viewHolder.until.setText(format.format(l.getEnd()));
        } else {

            viewHolder.LLEvent.setVisibility(View.GONE);
            viewHolder.date.setVisibility(View.VISIBLE);
            viewHolder.date.setText(format2.format(l.getStart()));
        }
    }

    @Override
    public int getItemCount() {
        return lecture.size();
    }

    public int getPositionToday() {
        return positionToday==-1?0:positionToday;
    }
}
