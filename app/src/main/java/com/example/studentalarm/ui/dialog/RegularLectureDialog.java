package com.example.studentalarm.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.studentalarm.EventColor;
import com.example.studentalarm.R;
import com.example.studentalarm.regular.RegularLectureSchedule;
import com.example.studentalarm.ui.adapter.RoomAdapter;
import com.example.studentalarm.ui.fragments.RegularLectureFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RegularLectureDialog extends DialogFragment {
    private static final String LOG = "RegularLectureDialog";
    @Nullable
    private final RegularLectureSchedule data;
    private final int index, oldColor;
    @NonNull
    private final RegularLectureFragment fragment;
    private List<EventColor> colors;
    private final String oldTitle, oldDocent;

    private EditText title, docent;
    private RoomAdapter adapter;
    private RecyclerView recyclerView;
    private TextView add, cancel, delete;
    private Spinner spinner;
    private boolean cancelDirect = true;

    public RegularLectureDialog(@Nullable RegularLectureSchedule data, int index, @NonNull RegularLectureFragment fragment) {
        this.fragment = fragment;
        this.data = data;
        this.index = index;
        if (data != null && index >= 0) {
            RegularLectureSchedule.RegularLecture lecture = data.getLectures().get(index);
            oldTitle = lecture.getName();
            oldDocent = lecture.getDocent();
            oldColor = lecture.getColor();
        } else {
            oldTitle = "";
            oldDocent = "";
            oldColor = Color.BLUE;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        return super.onCreateDialog(savedInstanceState);
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
        spinner = view.findViewById(R.id.spColor);

        Log.d(LOG, "Context is: " + getContext());
        colors = EventColor.possibleColors(getContext());

        init();
        if (data != null && index >= 0 && index < data.getLectures().size())
            initData();
        initAdapter(data != null && index >= 0 ? data.getLectures().get(index) : new RegularLectureSchedule.RegularLecture(""));
        return view;
    }

    /**
     * Remove dialog.
     */
    @Override
    public void onDestroyView() {
        Log.i(LOG, "destroy");
        fragment.loadRecyclerView();
        super.onDestroyView();
    }

    /**
     * init all views
     */
    private void init() {
        Log.i(LOG, "Init");
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
                        .setColor(((EventColor) spinner.getSelectedItem()).getColor())
                        .setAllRooms(adapter.getAllRooms()));
            } else if (data != null) {
                Log.i(LOG, "Update Lecture");
                fragment.setChanges(true);
                data.getLectures().get(index)
                        .setName(title.getText().toString())
                        .setDocent(docent.getText().toString())
                        .setColor(((EventColor) spinner.getSelectedItem()).getColor())
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
        ArrayAdapter<EventColor> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.addAll(colors);
        spinner.setAdapter(adapter);
        spinner.setSelection(colors.indexOf(new EventColor(Color.BLUE)));

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
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkCancelDirect();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
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
        spinner.setSelection(colors.indexOf(new EventColor(lecture.getColor())));
        delete.setVisibility(View.VISIBLE);
    }

    /**
     * set cancel direct, if data is the same
     */
    private void checkCancelDirect() {
        cancelDirect = ((EventColor) spinner.getSelectedItem()).getColor() == oldColor && docent.getText().toString().equals(oldDocent) && title.getText().toString().equals(oldTitle);
    }

}

