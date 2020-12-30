package com.example.studentalarm.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.example.studentalarm.R;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.dialog.AlarmShutdownDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AlarmFragment extends Fragment {

    private static final String LOG = "Alarm_Fragment";
    private static CountDownTimer timer;
    private static View view;
    private static Context context;
    private static FragmentManager fragmanager;
    private static ProgressDialog progress;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(PreferenceManager.getDefaultSharedPreferences(context).getLong(PreferenceKeys.ALARM_TIME, 0) <= Calendar.getInstance().getTime().getTime())
            PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PreferenceKeys.ALARM_SHUTDOWN,0).apply();
        if (getActivity() != null) {
            RegularLectureFragment.removeRegularLectureMenu(getActivity());
            LectureFragment.removeLectureMenu(getActivity());
        }
        Log.i(LOG, "open");
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        if (getContext() == null) return view;

        checkNotification();
        this.view = view;
        context=getContext();
        fragmanager=getActivity().getSupportFragmentManager();
        setTimer(view);
        showAlarmshutdown(view);

        return view;
    }

    /**
     * If change fragment, the countdown can stop
     */
    @Override
    public void onDestroyView() {
        Log.i(LOG, "Destroyed");
        super.onDestroyView();
        if (timer != null)
            timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(PreferenceManager.getDefaultSharedPreferences(context).getLong(PreferenceKeys.ALARM_TIME, 0) <= Calendar.getInstance().getTime().getTime())
            PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(PreferenceKeys.ALARM_SHUTDOWN,0).apply();
        showAlarmshutdown(view);
    }

    /**
     * relode after temporary alarmcancelation
     */
    public static void reloade(){
        Log.d(LOG, "reloade");
        if (timer != null)
            timer.cancel();
        setTimer(view);
        showAlarmshutdown(view);
    }

    public static void stopload(){
        progress.dismiss();
    }

    /**
     * If preferences not initialised => doesnt show
     *
     * @param view needs View
     */
    private static void showAlarmshutdown(@NonNull View view) {
        Log.d(LOG,"GetContext: "+context);
        if (context == null) return;
        Log.i(LOG, "check / show Button");
        Log.d(LOG, "ALARM_ON: " + PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.ALARM_ON, false));
        Log.d(LOG, "ALARM_PHONE: " + PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.ALARM_PHONE, true));

        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.ALARM_ON, false)) {
            if (!PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.ALARM_PHONE, true)) {
                Log.d(LOG, "Button VISIBLE");
                view.findViewById(R.id.btntmpalarmshutdown).setVisibility(View.VISIBLE);
                view.findViewById(R.id.btntmpalarmshutdown).setOnClickListener(view1 -> {
                    Log.i(LOG, "Button pressed");
                    progress = new ProgressDialog(context);
                    progress.setTitle(context.getString(R.string.loading));
                    progress.setMessage(context.getString(R.string.wait_while_loading));
                    progress.setCancelable(false);
                    progress.show();
                    new AlarmShutdownDialog().show(fragmanager, "dialog");
                });
                if (PreferenceManager.getDefaultSharedPreferences(context).getLong(PreferenceKeys.ALARM_SHUTDOWN, 0) != 0) {
                    Log.d(LOG, "Text VISIBLE");
                    Date date = new Date(PreferenceManager.getDefaultSharedPreferences(context).getLong(PreferenceKeys.ALARM_SHUTDOWN, 0));
                    ((TextView) view.findViewById(R.id.txtalarmshutdownuntil)).setText(date.toString());
                    view.findViewById(R.id.txtalarmshutdownuntil).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.txtalarmshutdownuntil).setVisibility(View.GONE);
                }
            } else {
                if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PreferenceKeys.ALARM_PHONE, true)) {
                    Log.d(LOG, "Alarm on phone message");
                    ((TextView) view.findViewById(R.id.textView4)).setText(R.string.alarm_in_phone);
                }
            }
        } else {
            Log.d(LOG, "no alarm at all");
            ((TextView) view.findViewById(R.id.textView4)).setText(R.string.no_alarms);
        }

    }

    /**
     * Set the timer to show when the alarm is going to trigger
     *
     * @param view view to display the timer
     */
    private static void setTimer(@NonNull View view) {
        if (context == null) return;
        Log.i(LOG, "Set timer");
        long time = PreferenceManager.getDefaultSharedPreferences(context).getLong(PreferenceKeys.ALARM_TIME, 0);
        if (time != 0 && time > Calendar.getInstance().getTimeInMillis()) {
            TextView txVTimer = view.findViewById(R.id.txVCountdown);
            timer = new CountDownTimer(time - Calendar.getInstance().getTimeInMillis(), 1000) {
                @Override
                public void onTick(long l) {
                    Calendar ca = Calendar.getInstance();
                    ca.setTimeInMillis(l);
                    txVTimer.setText(context.getString(R.string.time_format, getHour(ca), ca.get(Calendar.MINUTE) + 1));
                }

                /**
                 * get hour inclusive day time
                 * @param ca ca to get the hours
                 * @return hour
                 */
                private int getHour(@NonNull Calendar ca) {
                    int erg = ca.get(Calendar.HOUR_OF_DAY);
                    erg += ca.get(Calendar.DAY_OF_MONTH) * 24 - 24;
                    return erg;
                }

                @Override
                public void onFinish() {
                    txVTimer.setText(R.string.zero_time);
                }
            }.start();
            ((TextView) view.findViewById(R.id.txVAlarm)).setText(context.getString(R.string.alarm_at, new SimpleDateFormat("HH:mm", Locale.GERMAN).format(time)));
        } else {
            ((TextView) view.findViewById(R.id.textView4)).setText(R.string.no_alarm_set);
        }
    }

    /**
     * Check if needed notification are given
     * if not, pop up a dialog and ask
     */
    private void checkNotification() {
        if (getContext() != null && !NotificationManagerCompat.from(getContext()).areNotificationsEnabled()) {
            Log.i(LOG, "Missing notification permission");
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.notification_permission_missing)
                    .setMessage(R.string.notification_permission_are_missing_without_them_the_alarm_will_not_work_properly)
                    .setPositiveButton(R.string.ok, null)
                    .setCancelable(true)
                    .show();
        }
    }
}