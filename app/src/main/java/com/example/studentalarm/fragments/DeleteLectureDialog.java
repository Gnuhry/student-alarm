package com.example.studentalarm.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.CheckBox;

import com.example.studentalarm.R;
import com.example.studentalarm.import_.Lecture_Schedule;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;

public class DeleteLectureDialog extends Dialog {

    public DeleteLectureDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_lecture_dialog);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        findViewById(R.id.btnCancel).setOnClickListener(view1 -> this.cancel());
        findViewById(R.id.btnDelete).setOnClickListener(view -> {
            boolean normal = ((CheckBox) findViewById(R.id.rdBNormalEvents)).isChecked(), import_ = ((CheckBox) findViewById(R.id.rdBImportEvents)).isChecked();
            if (normal || import_)
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.delete_all)
                        .setMessage(R.string.do_you_want_to_delete_this_events)
                        .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                            if (getContext() == null) return;
                            Lecture_Schedule schedule = Lecture_Schedule.Load(getContext());
                            if (normal)
                                schedule.deleteAllNotImportEvents();
                            if (import_)
                                schedule.deleteAllImportEvents();
                            schedule.Save(getContext());
                            this.cancel();
                        })
                        .setNegativeButton(R.string.cancel, (dialogInterface, i) -> this.cancel())
                        .setCancelable(true)
                        .show();
            else
                this.cancel();
        });
    }
}