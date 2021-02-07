package com.example.studentalarm.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.studentalarm.R;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.dialog.AlarmShutdownDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.studentalarm.ui.adapter.MonthlyAdapter.cutTime;

public class AlarmShutdownAdapter extends RecyclerView.Adapter<AlarmShutdownAdapter.ViewHolder> {
    private static final String LOG = "AlarmShutdownAdapter";
    private static SimpleDateFormat dayOfWeekName;
    private static DateFormat day, time;
    @NonNull
    private final Context context;
    private final AlarmShutdownDialog dialog;
    @NonNull
    private final List<LectureSchedule.Lecture> lecture;

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


    public AlarmShutdownAdapter(@NonNull List<LectureSchedule.Lecture> lecture_schedule, @NonNull Context context, AlarmShutdownDialog dialog) {
        this.dialog = dialog;
        this.context = context;
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            locale = context.getResources().getConfiguration().getLocales().get(0);
        else
            locale = context.getResources().getConfiguration().locale;
        dayOfWeekName = new SimpleDateFormat("EEEE", locale);
        day = DateFormat.getDateInstance(DateFormat.LONG, locale);
        time = DateFormat.getTimeInstance(DateFormat.LONG, locale);
        this.lecture = lecture_schedule;
    }

    @NonNull
    @Override
    public AlarmShutdownAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new AlarmShutdownAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_monthly, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmShutdownAdapter.ViewHolder viewHolder, final int position) {
        LectureSchedule.Lecture l = lecture.get(position);
        if (l.getId() != Integer.MIN_VALUE) {
            viewHolder.TLEvent.setVisibility(View.VISIBLE);
            viewHolder.barrier.setVisibility(View.GONE);
            viewHolder.date.setVisibility(View.GONE);
            viewHolder.title.setText(l.getName());
            if (l.getId() >= 0) {
                viewHolder.from.setVisibility(View.VISIBLE);
                viewHolder.until.setVisibility(View.VISIBLE);
                viewHolder.from.setText(cutTime(time.format(l.getStart())));
                viewHolder.until.setText(cutTime(time.format(l.getEnd())));
                if (l.getStart().equals(new Date(PreferenceManager.getDefaultSharedPreferences(context).getLong(PreferenceKeys.ALARM_SHUTDOWN, 0))))
                    viewHolder.TLEvent.setBackgroundColor(Color.parseColor("#da2c43"));
                else
                    viewHolder.TLEvent.setBackgroundColor(Color.TRANSPARENT);// slows process down but necessary because otherwise random error that background becomes Yellow
            } else {
                viewHolder.from.setVisibility(View.GONE);
                viewHolder.until.setVisibility(View.GONE);
            }
            boolean aa = l.getDocent() != null, ab = l.getLocation() != null;
            viewHolder.detail.setText(aa && ab ? l.getDocent() + " - " + l.getLocation() : aa ? l.getDocent() : ab ? l.getLocation() : null);
            viewHolder.colorLine.setBackgroundColor(l.getColor());
            viewHolder.TLEvent.setOnClickListener(view -> {
                Log.d(LOG, "Time: " + l.getStart().getTime());
                PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PreferenceKeys.ALARM_SHUTDOWN, l.getStart().getTime()).apply();
                dialog.dismiss();
            });
        } else {
            viewHolder.TLEvent.setVisibility(View.GONE);
            viewHolder.barrier.setVisibility(View.VISIBLE);
            viewHolder.date.setVisibility(View.VISIBLE);
            viewHolder.date.setText(String.format("%s %s", dayOfWeekName.format(l.getStart()), day.format(l.getStart())));
        }
    }


    @Override
    public int getItemCount() {
        return lecture.size();
    }
}
