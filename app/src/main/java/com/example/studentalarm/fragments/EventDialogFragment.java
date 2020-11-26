package com.example.studentalarm.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.studentalarm.R;
import com.example.studentalarm.import_.Lecture_Schedule;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

public class EventDialogFragment extends DialogFragment {
    private final Lecture_Schedule.Lecture data;
    private final Lecture_Schedule schedule;
    private boolean create = false, cancel_direct = true;
    private static boolean working;
    private final ReloadLecture lecture;


    private EditText title, docent, location, begin, end;
    private LinearLayout LBegin, LEnd;
    private ConstraintLayout CBegin, CEnd;
    private DatePicker dPBegin, dPEnd;
    private TextView txVBegin, txVEnd, add, cancel, delete;
    private Spinner spinner;
    @NonNull
    private final List<EventColor> colors;

    public EventDialogFragment(Lecture_Schedule.Lecture data, Lecture_Schedule schedule, ReloadLecture lecture) {
        this.lecture = lecture;
        this.data = data;
        this.schedule = schedule;
        colors = new ArrayList<>();
        colors.add(new EventColor(R.string.red, Color.RED));
        colors.add(new EventColor(R.string.green, Color.GREEN));
        colors.add(new EventColor(R.string.blue, Color.BLUE));
        colors.add(new EventColor(R.string.yellow, Color.YELLOW));
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_dialog_fragment, container, false);
        title = view.findViewById(R.id.edTTitle);
        docent = view.findViewById(R.id.edTDocent);
        location = view.findViewById(R.id.edTLocation);
        begin = view.findViewById(R.id.edTTimeBegin);
        end = view.findViewById(R.id.edTTimeEnd);
        LBegin = view.findViewById(R.id.LLTimeBegin);
        LEnd = view.findViewById(R.id.LLEnd);
        CBegin = view.findViewById(R.id.CLBegin);
        CEnd = view.findViewById(R.id.CLEnd);
        dPBegin = view.findViewById(R.id.dPDateBegin);
        dPEnd = view.findViewById(R.id.dPDateEnd);
        txVBegin = view.findViewById(R.id.txVBegin);
        txVEnd = view.findViewById(R.id.txVEnd);
        add = view.findViewById(R.id.txVAdd);
        cancel = view.findViewById(R.id.txVCancel);
        delete = view.findViewById(R.id.txVDelete);
        spinner = view.findViewById(R.id.spColor);

        InitSpinner();
        if (data != null) {
            InitData();
            if (!data.isImport()) {
                cancel_direct = false;
                InitListener();
                InitDataChangeable();
            } else
                DisableView();
        } else {
            create = true;
            InitListener();
            cancel_direct = false;
        }
        InitCancel();
        return view;
    }

    /**
     * init the cancel button
     */
    private void InitCancel() {
        cancel.setOnClickListener(view -> {
            if (cancel_direct)
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
    }

    /**
     * init the color spinner
     */
    private void InitSpinner() {
        ArrayAdapter<EventColor> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.addAll(colors);
        spinner.setAdapter(adapter);
        spinner.setSelection(colors.indexOf(new EventColor(Color.BLUE)));
    }

    /**
     * disable all views, if lecture is import
     */
    private void DisableView() {
        title.setEnabled(false);
        docent.setEnabled(false);
        location.setEnabled(false);
        add.setVisibility(View.INVISIBLE);
        spinner.setEnabled(false);
    }

    /**
     * init all views to make changes possible
     */
    private void InitListener() {
        Calendar calendar = Calendar.getInstance();
        dPBegin.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> {
            SetDateTime(txVBegin, dPBegin, begin);
            calendar.set(dPBegin.getYear(), dPBegin.getMonth(), dPBegin.getDayOfMonth(), 0, 0, 0);
            dPEnd.setMinDate(calendar.getTimeInMillis());
        });
        dPEnd.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> SetDateTime(txVEnd, dPEnd, end));
        InitTimeEditText(begin, dPBegin, txVBegin);
        InitTimeEditText(end, dPEnd, txVEnd);
        add.setOnClickListener(view -> {
            if (getContext() == null) return;
            boolean error = false;

            if (begin.getText().length() <= 3) {
                begin.setError(getString(R.string.missing));
                txVBegin.setError(getString(R.string.missing));
                error = true;
            } else {
                begin.setError(null);
                txVBegin.setError(null);
            }

            if (end.getText().length() <= 3) {
                end.setError(getString(R.string.missing));
                txVEnd.setError(getString(R.string.missing));
                error = true;
            } else {
                end.setError(null);
                txVEnd.setError(null);
            }

            if (title.getText().toString().isEmpty()) {
                title.setError(getString(R.string.missing));
                error = true;
            } else
                title.setError(null);

            boolean error2 = false;
            if (txVBegin.getTag() == null) {
                begin.setError(getString(R.string.missing));
                txVBegin.setError(getString(R.string.missing));
                error2 = true;
            } else {
                begin.setError(null);
                txVBegin.setError(null);
            }

            if (txVEnd.getTag() == null) {
                end.setError(getString(R.string.missing));
                txVEnd.setError(getString(R.string.missing));
                error2 = true;
            } else {
                end.setError(null);
                txVEnd.setError(null);
            }

            if (error2) return; //Catch null Pointer Error

            Date dBegin = convertDate(txVBegin.getText().toString(), (Integer) txVBegin.getTag()),
                    dEnd = convertDate(txVEnd.getText().toString(), (Integer) txVEnd.getTag());
            if (dBegin == null) {
                begin.setError(getString(R.string.wrong));
                txVBegin.setError(getString(R.string.wrong));
                error = true;
            } else {
                begin.setError(null);
                txVBegin.setError(null);
            }

            if (dEnd == null) {
                end.setError(getString(R.string.wrong));
                txVEnd.setError(getString(R.string.wrong));
                error = true;
            } else {
                end.setError(null);
                txVEnd.setError(null);
            }
            if (error) return;

            if (dBegin.after(dEnd)) {
                end.setError(getString(R.string.end_must_start_after_begin));
                txVEnd.setError(getString(R.string.end_must_start_after_begin));
                return;
            }

            if (create) {
                schedule.addLecture(new Lecture_Schedule.Lecture(false).setName(title.getText().toString())
                        .setDocent(docent.getText().toString())
                        .setLocation(location.getText().toString())
                        .setStart(new Date(dBegin.getTime() - TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET)))
                        .setEnd(new Date(dEnd.getTime() - TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET)))
                        .setColor(((EventColor) spinner.getSelectedItem()).getColor()));
            } else {
                List<Lecture_Schedule.Lecture> help = schedule.getAllLecture();
                help.get(help.indexOf(this.data))
                        .setName(title.getText().toString())
                        .setDocent(docent.getText().toString())
                        .setLocation(location.getText().toString())
                        .setStart(new Date(dBegin.getTime() - TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET)))
                        .setEnd(new Date(dEnd.getTime() - TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET)))
                        .setColor(((EventColor) spinner.getSelectedItem()).getColor());
            }
            schedule.Save(getContext());
            this.dismiss();
        });
        delete.setOnClickListener(view -> {
            if (getContext() == null) return;
            schedule.removeLecture(this.data);
            schedule.Save(getContext());
            this.dismiss();
        });
        LBegin.setOnClickListener(view -> {
            if (CBegin.getVisibility() == View.VISIBLE) {
                CBegin.setVisibility(View.GONE);
            } else {
                CBegin.setVisibility(View.VISIBLE);
                CEnd.setVisibility(View.GONE);
            }
        });
        LEnd.setOnClickListener(view -> {
            if (CEnd.getVisibility() == View.VISIBLE) {
                CEnd.setVisibility(View.GONE);
            } else {
                CEnd.setVisibility(View.VISIBLE);
                CBegin.setVisibility(View.GONE);
            }
        });
    }

    /**
     * set the date in the views
     */
    private void InitData() {
        add.setText(R.string.update);
        title.setText(data.getName());
        docent.setText(data.getDocent());
        location.setText(data.getLocation());
        begin.setText(formatTime(data.getStart()));
        end.setText(formatTime(data.getEnd()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data.getStart());
        dPBegin.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> {
            SetDateTime(txVBegin, dPBegin, begin);
            calendar.set(dPBegin.getYear(), dPBegin.getMonth(), dPBegin.getDayOfMonth(), 0, 0, 0);
            dPEnd.setMinDate(calendar.getTimeInMillis());
        });
        calendar.setTime(data.getEnd());
        dPEnd.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> SetDateTime(txVEnd, dPEnd, end));
        SetDateTime(txVBegin, dPBegin, begin);
        SetDateTime(txVEnd, dPEnd, end);
        spinner.setSelection(colors.indexOf(new EventColor(data.getColor())));
    }

    /**
     * set data, for views, who only visible if date is not an import
     */
    private void InitDataChangeable() {
        delete.setVisibility(View.VISIBLE);
    }

    /**
     * format the date to a string
     *
     * @param date date to format
     * @return date as formatted date string
     */
    @NonNull
    private String formatDate(@NonNull Date date) {
        SimpleDateFormat format = new SimpleDateFormat("E", getResources().getConfiguration().locale);
        DateFormat dateformat = DateFormat.getDateInstance(DateFormat.MEDIUM, getResources().getConfiguration().locale);
        return String.format("%s %s", format.format(date.getTime()), dateformat.format(date.getTime()));
    }

    /**
     * convert string to date
     *
     * @param string string to convert
     * @return the date of the string
     */
    @Nullable
    private Date convertDate(@NonNull String string, int pos) {
        DateFormat dateformat = DateFormat.getDateInstance(DateFormat.MEDIUM, getResources().getConfiguration().locale);
        Calendar calendar = Calendar.getInstance(), calendar1 = Calendar.getInstance();
        try {
            Date date = dateformat.parse(string.substring(4, pos));
            if (date != null)
                calendar.setTime(date);
            date = new SimpleDateFormat("HH:mm", Locale.getDefault()).parse(string.substring(pos + 3));
            if (date != null)
                calendar1.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, calendar1.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, calendar1.get(Calendar.MINUTE));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return calendar.getTime();
    }

    /**
     * format the date to a string
     *
     * @param date date to format
     * @return date as formatted time string
     */
    @NonNull
    private String formatTime(@NonNull Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return format.format(date);
    }

    /**
     * set date and time into textView
     *
     * @param textView   textView to set the text
     * @param datePicker datePicker where the date is from
     * @param editText   editText where the time is from
     */
    private void SetDateTime(@NonNull TextView textView, @NonNull DatePicker datePicker, @NonNull EditText editText) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, datePicker.getYear());
        calendar.set(Calendar.MONTH, datePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        String help = formatDate(calendar.getTime());
        textView.setText(String.format("%s   %s", help, editText.getText().toString()));
        textView.setTag(help.length());
    }

    /**
     * init time editText
     *
     * @param text       editText to init
     * @param datePicker datePicker to get date to show in textView
     * @param textView   textView to show date time
     */
    private void InitTimeEditText(@NonNull EditText text, @NonNull DatePicker datePicker, @NonNull TextView textView) {
        text.setTag(false);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(@NonNull Editable editable) {
                if (working) return;
                working = true;
                String text_ = editable.toString();
                String without = text_;
                if (text_.contains(":"))
                    without = text_.replace(":", "");
                switch (without.length()) {
                    case 4:  //XX:XX
                        editable.replace(0, editable.length(), without);
                        editable.insert(2, ":");
                        CheckMinute(editable, 3, true);
                        CheckHour(editable);
                        SetDateTime(textView, datePicker, text);
                        break;
                    case 3: //X:XX
                        editable.replace(0, editable.length(), without);
                        editable.insert(1, ":");
                        if (CheckMinute(editable, 2, false))
                            SetDateTime(textView, datePicker, text);
                        break;
                    case 2: //XX
                    case 1:
                        editable.replace(0, editable.length(), without);
                        break;
                    case 0:
                        editable.clear();
                        break;
                }
                text.setTag(editable.length() >= 5);
                working = false;
            }

            private void CheckHour(@NonNull Editable editable) {
                String hour = editable.toString().substring(0, 2);
                if (Integer.parseInt(hour) >= 24)
                    editable.replace(0, 1, "0");
            }

            private boolean CheckMinute(@NonNull Editable editable, int pos, boolean change) {
                String minute = editable.toString().substring(pos);
                boolean erg = Integer.parseInt(minute) >= 60;
                if (erg && change)
                    editable.replace(pos, pos + 1, "0");
                return !erg;
            }
        });
        text.setOnEditorActionListener((textView1, i, keyEvent) -> {
            if (keyEvent != null || getActivity() == null) return false;
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
            return true;
        });
        //Won't work on virtual keyboard
        text.setOnKeyListener((view, i, keyEvent) -> {
            Log.d("KeyListener", keyEvent.toString());
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_DEL:
                    return false;
                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_1:
                case KeyEvent.KEYCODE_2:
                case KeyEvent.KEYCODE_3:
                case KeyEvent.KEYCODE_4:
                case KeyEvent.KEYCODE_5:
                case KeyEvent.KEYCODE_6:
                case KeyEvent.KEYCODE_7:
                case KeyEvent.KEYCODE_8:
                case KeyEvent.KEYCODE_9:
                    return (Boolean) text.getTag();
            }
            return true;
        });
    }

    /**
     * Remove dialog.
     */
    @Override
    public void onDestroyView() {
        lecture.RefreshLectureSchedule();
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