package com.example.studentalarm.ui.dialog;

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

import com.example.studentalarm.R;
import com.example.studentalarm.regular.RegularLectureSchedule;
import com.example.studentalarm.ui.fragments.RegularLectureFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class RegularLectureDialog extends DialogFragment {
    @Nullable
    private final RegularLectureSchedule data;
    private final int index;
    private boolean cancelDirect = true;
    @NonNull
    private final RegularLectureFragment fragment;
    private static final String LOG = "RegularLectureFragment";


    private EditText title, docent, location;
    private TextView add, cancel, delete;
    private Spinner spinner;
    @NonNull
    private final List<EventColor> colors;
    private final String oldTitle, oldDocent;
    private final int oldColor;

    public RegularLectureDialog(@Nullable RegularLectureSchedule data, int index, @NonNull RegularLectureFragment fragment) {
        this.fragment = fragment;
        this.data = data;
        this.index = index;
        colors = new ArrayList<>();
        colors.add(new EventColor(R.string.red, Color.RED));
        colors.add(new EventColor(R.string.green, Color.GREEN));
        colors.add(new EventColor(R.string.blue, Color.BLUE));
        colors.add(new EventColor(R.string.yellow, Color.YELLOW));
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


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "open");
        View view = inflater.inflate(R.layout.dialog_regular_lecture, container, false);
        title = view.findViewById(R.id.edTTitle);
        docent = view.findViewById(R.id.edTDocent);
        location = view.findViewById(R.id.edTLocation);
        add = view.findViewById(R.id.txVAdd);
        cancel = view.findViewById(R.id.txVCancel);
        delete = view.findViewById(R.id.txVDelete);
        spinner = view.findViewById(R.id.spColor);

        init();
        if (data != null && index >= 0)
            initData();
        return view;
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

            if (index < 0) {
                Log.i(LOG, "Create Lecture");
                data.addLecture(new RegularLectureSchedule.RegularLecture(title.getText().toString())
                        .setDocent(docent.getText().toString())
                        .setColor(((EventColor) spinner.getSelectedItem()).getColor())
                        .setAllRooms(getRooms()));
            } else {
                Log.i(LOG, "Update Lecture");
                data.getLectures().get(index)
                        .setName(title.getText().toString())
                        .setDocent(docent.getText().toString())
                        .setColor(((EventColor) spinner.getSelectedItem()).getColor())
                        .setAllRooms(getRooms());
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
     * set the date in the views
     */
    private void initData() {
        Log.i(LOG, "init data");
        add.setText(R.string.update);
        if (data == null || index < 0) return;
        RegularLectureSchedule.RegularLecture lecture = data.getLectures().get(index);
        title.setText(lecture.getName());
        docent.setText(lecture.getDocent());
        setRooms();
        spinner.setSelection(colors.indexOf(new EventColor(lecture.getColor())));
        delete.setVisibility(View.VISIBLE);
    }

    /**
     * Setting the rooms
     */
    private void setRooms() {//TODO change room
        StringBuilder sb = new StringBuilder();
        for (String s : data.getLectures().get(index).getRooms())
            sb.append(s).append(";");
        location.setText(sb.toString());
    }

    /**
     * getting all rooms
     *
     * @return room as string list
     */
    @NonNull
    private List<String> getRooms() {//TODO change room
        return Arrays.asList(location.getText().toString().split(";"));
    }

    /**
     * set cancel direct, if data is the same
     */
    private void checkCancelDirect() {
        cancelDirect = ((EventColor) spinner.getSelectedItem()).color == oldColor && docent.getText().toString().equals(oldDocent) && title.getText().toString().equals(oldTitle);
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
     * class to create a adapter with colors for spinner
     */
    public class EventColor {
        private final int name, color;

        private EventColor(int color) {
            name = 0;
            this.color = color;
        }

        private EventColor(int name, int color) {
            this.name = name;
            this.color = color;
        }

        public int getColor() {
            return color;
        }

        @NonNull
        @Override
        public String toString() {
            return getString(name);
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof EventColor)
                return color == ((EventColor) obj).color;
            return false;
        }
    }
}

