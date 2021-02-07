package com.example.studentalarm.ui.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.RemoteViews;

import com.example.studentalarm.R;
import com.example.studentalarm.imports.LectureSchedule;

import androidx.annotation.NonNull;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    private static Handler sWorkerQueue;

    public AppWidget() {
        HandlerThread sWorkerThread = new HandlerThread("MyWidgetProvider-worker");
        sWorkerThread.start();
        sWorkerQueue = new Handler(sWorkerThread.getLooper());
    }

    /**
     * setting the widget remote adapter
     */
    private static void updateAppWidget(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager,
                                        int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        views.setRemoteAdapter(R.id.lvWidget, new Intent(context, RemoteViewsService.class));
        sWorkerQueue.postDelayed(() -> {
            views.setScrollPosition(R.id.lvWidget, LectureSchedule.getPositionScroll());
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
        }, 5000);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager, @NonNull int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds)
            updateAppWidget(context, appWidgetManager, appWidgetId);
    }
}