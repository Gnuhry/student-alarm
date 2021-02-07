package com.example.studentalarm.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.CheckBox;

import com.example.studentalarm.R;
import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.regular.RegularLectureSchedule;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;

public class DeleteLectureDialog extends Dialog {

    private static final String LOG = "DeleteLectureDialog";

    public DeleteLectureDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG, "open");
        setContentView(R.layout.dialog_delete);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        findViewById(R.id.btnCancel).setOnClickListener(view1 -> {
            Log.i(LOG, "cancel");
            this.cancel();
        });
        findViewById(R.id.btnDelete).setOnClickListener(view -> {
            Log.i(LOG, "delete");
            boolean normal = ((CheckBox) findViewById(R.id.rdBNormalEvents)).isChecked(),
                    chBImport = ((CheckBox) findViewById(R.id.rdBImportEvents)).isChecked(),
                    chbHoliday = ((CheckBox) findViewById(R.id.rdBHolidayEvents)).isChecked(),
                    chBRegular = ((CheckBox) findViewById(R.id.rdBRegularEvents)).isChecked();
            if (normal || chBImport || chbHoliday || chBRegular) {
                Log.i(LOG, "Delete Dialog");
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.delete_all)
                        .setMessage(R.string.do_you_want_to_delete_this_events)
                        .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                            Log.i(LOG, "Delete Dialog - positive");
                            if (getContext() == null) return;
                            LectureSchedule schedule = LectureSchedule.load(getContext());
                            if (normal)
                                schedule.clearNormalEvents();
                            if (chBImport)
                                schedule.clearImportEvents();
                            if (chbHoliday)
                                schedule.clearHolidayEvents();
                            if (chBRegular)
                                RegularLectureSchedule.clearSave(getContext());
                            schedule.save(getContext());
                            AlarmManager.updateNextAlarm(getContext());
                            this.cancel();
                        })
                        .setNegativeButton(R.string.cancel, (dialogInterface, i) -> this.cancel())
                        .setCancelable(true)
                        .show();
            } else
                this.cancel();
        });
    }
}
