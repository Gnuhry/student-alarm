package com.example.studentalarm.ui.dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.studentalarm.EventColor;
import com.example.studentalarm.R;
import com.example.studentalarm.regular.RegularLectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.adapter.RoomAdapter;
import com.example.studentalarm.ui.fragments.RegularLectureFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RegularLectureDialog extends DialogFragment implements CallColorDialog {
    private static final String LOG = "RegularLectureDialog";
    @Nullable
    private final RegularLectureSchedule data;
    private final int index, oldColor;
    @NonNull
    private final RegularLectureFragment fragment;
    private final String oldTitle, oldDocent;

    private LinearLayout llColor;
    private EditText title, docent;
    private RoomAdapter adapter;
    private RecyclerView recyclerView;
    private TextView add, cancel, delete, color;
    private boolean cancelDirect = true;
    private int colorHelp;

    public RegularLectureDialog(@Nullable RegularLectureSchedule data, int index, @NonNull RegularLectureFragment fragment) {
        this.fragment = fragment;
        this.data = data;
        this.index = index;
        if (data != null && index >= 0) {
            RegularLectureSchedule.RegularLecture lecture = data.getLectures().get(index);
            oldTitle = lecture.getName();
            oldDocent = lecture.getDocent();
            colorHelp = oldColor = lecture.getColor();
        } else {
            oldTitle = "";
            oldDocent = "";
            colorHelp = oldColor = PreferenceKeys.DEFAULT_REGULAR_EVENT_COLOR;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "open");
        View view = inflater.inflate(R.layout.dialog_regular_lecture, container, false);
        if (getContext() == null) return view;
        title = view.findViewById(R.id.edTTitleReg);
        docent = view.findViewById(R.id.edTDocentReg);
        recyclerView = view.findViewById(R.id.rVRoom);
        add = view.findViewById(R.id.txVAdd);
        cancel = view.findViewById(R.id.txVCancel);
        delete = view.findViewById(R.id.txVDelete);
        color = view.findViewById(R.id.txVColor);
        llColor = view.findViewById(R.id.llColor);

        Log.d(LOG, "Context is: " + getContext());

        init();
        if (data != null && index >= 0 && index < data.getLectures().size())
            initData();
        initAdapter(data != null && index >= 0 ? data.getLectures().get(index) : new RegularLectureSchedule.RegularLecture(""));
        return view;
    }

    @Override
    public void onDestroyView() {
        Log.i(LOG, "destroy");
        fragment.loadRecyclerView();
        super.onDestroyView();
    }

    @Override
    public void setColorHelp(int colorHelp) {
        this.colorHelp = colorHelp;
        checkCancelDirect();
        setColor();
    }

    /**
     * init all views
     */
    private void init() {
        Log.i(LOG, "Init");
        setColor();
        add.setOnClickListener(view -> {
            if (getContext() == null) return;

            if (title.getText().toString().isEmpty()) {
                Log.i(LOG, "title missing");
                title.setError(getString(R.string.missing));
                return;
            }

            if (index < 0 && data != null) {
                Log.i(LOG, "Create Lecture");
                fragment.setChanges(true);
                data.addLecture(new RegularLectureSchedule.RegularLecture(title.getText().toString())
                        .setDocent(docent.getText().toString())
                        .setColor(colorHelp)
                        .setAllRooms(adapter.getAllRooms()));
            } else if (data != null) {
                Log.i(LOG, "Update Lecture");
                fragment.setChanges(true);
                data.getLectures().get(index)
                        .setName(title.getText().toString())
                        .setDocent(docent.getText().toString())
                        .setColor(colorHelp)
                        .setAllRooms(adapter.getAllRooms());
            }
            this.dismiss();
        });
        delete.setOnClickListener(view -> {
            Log.i(LOG, "delete");
            if (getContext() == null) return;
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.delete)
                    .setMessage(R.string.do_you_want_to_delete_this_lecture)
                    .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                        if (data == null) return;
                        Log.i(LOG, "delete Lecture");
                        data.getLectures().remove(index);
                        fragment.setChanges(true);
                        this.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> this.dismiss())
                    .setCancelable(true)
                    .show();

        });
        cancel.setOnClickListener(view -> {
            Log.i(LOG, "cancel");
            if (cancelDirect)
                this.dismiss();
            else if (getContext() != null)
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(R.string.dismiss)
                        .setMessage(R.string.do_you_want_to_dismiss_all_your_changes)
                        .setPositiveButton(R.string.dismiss, (dialogInterface, i) -> this.dismiss())
                        .setNegativeButton(R.string.no, null)
                        .setCancelable(true)
                        .show();
        });

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkCancelDirect();
            }
        });
        docent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkCancelDirect();
            }
        });
        llColor.setOnClickListener(view -> {
            if (getActivity() != null & data != null)
                new ColorDialog(colorHelp, this).show(getActivity().getSupportFragmentManager(), "dialog");
        });
    }

    /**
     * init adapter
     *
     * @param lecture lecture with the data to init
     */
    private void initAdapter(RegularLectureSchedule.RegularLecture lecture) {
        adapter = new RoomAdapter(lecture);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    /**
     * set the date in the views
     */
    private void initData() {
        Log.i(LOG, "init data");
        add.setText(R.string.update);
        if (data == null || index < 0) return;
        RegularLectureSchedule.RegularLecture lecture = data.getLectures().get(index);
        title.setText(lecture.getName());
        docent.setText(lecture.getDocent());
        delete.setVisibility(View.VISIBLE);
    }

    /**
     * set cancel direct, if data is the same
     */
    private void checkCancelDirect() {
        cancelDirect = colorHelp == oldColor && docent.getText().toString().equals(oldDocent) && title.getText().toString().equals(oldTitle);
    }


    /**
     * sets color views
     */
    private void setColor() {
        if (getContext() == null) return;
        List<EventColor> colors = EventColor.possibleColors(getContext());
        int index = colors.indexOf(new EventColor(colorHelp));
        if (index == -1)
            color.setText(getString(R.string.custom));
        else color.setText(colors.get(index).getName());
    }
}

