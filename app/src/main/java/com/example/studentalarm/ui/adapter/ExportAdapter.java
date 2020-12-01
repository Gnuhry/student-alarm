package com.example.studentalarm.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.studentalarm.R;
import com.example.studentalarm.ui.dialog.ExportDialog;
import com.example.studentalarm.imports.Export;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ExportAdapter extends RecyclerView.Adapter<ExportAdapter.ViewHolder> {

    @Nullable
    private final File[] files;
    private Context context;
    private Activity activity;
    private ExportDialog dialog;
    private List<ViewHolder> list;
    private boolean[] booleans;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox file;
        private final ImageView share;
        @Nullable
        private File holderFile;

        public ViewHolder(@NonNull View view) {
            super(view);
            file = view.findViewById(R.id.chBFile);
            share = view.findViewById(R.id.imVShare);
        }
    }

    public ExportAdapter(@NonNull Context context, Activity activity, ExportDialog dialog) {
        File folder = new File(context.getFilesDir(), "share");
        files = folder.listFiles();
        if (files == null) return;
        this.context = context;
        this.activity = activity;
        this.dialog = dialog;
        list = new ArrayList<>();
        booleans = new boolean[files.length];
    }

    /**
     * delete all selected files
     */
    public void deleteFiles() {
        for (ViewHolder holder : list)
            if (holder.file.isChecked() && holder.holderFile != null && !holder.holderFile.delete())
                holder.holderFile.deleteOnExit();
    }

    /**
     * checked all checkBox
     *
     * @param checkedAll checked value
     */
    public void setCheckedAll(boolean checkedAll) {
        for (ViewHolder holder : list)
            holder.file.setChecked(checkedAll);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_export, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (files != null && files[position] != null) {
            list.add(holder);
            holder.file.setText(files[position].getName());
            holder.share.setOnClickListener(view -> Export.share(context, files[position], activity));
            holder.holderFile = files[position];
            holder.file.setOnCheckedChangeListener((compoundButton, b) -> {
                booleans[position] = b;
                int tr = 0;
                for (boolean boo : booleans)
                    if (boo) tr++;
                dialog.setAllChecked(tr == files.length);
                dialog.setDelete(tr > 0);
            });
        }
    }

    @Override
    public int getItemCount() {
        return files == null ? -1 : files.length;
    }


}
