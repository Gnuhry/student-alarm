package com.example.studentalarm.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.os.Binder;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.studentalarm.R;
import com.example.studentalarm.imports.LectureSchedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.example.studentalarm.ui.adapter.MonthlyAdapter.cutTime;

public class RemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final Context context;
    private List<LectureSchedule.Lecture> lectures;
    private static SimpleDateFormat dayOfWeekName;
    private static DateFormat day, time;

    public RemoteViewsFactory(Context context) {
        this.context = context;
        dayOfWeekName = new SimpleDateFormat("EEEE", context.getResources().getConfiguration().locale);
        day = DateFormat.getDateInstance(DateFormat.LONG, context.getResources().getConfiguration().locale);
        time = DateFormat.getTimeInstance(DateFormat.LONG, context.getResources().getConfiguration().locale);
    }

    @Override
    public void onCreate() {
        lectures = LectureSchedule.load(context).getAllLectureWithEachHolidayAndDayTitle(context);
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();
        lectures = LectureSchedule.load(context).getAllLectureWithEachHolidayAndDayTitle(context);
        Log.d("Data", "CHANGED");
        dayOfWeekName = new SimpleDateFormat("EEEE", context.getResources().getConfiguration().locale);
        day = DateFormat.getDateInstance(DateFormat.LONG, context.getResources().getConfiguration().locale);
        time = DateFormat.getTimeInstance(DateFormat.LONG, context.getResources().getConfiguration().locale);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        lectures.clear();
    }

    @Override
    public int getCount() {
        return lectures.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.app_widget_item);
        LectureSchedule.Lecture l = lectures.get(i);
        if (l.getId() == -1) {
            rv.setTextViewTextSize(R.id.txVTitle, TypedValue.COMPLEX_UNIT_SP, 18);
            rv.setViewVisibility(R.id.txVBarrier1, View.INVISIBLE);
            rv.setViewVisibility(R.id.LLEvent2, View.INVISIBLE);
            rv.setTextColor(R.id.txVTitle, Color.BLACK);
            rv.setTextViewText(R.id.txVTitle, String.format("-- %s %s --", dayOfWeekName.format(l.getStart()), day.format(l.getStart())));
            rv.setTextViewText(R.id.txVFrom, null);
        } else {
            rv.setTextViewTextSize(R.id.txVTitle, TypedValue.COMPLEX_UNIT_SP, 14);
            rv.setViewVisibility(R.id.txVBarrier1, View.VISIBLE);
            rv.setViewVisibility(R.id.LLEvent2, View.VISIBLE);
            rv.setTextViewText(R.id.txVTitle, l.getName());
            rv.setTextViewText(R.id.txVFrom, cutTime(time.format(l.getStart())));
            boolean aa = l.getDocent() != null, ab = l.getLocation() != null;
            rv.setTextViewText(R.id.txVDetails, aa && ab ? l.getDocent() + " - " + l.getLocation() : aa ? l.getDocent() : ab ? l.getLocation() : null);
            rv.setTextViewText(R.id.txVUntil, cutTime(time.format(l.getEnd())));
            rv.setTextColor(R.id.txVBarrier1, l.getColor());
            rv.setTextColor(R.id.txVBarrier2, l.getColor());
        }
        Log.d("Data", "set");
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
