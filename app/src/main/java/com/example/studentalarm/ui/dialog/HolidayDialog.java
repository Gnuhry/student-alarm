package com.example.studentalarm.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.studentalarm.Formatter;
import com.example.studentalarm.R;
import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.ui.adapter.HolidayAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class HolidayDialog extends DialogFragment {
    private static final String LOG = "HolidayDialog";
    @NonNull
    private final LectureSchedule schedule;
    private final int index;
    @Nullable
    private final LectureSchedule.Lecture old_lecture;
    @NonNull
    private final Context context;
    @NonNull
    private final HolidayAdapter adapter;
    @NonNull
    private final LectureSchedule.Lecture lecture;
    private DatePicker calendarView;
    private TextView from, until;
    private boolean create = false;

    public HolidayDialog(@NonNull LectureSchedule schedule, @NonNull Context context, @NonNull HolidayAdapter adapter, int index) {
        if (index < 0) {
            this.lecture = new LectureSchedule.Lecture(true, new Date(), new Date());
            create = true;
        } else
            this.lecture = schedule.getHolidays().get(index);
        old_lecture = lecture;
        this.context = context;
        this.adapter = adapter;
        this.schedule = schedule;
        this.index = index;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_holidays, container, false);
        Log.d(LOG, "open");
        from = view.findViewById(R.id.txVFrom);
        until = view.findViewById(R.id.txVUntil);
        calendarView = view.findViewById(R.id.cVHoliday);
        from.setBackgroundResource(R.drawable.textview_border);
        from.setOnClickListener(view1 -> {
            from.setBackgroundResource(R.drawable.textview_border);
            until.setBackground(null);
            calendarView.setMinDate(0);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lecture.getStart());
            calendarView.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> {
                lecture.setStart(getCalendarFromDatePicker(datePicker).getTime());
                setTextBox();
            });
        });
        until.setOnClickListener(view1 -> {
            until.setBackgroundResource(R.drawable.textview_border);
            from.setBackground(null);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lecture.getEnd());
            calendarView.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> {
                lecture.setEnd(getCalendarFromDatePicker(datePicker).getTime());
                setTextBox();
            });
            calendarView.setMinDate(lecture.getStart().getTime());
        });
        view.findViewById(R.id.txVSave).setOnClickListener(view1 -> {
            if (lecture.getStart().after(lecture.getEnd())) {
                until.setError(getString(R.string.end_must_start_after_begin));
                return;
            }
            if (create)
                schedule.addHoliday(lecture.setName(getString(R.string.holidays)).setAllDayEvent(true).setColor(Color.BLACK));
            else
                schedule.getHolidays().get(index).setStart(lecture.getStart()).setEnd(lecture.getEnd());
            schedule.save(context);
            this.dismiss();

        });
        view.findViewById(R.id.txVCancel).setOnClickListener(view1 -> {
            if (old_lecture != null && old_lecture.getStart().equals(lecture.getStart()) && old_lecture.getEnd().equals(lecture.getEnd()))
                dismiss();
            else {
                if (getContext() == null)
                    return;
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.dismiss)
                        .setMessage(R.string.do_you_want_to_dismiss_all_your_changes)
                        .setPositiveButton(R.string.dismiss, (dialogInterface, i) -> this.dismiss())
                        .setNegativeButton(R.string.cancel, null)
                        .setCancelable(true)
                        .show();
            }
        });

        setTextBox();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lecture.getStart());
        calendarView.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> {
            lecture.setStart(getCalendarFromDatePicker(datePicker).getTime());
            setTextBox();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(LOG, "destroy");
        adapter.reloadAdapter();
        AlarmManager.updateNextAlarm(context);
    }

    /**
     * get the selected date
     *
     * @param datePicker datePicker to get date
     * @return date as calendar object
     */
    @NotNull
    private Calendar getCalendarFromDatePicker(@NonNull DatePicker datePicker) {
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.YEAR, datePicker.getYear());
        calendar2.set(Calendar.MONTH, datePicker.getMonth());
        calendar2.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        return calendar2;
    }

    /**
     * set the date in the textBox
     */
    private void setTextBox() {
        SimpleDateFormat format = Formatter.dayFormatter();
        from.setText(getString(R.string.from_day, format.format(lecture.getStart())));
        until.setText(getString(R.string.until_day, format.format(lecture.getEnd())));
    }
}
