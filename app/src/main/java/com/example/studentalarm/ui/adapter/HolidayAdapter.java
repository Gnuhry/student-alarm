package com.example.studentalarm.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.studentalarm.R;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.ui.dialog.HolidayDialog;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class HolidayAdapter extends RecyclerView.Adapter<HolidayAdapter.ViewHolder> {
    private LectureSchedule schedule;
    @NonNull
    private final Context context;
    @NonNull
    private final FragmentActivity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView holiday;
        private final LinearLayout llHolidays;
        private final ImageView add, edit;

        public ViewHolder(@NonNull View view) {
            super(view);
            add = view.findViewById(R.id.imVAdd);
            holiday = view.findViewById(R.id.txVHoliday);
            llHolidays = view.findViewById(R.id.llHoliday);
            edit = view.findViewById(R.id.imVEdit);
        }
    }

    public HolidayAdapter(@NonNull Context context, @NonNull FragmentActivity activity) {
        this.schedule = LectureSchedule.load(context);
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_holiday, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (schedule.getHolidays().size() == position) {
            holder.llHolidays.setVisibility(View.GONE);
            holder.add.setVisibility(View.VISIBLE);
            holder.add.setOnClickListener(view -> new HolidayDialog(schedule, context, this, -1).show(activity.getSupportFragmentManager(), "dialog"));
        } else {
            holder.add.setVisibility(View.GONE);
            holder.llHolidays.setVisibility(View.VISIBLE);
            holder.llHolidays.setTag(position);
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            holder.holiday.setText(String.format("%s - %s", format.format(schedule.getHolidays().get(position).getStart()), format.format(schedule.getHolidays().get(position).getEnd())));
            holder.edit.setOnClickListener(view -> new HolidayDialog(schedule, context, this, ((int) holder.llHolidays.getTag())).show(activity.getSupportFragmentManager(), "dialog"));
        }
    }

    /**
     * reloading the adapter to display new data
     */
    public void reloadAdapter() {
        this.schedule = LectureSchedule.load(context);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return schedule.getHolidays().size() + 1;
    }


}
