package com.example.studentalarm.ui.widget;

import android.content.Context;
import android.os.Binder;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.studentalarm.R;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import static com.example.studentalarm.ui.adapter.MonthlyAdapter.cutTime;

public class RemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    @NonNull
    private final Context context;
    private static SimpleDateFormat dayOfWeekName;
    private static DateFormat day, time;
    private List<LectureSchedule.Lecture> lectures;

    public RemoteViewsFactory(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        lectures = LectureSchedule.load(context).getAllLectureWithEachHolidayAndDayTitle(context);
        reload();
    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();
        lectures = LectureSchedule.load(context).getAllLectureWithEachHolidayAndDayTitle(context);
        reload();
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

    @NonNull
    @Override
    public RemoteViews getViewAt(int i) {
        reload();
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.app_widget_item);
        LectureSchedule.Lecture l = lectures.get(i);
        if (l.getId() == -1) {
            rv.setTextViewTextSize(R.id.txVTitle, TypedValue.COMPLEX_UNIT_SP, 18);
            rv.setViewVisibility(R.id.txVBarrier1, View.INVISIBLE);
            rv.setViewVisibility(R.id.LLEvent2, View.INVISIBLE);
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
        return rv;
    }

    @Nullable
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

    /**
     * loading the data
     */
    private void reload() {
        Locale locale = new Locale(PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.LANGUAGE, PreferenceKeys.defaultLanguage(context)).toLowerCase());
        dayOfWeekName = new SimpleDateFormat("EEEE", locale);
        day = DateFormat.getDateInstance(DateFormat.LONG, locale);
        time = DateFormat.getTimeInstance(DateFormat.LONG, locale);
    }
}
