package com.example.studentalarm.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.studentalarm.PreferenceKeys;
import com.example.studentalarm.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

public class AlarmFragment extends Fragment {

    private CountDownTimer timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        if (getContext() == null) return view;

        CheckNotification();
        SetTimer(view);

        return view;
    }

    /**
     * Set the timer to show when the alarm is going to trigger
     *
     * @param view view to display the timer
     */
    private void SetTimer(View view) {
        if (getContext() == null) return;
        long time = PreferenceManager.getDefaultSharedPreferences(getContext()).getLong(PreferenceKeys.ALARM_TIME, 0);
        if (time != 0 && time > Calendar.getInstance().getTimeInMillis()) {
            TextView txVTimer = view.findViewById(R.id.txVCountdown);
            timer = new CountDownTimer(time - Calendar.getInstance().getTimeInMillis(), 1000) {
                @Override
                public void onTick(long l) {
                    Calendar ca = Calendar.getInstance();
                    ca.setTimeInMillis(l);
                    txVTimer.setText(getString(R.string.time_format, ca.get(Calendar.HOUR_OF_DAY), ca.get(Calendar.MINUTE)));
                }

                @Override
                public void onFinish() {
                    txVTimer.setText(R.string.zero_time);
                }
            }.start();
            ((TextView) view.findViewById(R.id.txVAlarm)).setText(getString(R.string.alarm_at, new SimpleDateFormat("HH:mm", Locale.GERMAN).format(time)));
        } else {
            ((TextView) view.findViewById(R.id.textView4)).setText(R.string.no_alarm_set);
        }
    }

    /**
     * Check if needed notification are given
     * if not, pop up a dialog and ask
     */
    private void CheckNotification() {
        if (getContext() == null) return;
        if (!NotificationManagerCompat.from(getContext()).areNotificationsEnabled()) {
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
        super.onDestroyView();
        if (timer != null)
            timer.cancel();
    }
}