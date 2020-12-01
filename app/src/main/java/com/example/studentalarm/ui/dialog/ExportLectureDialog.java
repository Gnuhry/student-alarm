package com.example.studentalarm.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.studentalarm.R;
import com.example.studentalarm.imports.Export;
import com.example.studentalarm.imports.LectureSchedule;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class ExportLectureDialog extends Dialog {

    @NonNull
    private final Context context;
    @NonNull
    private final Activity activity;
    private static final String LOG = "ExportLectureDialog";

    public ExportLectureDialog(@NonNull Context context, @NonNull Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG, "open");
        setContentView(R.layout.dialog_delete);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        ((TextView) findViewById(R.id.txVDeleteEvent)).setText(R.string.which_event_do_you_want_to_export);
        findViewById(R.id.btnCancel).setOnClickListener(view1 -> {
            Log.i(LOG, "cancel");
            this.cancel();
        });
        ((Button) findViewById(R.id.btnDelete)).setText(R.string.export);
        findViewById(R.id.btnDelete).setOnClickListener(view -> {
            Log.i(LOG, "delete");
            boolean normal = ((CheckBox) findViewById(R.id.rdBNormalEvents)).isChecked(), import_ = ((CheckBox) findViewById(R.id.rdBImportEvents)).isChecked();
            if (normal || import_) {
                if (getContext() == null) return;
                List<LectureSchedule.Lecture> export = new ArrayList<>();
                LectureSchedule schedule = LectureSchedule.load(getContext());
                if (normal)
                    export.addAll(schedule.getLecture());
                if (import_)
                    export.addAll(schedule.getImportLecture());
                Export.exportToICS(context, activity, export);
            }
            this.cancel();
        });
    }
}
