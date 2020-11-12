package com.example.studentalarm.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.studentalarm.Import.Lecture_Schedule;
import com.example.studentalarm.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.fragment.app.DialogFragment;

public class EventDialogFragment extends DialogFragment {
    private final Lecture_Schedule.Lecture data;
    private final SimpleDateFormat format=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN); //TODO add internationalization

    public EventDialogFragment(Lecture_Schedule.Lecture data) {
        this.data = data;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_dialog_fragment, container, false);
        ((TextView)view.findViewById(R.id.txVTitle)).setText(data.getName());
        ((TextView)view.findViewById(R.id.txVDocent)).setText(data.getDocent());
        ((TextView)view.findViewById(R.id.txVLocation)).setText(data.getLocation());
        ((TextView)view.findViewById(R.id.txVStartDate)).setText(format.format(data.getStart()));
        ((TextView)view.findViewById(R.id.txVEndDate)).setText(format.format(data.getEnd()));

        return view;
    }


    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }


}
