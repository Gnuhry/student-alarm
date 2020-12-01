package com.example.studentalarm.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;

import com.example.studentalarm.R;
import com.example.studentalarm.ui.adapter.ExportAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExportDialog extends Dialog {

    @NonNull
    private final Context context;
    @NonNull
    private final Activity activity;
    private RecyclerView recyclerView;
    private boolean pause = false;
    private static final String LOG = "ExportDialog";

    public ExportDialog(@NonNull Context context, @NonNull Activity activity) {
        super(context);
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG, "open");
        setContentView(R.layout.dialog_export);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        recyclerView = findViewById(R.id.rVExport);
        ExportAdapter adapter = new ExportAdapter(context, activity, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        findViewById(R.id.imVAdd).setOnClickListener(view -> {
            Log.i(LOG, "add");
            new ExportLectureDialog(context, activity).show();
            this.cancel();
        });
        if (adapter.getItemCount() > 0) {
            findViewById(R.id.chBAll).setVisibility(View.VISIBLE);
            ((CheckBox) findViewById(R.id.chBAll)).setOnCheckedChangeListener((compoundButton, b) -> {
                if (!pause)
                    adapter.setCheckedAll(b);
            });
        }
    }

    /**
     * sett the all checked checkBox
     *
     * @param checked checked value
     */
    public void setAllChecked(boolean checked) {
        pause = true;
        ((CheckBox) findViewById(R.id.chBAll)).setChecked(checked);
        pause = false;
    }

    /**
     * Set the delete button visible or gone
     *
     * @param b visibility of delete button
     */
    public void setDelete(boolean b) {
        if (b) {
            findViewById(R.id.imVAdd).setVisibility(View.GONE);
            findViewById(R.id.imVDelete).setVisibility(View.VISIBLE);
            findViewById(R.id.imVDelete).setOnClickListener(view -> {
                Log.i(LOG, "delete");
                ExportAdapter adapter = ((ExportAdapter) recyclerView.getAdapter());
                if (adapter != null)
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle(R.string.delete)
                            .setMessage(R.string.do_you_want_to_delete_this_exports)
                            .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                                Log.i(LOG, "delete positive");
                                adapter.deleteFiles();
                            })
                            .setNegativeButton(R.string.cancel, (dialogInterface, i) -> this.cancel())
                            .setCancelable(true)
                            .show();
                this.cancel();
            });
        } else {
            findViewById(R.id.imVAdd).setVisibility(View.VISIBLE);
            findViewById(R.id.imVDelete).setVisibility(View.GONE);
        }
    }


}
