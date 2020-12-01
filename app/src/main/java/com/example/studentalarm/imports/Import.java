package com.example.studentalarm.imports;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.example.studentalarm.PreferenceKeys;
import com.example.studentalarm.R;
import com.example.studentalarm.receiver.ImportReceiver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Import {

    private static final String LOG = "Import";

    /**
     * set a daily timer for import
     *
     * @param context context of the application
     */
    public static void setTimer(@NonNull Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String[] time = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.IMPORT_TIME, PreferenceKeys.DEFAULT_IMPORT_TIME).split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, ImportReceiver.class), 0);
        Log.d(LOG, "Set timer to " + calendar.toString());

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

    }

    /**
     * stops the timer
     */
    public static void stopTimer(@NonNull Context context) {
        Log.d(LOG, "Stop timer");
        ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(context, 0, new Intent(context, ImportReceiver.class), 0));
    }

    /**
     * Create the import (synchronous)
     *
     * @param context context of the application
     */
    public static void importLecture(@NonNull Context context) {
        Log.d(LOG, "import lecture");
        LectureSchedule lecture_schedule = LectureSchedule.load(context);
        switch (PreferenceManager.getDefaultSharedPreferences(context).getInt(PreferenceKeys.MODE, 0)) {
            case ImportFunction.NONE:
                return;
            case ImportFunction.ICS:
            case ImportFunction.DHBWMA:
                String link = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKeys.LINK, null);
                if (link == null) return;
                String icsFile = runSynchronous(link);
                if (icsFile == null) return;
                ICS ics = new ICS(icsFile);
                lecture_schedule.importICS(ics).save(context);
        }
    }

    /**
     * get file synchronous from the internet
     *
     * @param link web link
     */
    @Nullable
    public static String runSynchronous(@NonNull String link) {
        Log.d(LOG, "runSynchronous: " + link);
        Request request = new Request.Builder()
                .url(link)
                .build();

        try (Response response = new OkHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful())
                Log.e("Synchronous", "Unexpected code " + response);
            ResponseBody body = response.body();
            if (body != null) {
                return body.string();
            } else
                Log.e("Synchronous", "No body");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Check for connection
     *
     * @param context  context of application
     * @return boolean if connection is active
     */
    public static boolean checkConnection(@NonNull Context context) {
        Log.d(LOG, "Check connection");
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null || !cm.getActiveNetworkInfo().isConnected()) {
            Log.d(LOG, "Missing connection");
            Toast.makeText(context, R.string.no_connection, Toast.LENGTH_SHORT).show();
            return false;
        }
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PreferenceKeys.WAIT_FOR_NETWORK,false).apply();
        return true;
    }

    /**
     * Display the different import possibilities
     */
    public static class ImportFunction {
        public static final int NONE = 0;
        public static final int ICS = 1;
        public static final int DHBWMA = 2;
        @NonNull
        public static final List<String> IMPORTS = Arrays.asList("None", "ICS", "DHBWMa");
    }

}

