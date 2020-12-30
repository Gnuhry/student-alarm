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

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentalarm.R;
import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.dialog.AlarmShutdownDialog;
import com.example.studentalarm.ui.fragments.ReloadLecture;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlarmShutdownAdapter extends RecyclerView.Adapter<AlarmShutdownAdapter.ViewHolder> {
    private static final String LOG = "AlarmshutdownAd";
    private static SimpleDateFormat dayOfWeekName;
    private static DateFormat day, time;
    private static FragmentActivity activity;
    private static ReloadLecture reloadLecture;
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


    public AlarmShutdownAdapter(@NonNull LectureSchedule lecture_schedule, @NonNull Context context, FragmentActivity ac, AlarmShutdownDialog dialog) {
        this.dialog = dialog;
        this.context = context;
        activity = ac;
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            locale = context.getResources().getConfiguration().getLocales().get(0);
        else
            locale = context.getResources().getConfiguration().locale;
        dayOfWeekName = new SimpleDateFormat("EEEE", locale);
        day = DateFormat.getDateInstance(DateFormat.LONG, locale);
        time = DateFormat.getTimeInstance(DateFormat.LONG, locale);
        this.lecture = lecture_schedule.getAllLecturesFromNow(context);
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
                Log.d(LOG, "Startdate: " + l.getStart() + " smaler as: " + new Date(0) + " Bool:" + l.getStart().equals(new Date(0)));
                if (l.getStart().equals(new Date(0))) {
                    viewHolder.colorLine.setVisibility(View.INVISIBLE);
                    viewHolder.from.setVisibility(View.INVISIBLE);
                    viewHolder.until.setVisibility(View.INVISIBLE);
                }
                Log.d(LOG, "Startdatum: " + l.getStart() + " Vergleichsdatum" + new Date(PreferenceManager.getDefaultSharedPreferences(context).getLong(PreferenceKeys.ALARM_SHUTDOWN, 0)));
                if (l.getStart().equals(new Date(PreferenceManager.getDefaultSharedPreferences(context).getLong(PreferenceKeys.ALARM_SHUTDOWN, 0)))) {
                    viewHolder.TLEvent.setBackgroundColor(Color.YELLOW);
                }
            } else {
                viewHolder.from.setVisibility(View.GONE);
                viewHolder.until.setVisibility(View.GONE);
            }
            boolean aa = l.getDocent() != null, ab = l.getLocation() != null;
            viewHolder.detail.setText(aa && ab ? l.getDocent() + " - " + l.getLocation() : aa ? l.getDocent() : ab ? l.getLocation() : null);
            viewHolder.colorLine.setBackgroundColor(l.getColor());
            viewHolder.TLEvent.setOnClickListener(view -> {
                Log.d(LOG, "Time: " + l.getStart().getTime());
                if (context != null) {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PreferenceKeys.ALARM_SHUTDOWN, l.getStart().getTime()).apply();
                    AlarmManager.updateNextAlarm(context);
                    dialog.dismiss();
                }
            });
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
        return LectureSchedule.getPositionScroll() == -1 ? 0 : LectureSchedule.getPositionScroll();
    }

    /**
     * Format time to am/pm format
     *
     * @param time time to format
     * @return time format to am/pm format
     */
    @NonNull
    public static String cutTime(@NonNull String time) {
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
