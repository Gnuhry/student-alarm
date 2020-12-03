package com.example.studentalarm.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.studentalarm.R;
import com.example.studentalarm.RegularLectureSchedule;

import java.util.List;

import androidx.annotation.NonNull;

public class ChangeRoomDialog extends Dialog {

    @NonNull
    private final Context context;
    private final RegularLectureSchedule.RegularLecture lecture;
    private static final String LOG = "ChangeRoomDialog";

    public ChangeRoomDialog(@NonNull Context context, RegularLectureSchedule.RegularLecture lecture) {
        super(context);
        this.context = context;
        this.lecture = lecture;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_change_room);
        Log.i(LOG, "open");

        RadioGroup radioGroup = findViewById(R.id.rGRoom);
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        List<String> rooms = lecture.getRooms();
        for (int i = 0; i < rooms.size(); i++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(rooms.get(i));
            radioButton.setId(i);
            radioGroup.addView(radioButton, params);
        }
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            lecture.setActiveRoomId(i);
            this.dismiss();
        });
    }
}
