package com.example.studentalarm.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.studentalarm.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmFragment extends Fragment {

    private final SimpleDateFormat format = new SimpleDateFormat("hh:mm", Locale.GERMAN);

    public AlarmFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        long time=PreferenceManager.getDefaultSharedPreferences(getContext()).getLong("ALARM_TIME", 0);
        if(time!=0){
        TextView txVTimer = view.findViewById(R.id.txVCountdown);
        new CountDownTimer(time- Calendar.getInstance().getTimeInMillis(), 1000) {
            @Override
            public void onTick(long l) {
                txVTimer.setText(format.format(new Date(l)));
            }

            @Override
            public void onFinish() {
                txVTimer.setText(R.string.zero_time);
            }
        }.start();
        ((TextView)view.findViewById(R.id.txVAlarm)).setText(getString(R.string.alarm_at)+format.format(time));
        }
        else{
            ((TextView)view.findViewById(R.id.textView4)).setText(R.string.no_alarm_set);
        }
        return view;
    }

    //TODO stop Timer
}