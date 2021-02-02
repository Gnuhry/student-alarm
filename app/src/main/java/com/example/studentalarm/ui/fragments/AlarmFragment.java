package com.example.studentalarm.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.studentalarm.Formatter;
import com.example.studentalarm.R;
import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.receiver.AlarmOffReceiver;
import com.example.studentalarm.receiver.SnoozeReceiver;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.dialog.AlarmShutdownDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

public class AlarmFragment extends Fragment {

    private static final String LOG = "Alarm_Fragment";
    private CountDownTimer timer;
    private View view;
    @Nullable
    private ProgressDialog progress;
    private List<LectureSchedule.Lecture> lectureSchedule;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity() != null) {
            RegularLectureFragment.removeRegularLectureMenu(getActivity());
            LectureFragment.removeLectureMenu(getActivity());
        }
        Log.i(LOG, "open");
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        if (getContext() == null) return view;
        setAlarmViews(view);
        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getLong(PreferenceKeys.ALARM_SHUTDOWN, 0) <= Calendar.getInstance().getTime().getTime())
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putLong(PreferenceKeys.ALARM_SHUTDOWN, 0).apply();

        view.findViewById(R.id.btnSnooze).setOnClickListener(view1 -> {
            new SnoozeReceiver().onReceive(getContext(), null);
            reload();
        });
        view.findViewById(R.id.btnAlarmOff).setOnClickListener(view1 -> {
            new AlarmOffReceiver().onReceive(getContext(), null);
            reload();
        });
        view.findViewById(R.id.btnStopSnooze).setOnClickListener(view1 -> {
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putInt(PreferenceKeys.ALARM_MODE, 0).apply();
            AlarmManager.updateNextAlarm(getContext());
            reload();
        });

        checkNotification();
        this.view = view;
        setTimer(view);
        showAlarmShutdown(view);

        return view;
    }

    public void setAlarmViews(View view) {
        if (getContext() == null) return;
        int alarmId = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(PreferenceKeys.ALARM_MODE, -1);
        view.findViewById(R.id.llNormal).setVisibility(alarmId == 1 ? View.GONE : View.VISIBLE);
        view.findViewById(R.id.llAlarm).setVisibility(alarmId == 1 ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.btnStopSnooze).setVisibility(alarmId == 2 ? View.VISIBLE : View.GONE);
    }

    /**
     * !
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
        if (getContext() != null && PreferenceManager.getDefaultSharedPreferences(getContext()).getLong(PreferenceKeys.ALARM_TIME, 0) <= Calendar.getInstance().getTime().getTime())
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putLong(PreferenceKeys.ALARM_SHUTDOWN, 0).apply();
        reload();
    }

    /**
     * reload after temporary alarm cancel action
     */
    public void reload() {
        Log.d(LOG, "reload");
        if (timer != null)
            timer.cancel();
        setTimer(view);
        showAlarmShutdown(view);
        setAlarmViews(view);
    }

    public void stopLoad() {
        if (progress != null)
            progress.dismiss();
    }

    /**
     * If preferences not initialised => doesn't show
     *
     * @param view needs View
     */
    private void showAlarmShutdown(@NonNull View view) {
        Log.d(LOG, "GetContext: " + getContext());
        if (getContext() == null) return;
        Log.i(LOG, "check / show Button");
        Log.d(LOG, "ALARM_ON: " + PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(PreferenceKeys.ALARM_ON, false));
        Log.d(LOG, "ALARM_PHONE: " + PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(PreferenceKeys.ALARM_PHONE, true));

        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(PreferenceKeys.ALARM_ON, false)) {
            if (!PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(PreferenceKeys.ALARM_PHONE, true)) {
                Log.d(LOG, "Button VISIBLE");
                progress = new ProgressDialog(getContext());
                progress.setTitle(getContext().getString(R.string.loading));
                progress.setMessage(getContext().getString(R.string.wait_while_loading));
                progress.setCancelable(false);
                progress.show();
                lectureSchedule = LectureSchedule.load(getContext()).getAllLecturesFromNowWithoutHoliday(getContext());
                if (lectureSchedule.size() > 0) {
                    view.findViewById(R.id.btnTmpAlarmShutdown).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.btnTmpAlarmShutdown).setOnClickListener(view1 -> {
                        if (getActivity() == null) return;
                        Log.i(LOG, "Button pressed");
                        progress.show(); // in AlarmShutdownDialog dismissed
                        new AlarmShutdownDialog(this, lectureSchedule).show(getActivity().getSupportFragmentManager(), "dialog");
                    });
                }
                progress.dismiss();
                if (getContext() != null && PreferenceManager.getDefaultSharedPreferences(getContext()).getLong(PreferenceKeys.ALARM_SHUTDOWN, 0) != 0) {
                    Log.d(LOG, "Text VISIBLE");
                    ((TextView) view.findViewById(R.id.txtAlarmShutdownUntil)).setText(new Date(PreferenceManager.getDefaultSharedPreferences(getContext()).getLong(PreferenceKeys.ALARM_SHUTDOWN, 0)).toString());
                    view.findViewById(R.id.txtAlarmShutdownUntil).setVisibility(View.VISIBLE);
                } else
                    view.findViewById(R.id.txtAlarmShutdownUntil).setVisibility(View.GONE);
            } else if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(PreferenceKeys.ALARM_PHONE, true)) {
                Log.d(LOG, "Alarm on phone message");
                ((TextView) view.findViewById(R.id.textView4)).setText(R.string.alarm_in_phone);
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
                    ca.setTimeInMillis(l - TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET));
                    if (getContext() != null)
                        txVTimer.setText(getContext().getString(R.string.time_format, getHour(ca), ca.get(Calendar.MINUTE) + 1));
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
            ((TextView) view.findViewById(R.id.txVAlarm)).setText(getContext().getString(R.string.alarm_at, Formatter.timeFormatter().format(time)));
            ((TextView) view.findViewById(R.id.textView4)).setText(R.string.next_alarm_in);
        } else {
            ((TextView) view.findViewById(R.id.txVAlarm)).setText(null);
            ((TextView) view.findViewById(R.id.txVCountdown)).setText(null);
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
