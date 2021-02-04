package com.example.studentalarm.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.studentalarm.Formatter;
import com.example.studentalarm.R;
import com.example.studentalarm.regular.Hours;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class SettingsHourAdapter extends RecyclerView.Adapter<SettingsHourAdapter.ViewHolder> {
    private final static String LOG = "SettingsHourAdapter";
    @NonNull
    private final List<Hours> hours;
    @NonNull
    private final Context context;
    @NonNull
    private final Activity activity;
    @NonNull
    private final List<ViewHolder> holders;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView hour;
        private final EditText from, until;
        private final LinearLayout llAddDelete, llTime;
        private final ImageView add, remove;

        public ViewHolder(@NonNull View view) {
            super(view);
            hour = view.findViewById(R.id.txVHour);
            from = view.findViewById(R.id.edTFrom);
            until = view.findViewById(R.id.edTUntil);
            llAddDelete = view.findViewById(R.id.LLAddDelete);
            llTime = view.findViewById(R.id.LLTime);
            add = view.findViewById(R.id.imVAdd);
            remove = view.findViewById(R.id.imVRemove);
        }
    }

    public SettingsHourAdapter(@NonNull Context context, @NonNull Activity activity) {
        hours = Hours.load(context);
        SimpleDateFormat format = Formatter.timeFormatter();
        for (Hours hour : hours) {
            Date date = hour.getFromAsDate();
            if (date != null)
                hour.setFrom(format.format(new Date(date.getTime() + TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET))));
            date = hour.getUntilAsDate();
            if (date != null)
                hour.setUntil(format.format(new Date(date.getTime() + TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET))));
        }
        while (hours.size() < 6)
            hours.add(new Hours(hours.size() + 1));
        this.context = context;
        this.activity = activity;
        holders = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_settings_hour, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == hours.size()) {
            holder.llTime.setVisibility(View.GONE);
            holder.llAddDelete.setVisibility(View.VISIBLE);
            holder.add.setOnClickListener(view -> {
                if (hours.size() < 24) {
                    hours.add(new Hours(hours.size() + 1));
                    for (int f = 0; f < holders.size(); f++)
                        hours.get((int) holders.get(f).llTime.getTag())
                                .setFrom(holders.get(f).from.getText().toString())
                                .setUntil(holders.get(f).until.getText().toString());
                    holders.clear();
                    notifyDataSetChanged();
                }
            });
            holder.remove.setOnClickListener(view -> {
                if (hours.size() > 0) {
                    hours.remove(hours.size() - 1);
                    for (int f = 0; f < holders.size() - 1; f++)
                        hours.get((int) holders.get(f).llTime.getTag())
                                .setFrom(holders.get(f).from.getText().toString())
                                .setUntil(holders.get(f).until.getText().toString());
                    holders.clear();
                    notifyDataSetChanged();
                }
            });
            holder.add.setVisibility(hours.size() == 24 ? View.GONE : View.VISIBLE);
            holder.remove.setVisibility(hours.size() == 6 ? View.GONE : View.VISIBLE);
        } else {
            holder.llTime.setVisibility(View.VISIBLE);
            holder.llTime.setTag(position);
            holder.llAddDelete.setVisibility(View.GONE);
            holders.add(holder);
            holder.hour.setText(context.getString(R.string.hour_back, hours.get(position).getId()));
            holder.from.setText(hours.get(position).getFrom());
            holder.until.setText(hours.get(position).getUntil());
            initTimeEditText(holder.from);
            initTimeEditText(holder.until);
            holder.from.setOnKeyListener((view, i, keyEvent) -> {
                int tag = ((TagHelp) view.getTag()).id;
                initTimeEditTextBeforeAfter(holders.get(tag).from, holders.get(tag).until);
                if (i == KeyEvent.KEYCODE_ENTER) {
                    view.clearFocus();
                    new Thread(() -> {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        holder.from.post(() -> {
                            holders.get(tag).until.requestFocus();
                            holders.get(tag).until.setCursorVisible(true);
                        });
                    }).start();
                    return true;
                }
                return false;
            });
            holder.until.setOnKeyListener((view, i, keyEvent) -> {
                int tag = ((TagHelp) view.getTag()).id;
                initTimeEditTextBeforeAfter(holders.get(tag).from, holders.get(tag).until);
                if (i == KeyEvent.KEYCODE_ENTER) {
                    if (tag + 1 < getItemCount() - 1) {
                        view.clearFocus();
                        new Thread(() -> {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            holder.from.post(() -> {
                                holders.get(tag + 1).from.requestFocus();
                                holders.get(tag + 1).from.setCursorVisible(true);
                            });
                        }).start();
                        return true;
                    }
                }
                return false;
            });
            holder.from.setTag(TagHelp.build().setId(position).setBool(false));
            holder.until.setTag(TagHelp.build().setId(position).setBool(false));
        }
    }

    @Override
    public int getItemCount() {
        return hours.size() + 1;
    }

    /**
     * save if all inputs are right
     *
     * @return -1 if error else the size of the list
     */
    public int save() {
        for (int f = 0; f < holders.size(); f++) {
            if (holders.get(f).from.getText().toString().equals("") || holders.get(f).until.getText().toString().equals(""))
                return -1;
            hours.get((int) holders.get(f).llTime.getTag())
                    .setFrom(holders.get(f).from.getText().toString())
                    .setUntil(holders.get(f).until.getText().toString());
            if (holders.get(f).until.getError() != null || holders.get(f).from.getError() != null)
                return -1;
            Date date = hours.get((int) holders.get(f).llTime.getTag()).getFromAsDate();
            if (f - 1 >= 0 && date != null && date.before(hours.get((int) holders.get(f - 1).llTime.getTag()).getUntilAsDate())) {
                holders.get(f).from.setError(context.getString(R.string.can_not_start_before_the_hour_before));
                return -1;
            } else
                holders.get(f).from.setError(null);
        }
        SimpleDateFormat format = Formatter.timeFormatter();
        for (Hours hour : hours) {
            Date date = hour.getFromAsDate();
            if (date != null)
                hour.setFrom(format.format(new Date(date.getTime() - TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET))));
            date = hour.getUntilAsDate();
            if (date != null)
                hour.setUntil(format.format(new Date(date.getTime() - TimeZone.getDefault().getOffset(Calendar.ZONE_OFFSET))));
        }
        Hours.save(context, hours);
        return hours.size();
    }

    /**
     * init the edit time box
     * @param before editBox for from time
     * @param until editBox for until time
     */
    private void initTimeEditTextBeforeAfter(@NonNull EditText before, @Nullable EditText until) {
        SimpleDateFormat format = Formatter.timeFormatter();
        try {
            Date beforeDate = format.parse(before.getText().toString());
            if (until != null && beforeDate != null && !beforeDate.before(format.parse(until.getText().toString())))
                until.setError(context.getString(R.string.end_must_start_after_begin));
        } catch (ParseException e) {
            Log.d(LOG, "no date");
        }
    }

    /**
     * init time editText
     *
     * @param text editText to init
     */
    private void initTimeEditText(@NonNull EditText text) {
        Log.i(LOG, "init time edit");
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(@NonNull Editable editable) {
                if (text.getTag() == null) return;
                TagHelp help = ((TagHelp) text.getTag());
                if (help == null || help.bool) return;
                help.setBool(true);
                String text_ = editable.toString();
                String without = text_;
                if (text_.contains(":"))
                    without = text_.replace(":", "");
                switch (without.length()) {
                    case 4:  //XX:XX
                        editable.replace(0, editable.length(), without);
                        editable.insert(2, ":");
                        checkMinute(editable, 3, true);
                        checkHour(editable);
                        text.setError(null);
                        break;
                    case 3: //X:XX
                        editable.replace(0, editable.length(), without);
                        editable.insert(1, ":");
                        if (checkMinute(editable, 2, false))
                            text.setError(null);
                        else
                            text.setError(context.getString(R.string.wrong));
                        break;
                    case 2: //XX
                    case 1:
                        editable.replace(0, editable.length(), without);
                        text.setError(context.getString(R.string.wrong));
                        break;
                    case 0:
                        text.setError(context.getString(R.string.wrong));
                        editable.clear();
                        break;
                }
                help.setBool(false);
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
            if (keyEvent != null) return false;
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
            return true;
        });
    }

    static class TagHelp {
        public boolean bool;
        public int id;

        @NonNull
        public TagHelp setBool(boolean bool) {
            this.bool = bool;
            return this;
        }

        @NonNull
        public TagHelp setId(int id) {
            this.id = id;
            return this;
        }

        @NonNull
        public static TagHelp build() {
            return new TagHelp();
        }
    }
}
