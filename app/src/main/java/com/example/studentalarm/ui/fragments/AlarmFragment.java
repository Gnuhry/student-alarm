package com.example.studentalarm.ui.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

public class AlarmFragment extends Fragment {

    private CountDownTimer timer;
    private static final String LOG = "Alarm_Fragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity() != null)
            LectureFragment.removeLectureMenu(getActivity());
        Log.i(LOG, "open");
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        if (getContext() == null) return view;

        checkNotification();
        setTimer(view);

        return view;
    }

    /**
     * Set the timer to show when the alarm is going to trigger
     *
     * @param view view to display the timer
     */
    private void setTimer(@NonNull View view) {
        if (getContext() == null) return;
        Log.i(LOG, "Set timer");
        long time = PreferenceManager.getDefaultSharedPreferences(getContext()).getLong(PreferenceKeys.ALARM_TIME, 0);
        if (time != 0 && time > Calendar.getInstance().getTimeInMillis()) {
            TextView txVTimer = view.findViewById(R.id.txVCountdown);
            timer = new CountDownTimer(time - Calendar.getInstance().getTimeInMillis(), 1000) {
                @Override
                public void onTick(long l) {
                    Calendar ca = Calendar.getInstance();
                    ca.setTimeInMillis(l);
                    txVTimer.setText(getString(R.string.time_format, getHour(ca), ca.get(Calendar.MINUTE) + 1));
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
            ((TextView) view.findViewById(R.id.txVAlarm)).setText(getString(R.string.alarm_at, new SimpleDateFormat("HH:mm", Locale.GERMAN).format(time)));
        } else
            ((TextView) view.findViewById(R.id.textView4)).setText(R.string.no_alarm_set);
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
}