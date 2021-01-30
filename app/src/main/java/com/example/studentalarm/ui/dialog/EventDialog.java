package com.example.studentalarm.ui.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.studentalarm.EventColor;
import com.example.studentalarm.R;
import com.example.studentalarm.alarm.AlarmManager;
import com.example.studentalarm.imports.LectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.fragments.ReloadLecture;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import static com.example.studentalarm.save.PreferenceKeys.getLocale;

public class EventDialog extends DialogFragment implements CallColorDialog {
    private static final String LOG = "EventDialogFragment";
    @Nullable
    private final LectureSchedule.Lecture data;
    private final LectureSchedule schedule;
    private final ReloadLecture lecture;
    private static boolean working;

    private EditText title, docent, location, begin, end;
    private LinearLayout LBegin, LEnd, llColor;
    private ConstraintLayout CBegin, CEnd;
    private DatePicker dPBegin, dPEnd;
    private TextView txVBegin, txVEnd, add, cancel, delete, color;
    private boolean create = false, cancelDirect = true;
    private int colorHelp;

    public EventDialog(@Nullable LectureSchedule.Lecture data, LectureSchedule schedule, ReloadLecture lecture) {
        this.lecture = lecture;
        this.data = data;
        this.schedule = schedule;
        colorHelp = data == null ? PreferenceKeys.DEFAULT_EVENT_COLOR : data.getColor();
        if (data != null)
            Log.d(LOG, "data: " + data.toString());

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG, "open");
        View view = inflater.inflate(R.layout.dialog_event, container, false);
        if (getContext() == null) return view;
        title = view.findViewById(R.id.edTTitle);
        docent = view.findViewById(R.id.edTDocent);
        location = view.findViewById(R.id.edTLocation);
        begin = view.findViewById(R.id.edTTimeBegin);
        end = view.findViewById(R.id.edTTimeEnd);
        LBegin = view.findViewById(R.id.LLTimeBegin);
        LEnd = view.findViewById(R.id.LLEnd);
        llColor = view.findViewById(R.id.llColor);
        CBegin = view.findViewById(R.id.CLBegin);
        CEnd = view.findViewById(R.id.CLEnd);
        dPBegin = view.findViewById(R.id.dPDateBegin);
        dPEnd = view.findViewById(R.id.dPDateEnd);
        txVBegin = view.findViewById(R.id.txVBegin);
        txVEnd = view.findViewById(R.id.txVEnd);
        add = view.findViewById(R.id.txVAdd);
        cancel = view.findViewById(R.id.txVCancel);
        delete = view.findViewById(R.id.txVDelete);
        color = view.findViewById(R.id.txVColor);

        Log.d(LOG, "Context is: " + getContext());

        setColor();
        if (data != null) {
            initData();
            if (!data.isImport()) {
                cancelDirect = false;
                initListener();
                initDataChangeable();
            } else
                disableView();
        } else {
            create = true;
            initListener();
            initDatePickerListenerWithoutDate();
            cancelDirect = false;
        }
        initCancel();
        return view;
    }

    /**
     * Remove dialog.
     */
    @Override
    public void onDestroyView() {
        Log.i(LOG, "destroy");
        if (data != null && data.isImport()) {
            super.onDestroyView();
            return;
        }
        lecture.loadData();
        if (getContext() != null)
            AlarmManager.updateNextAlarm(this.getContext());
        super.onDestroyView();
    }

    @Override
    public void setColorHelp(int colorHelp) {
        this.colorHelp = colorHelp;
        setColor();
    }

    /**
     * init the cancel button
     */
    private void initCancel() {
        Log.i(LOG, "Init cancel button");
        cancel.setOnClickListener(view -> {
            if (cancelDirect)
                this.dismiss();
            else if (data == null) {
                if (title.getText().toString().equals("") && docent.getText().toString().equals("") && location.getText().toString().equals("") &&
                        txVBegin.getText().toString().equals("") && txVEnd.getText().toString().equals(""))
                    this.dismiss();
                else {
                    if (getContext() != null)
                        new MaterialAlertDialogBuilder(getContext())
                                .setTitle(R.string.dismiss)
                                .setMessage(R.string.do_you_want_to_dismiss_all_your_changes)
                                .setPositiveButton(R.string.dismiss, (dialogInterface, i) -> this.dismiss())
                                .setNegativeButton(R.string.no, null)
                                .setCancelable(true)
                                .show();
                }
            } else {
                String docentString = data.getDocent();
                if (docentString == null) docentString = "";
                String locationString = data.getLocation();
                if (locationString == null) locationString = "";
                if (title.getText().toString().equals(data.getName()) && docent.getText().toString().equals(docentString) && location.getText().toString().equals(locationString) &&
                        txVBegin.getText().toString().equals(formatDate(data.getStartWithDefaultTimeZone()) + "   " + formatTime(data.getStartWithDefaultTimeZone())) &&
                        txVEnd.getText().toString().equals(formatDate(data.getEndWithDefaultTimezone()) + "   " + formatTime(data.getEndWithDefaultTimezone())))
                    this.dismiss();
                else {
                    if (getContext() != null)
                        new MaterialAlertDialogBuilder(getContext())
                                .setTitle(R.string.dismiss)
                                .setMessage(R.string.do_you_want_to_dismiss_all_your_changes)
                                .setPositiveButton(R.string.dismiss, (dialogInterface, i) -> this.dismiss())
                                .setNegativeButton(R.string.no, null)
                                .setCancelable(true)
                                .show();
                }
            }
        });
    }

    /**
     * init datePicker views to make changes possible
     */
    private void initDatePickerListenerWithoutDate() {
        Log.i(LOG, "Init datePicker without data");
        Calendar calendar = Calendar.getInstance();
        dPBegin.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> {
            setDateTime(txVBegin, dPBegin, begin);
            calendar.set(dPBegin.getYear(), dPBegin.getMonth(), dPBegin.getDayOfMonth(), 0, 0, 0);
            dPEnd.setMinDate(calendar.getTimeInMillis());
        });
        dPEnd.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> setDateTime(txVEnd, dPEnd, end));
    }

    /**
     * init all views to make changes possible
     */
    private void initListener() {
        Log.i(LOG, "Init listener");
        initTimeEditText(begin, dPBegin, txVBegin);
        initTimeEditText(end, dPEnd, txVEnd);
        add.setOnClickListener(view -> {
            Log.i(LOG, "start checking");
            if (getContext() == null) return;
            boolean error = false;

            if (begin.getText().length() <= 3) {
                Log.i(LOG, "begin missing");
                begin.setError(getString(R.string.missing));
                txVBegin.setError(getString(R.string.missing));
                error = true;
            } else {
                begin.setError(null);
                txVBegin.setError(null);
            }

            if (end.getText().length() <= 3) {
                Log.i(LOG, "end missing");
                end.setError(getString(R.string.missing));
                txVEnd.setError(getString(R.string.missing));
                error = true;
            } else {
                end.setError(null);
                txVEnd.setError(null);
            }

            if (title.getText().toString().isEmpty()) {
                Log.i(LOG, "title missing");
                title.setError(getString(R.string.missing));
                error = true;
            } else
                title.setError(null);

            boolean error2 = false;
            if (txVBegin.getTag() == null) {
                Log.i(LOG, "begin missing");
                begin.setError(getString(R.string.missing));
                txVBegin.setError(getString(R.string.missing));
                error2 = true;
            } else {
                begin.setError(null);
                txVBegin.setError(null);
            }

            if (txVEnd.getTag() == null) {
                Log.i(LOG, "end missing");
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
                Log.i(LOG, "begin wrong");
                begin.setError(getString(R.string.wrong));
                txVBegin.setError(getString(R.string.wrong));
                error = true;
            } else {
                begin.setError(null);
                txVBegin.setError(null);
            }

            if (dEnd == null) {
                Log.i(LOG, "end wrong");
                end.setError(getString(R.string.wrong));
                txVEnd.setError(getString(R.string.wrong));
                error = true;
            } else {
                end.setError(null);
                txVEnd.setError(null);
            }
            if (error) return;

            if (dBegin.after(dEnd)) {
                Log.i(LOG, "begin start after end");
                end.setError(getString(R.string.end_must_start_after_begin));
                txVEnd.setError(getString(R.string.end_must_start_after_begin));
                return;
            }

            if (create) {
                Log.i(LOG, "Create Lecture");
                schedule.addLecture(new LectureSchedule.Lecture(false,
                        new Date(dBegin.getTime() - TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET)),
                        new Date(dEnd.getTime() - TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET))).setName(title.getText().toString())
                        .setDocent(docent.getText().toString())
                        .setLocation(location.getText().toString())
                        .setColor(colorHelp));
            } else {
                Log.i(LOG, "Update Lecture");
                List<LectureSchedule.Lecture> help = schedule.getAllLecture(getContext());
                help.get(help.indexOf(this.data))
                        .setName(title.getText().toString())
                        .setDocent(docent.getText().toString())
                        .setLocation(location.getText().toString())
                        .setStart(new Date(dBegin.getTime() - TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET)))
                        .setEnd(new Date(dEnd.getTime() - TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET)))
                        .setColor(colorHelp);
            }
            schedule.save(getContext());
            this.dismiss();
        });
        delete.setOnClickListener(view -> {
            Log.i(LOG, "delete");
            if (getContext() == null) return;
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle(R.string.delete)
                    .setMessage(R.string.do_you_want_to_delete_this_events)
                    .setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                        if (data == null) return;
                        Log.i(LOG, "delete Lecture");
                        schedule.removeLecture(this.data).save(getContext());
                        this.dismiss();
                    })
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> this.dismiss())
                    .setCancelable(true)
                    .show();

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
        llColor.setOnClickListener(view -> {
            if (getActivity() != null)
                new ColorDialog(data == null ? PreferenceKeys.DEFAULT_EVENT_COLOR : data.getColor(), this).show(getActivity().getSupportFragmentManager(), "dialog");
        });
    }

    /**
     * set the date in the views
     */
    private void initData() {
        Log.i(LOG, "init data");
        add.setText(R.string.update);
        if (data == null) return;
        title.setText(data.getName());
        docent.setText(data.getDocent());
        location.setText(data.getLocation());
        begin.setText(formatTime(data.getStartWithDefaultTimeZone()));
        end.setText(formatTime(data.getEndWithDefaultTimezone()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(data.getStartWithDefaultTimeZone());
        dPBegin.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> {
            setDateTime(txVBegin, dPBegin, begin);
            calendar.set(dPBegin.getYear(), dPBegin.getMonth(), dPBegin.getDayOfMonth(), 0, 0, 0);
            dPEnd.setMinDate(calendar.getTimeInMillis());
        });
        calendar.setTime(data.getEndWithDefaultTimezone());
        dPEnd.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (datePicker, i, i1, i2) -> setDateTime(txVEnd, dPEnd, end));
        setDateTime(txVBegin, dPBegin, begin);
        setDateTime(txVEnd, dPEnd, end);
    }

    /**
     * set data, for views, who only visible if date is not an import
     */
    private void initDataChangeable() {
        Log.i(LOG, "init data changeable");
        delete.setVisibility(View.VISIBLE);
    }

    /**
     * init time editText
     *
     * @param text       editText to init
     * @param datePicker datePicker to get date to show in textView
     * @param textView   textView to show date time
     */
    private void initTimeEditText(@NonNull EditText text, @NonNull DatePicker datePicker, @NonNull TextView textView) {
        Log.i(LOG, "init time edit");
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
                Log.d(LOG, "EditText: " + text_);
                String without = text_;
                if (text_.contains(":"))
                    without = text_.replace(":", "");
                switch (without.length()) {
                    case 4:  //XX:XX
                        editable.replace(0, editable.length(), without);
                        editable.insert(2, ":");
                        checkMinute(editable, 3, true);
                        checkHour(editable);
                        setDateTime(textView, datePicker, text);
                        break;
                    case 3: //X:XX
                        editable.replace(0, editable.length(), without);
                        editable.insert(1, ":");
                        if (checkMinute(editable, 2, false))
                            setDateTime(textView, datePicker, text);
                        break;
                    case 2: //XX
                    case 1:
                        editable.replace(0, editable.length(), without);
                        break;
                    case 0:
                        editable.clear();
                        break;
                }
                Log.d(LOG, "EditText-after: " + editable.toString());
                text.setTag(editable.length() >= 5);
                working = false;
            }

            private void checkHour(@NonNull Editable editable) {
                String hour = editable.toString().substring(0, 2);
                if (Integer.parseInt(hour) >= 24)
                    editable.replace(0, 1, "0");
            }

            private boolean checkMinute(@NonNull Editable editable, int pos, boolean change) {
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
     * disable all views, if lecture is import
     */
    private void disableView() {
        Log.i(LOG, "Disable the views");
        title.setEnabled(false);
        docent.setEnabled(false);
        location.setEnabled(false);
        add.setVisibility(View.INVISIBLE);
        llColor.setEnabled(false);
    }

    /**
     * format the date to a string
     *
     * @param date date to format
     * @return date as formatted date string
     */
    @NonNull
    private String formatDate(@NonNull Date date) {
        Locale locale = getLocale(getContext());
        SimpleDateFormat format = new SimpleDateFormat("E", locale);
        DateFormat dateformat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        return String.format("%s %s", format.format(date.getTime()), dateformat.format(date.getTime()));
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
     * convert string to date
     *
     * @param string string to convert
     * @return the date of the string
     */
    @Nullable
    private Date convertDate(@NonNull String string, int pos) {
        Locale locale = getLocale(getContext());
        DateFormat dateformat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
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
     * set date and time into textView
     *
     * @param textView   textView to set the text
     * @param datePicker datePicker where the date is from
     * @param editText   editText where the time is from
     */
    private void setDateTime(@NonNull TextView textView, @NonNull DatePicker datePicker, @NonNull EditText editText) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, datePicker.getYear());
        calendar.set(Calendar.MONTH, datePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        String help = formatDate(calendar.getTime());
        textView.setText(String.format("%s   %s", help, editText.getText().toString()));
        textView.setTag(help.length());
    }

    /**
     * sets color views
     */
    private void setColor() {
        if (getContext() == null) return;
        List<EventColor> colors = EventColor.possibleColors(getContext());
        int index = colors.indexOf(new EventColor(data == null ? colorHelp : data.getColor()));
        if (index == -1)
            color.setText(getString(R.string.custom));
        else color.setText(colors.get(index).getName());
    }

}