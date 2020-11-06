package com.example.studentalarm;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.fragment.app.DialogFragment;

public class EventDialogFragment extends DialogFragment {
    private Lecture_Schedule.Lecture data;
    private SimpleDateFormat format=new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMAN);

    public EventDialogFragment(Lecture_Schedule.Lecture data) {
        this.data = data;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout to use as dialog or embedded fragment
        View view = inflater.inflate(R.layout.event_dialog_fragment, container, false);
        ((TextView)view.findViewById(R.id.txVTitle)).setText(data.getName());
        ((TextView)view.findViewById(R.id.txVDocent)).setText(data.getDocent());
        ((TextView)view.findViewById(R.id.txVLocation)).setText(data.getLocation());
        ((TextView)view.findViewById(R.id.txVStartDate)).setText(format.format(data.getStart()));
        ((TextView)view.findViewById(R.id.txVEndDate)).setText(format.format(data.getEnd()));

        return view;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
       // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


}
