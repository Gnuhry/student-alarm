package com.example.studentalarm.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentalarm.R;
import com.example.studentalarm.ui.adapter.ExportAdapter;

public class ImportColorDialog extends Dialog {
    @NonNull
    private final Context context;
    private static final String LOG = "ImportColorDialog";
    private boolean pause = false;



    public ImportColorDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG, "open");
        setContentView(R.layout.dialog_import_color);

        findViewById(R.id.btnCancel).setOnClickListener(view -> {
            Log.i(LOG, "add");
            this.cancel();
        });
    }
}
