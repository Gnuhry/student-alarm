package com.example.studentalarm.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.studentalarm.R;
import com.example.studentalarm.regular.RegularLectureSchedule;
import com.example.studentalarm.save.PreferenceKeys;
import com.example.studentalarm.ui.dialog.ChangeRoomDialog;
import com.example.studentalarm.ui.dialog.RegularLectureDialog;
import com.example.studentalarm.ui.fragments.RegularLectureFragment;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

public class RegularLectureAdapter extends RecyclerView.Adapter<RegularLectureAdapter.ViewHolder> {

    private static final String LOG = "RegularLectureAdapter";
    @NonNull
    private final RegularLectureSchedule regularLectureSchedule;
    @NonNull
    private final List<RegularLectureSchedule.RegularLecture> regularLecture;
    private final RegularLectureFragment fragment;
    private final Activity activity;
    @Nullable
    private LinearLayout selected;
    private int selected_id;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, docent, location;
        private final LinearLayout linearLayout;
        private final ImageView imageView;

        public ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.txVTitle);
            docent = view.findViewById(R.id.txVDocent);
            location = view.findViewById(R.id.txVLocation);
            linearLayout = view.findViewById(R.id.LLRegularLecture);
            imageView = view.findViewById(R.id.imVAdd);
        }
    }

    public RegularLectureAdapter(@NonNull RegularLectureSchedule regularLectureSchedule, RegularLectureFragment fragment, Activity activity) {
        this.regularLectureSchedule = regularLectureSchedule;
        regularLecture = regularLectureSchedule.getLectures();
        this.fragment = fragment;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_regular_lecture, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(LOG, position + "," + regularLecture.size());
        if (position == regularLecture.size()) {
            if (PreferenceManager.getDefaultSharedPreferences(activity).getBoolean(PreferenceKeys.APP_FIRST_TIME, true)) {
                PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(PreferenceKeys.APP_FIRST_TIME, false).apply();
                new OwnShowcaseView(activity,
                        new ViewTarget(holder.imageView),
                        activity.getString(R.string.to_create_lecture_click_on_the_button),
                        activity.getString(R.string.after_creating_the_lecture_),
                        view1 -> {
                            if ((boolean) view1.getTag()) return;
                            view1.setTag(true);
                            Toolbar toolbar = activity.findViewById(R.id.my_toolbar);
                            new OwnShowcaseView(activity,
                                    new ViewTarget(toolbar.findViewById(toolbar.getMenu().getItem(2).getItemId())),
                                    activity.getString(R.string.to_set_the_timetable_time_click_on_the_settings_button),
                                    "",
                                    null).show();
                        }).show();
            }
            holder.linearLayout.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setOnClickListener(view -> {
                Log.d(LOG, "add regular lecture");
                if (fragment.getActivity() != null)
                    new RegularLectureDialog(regularLectureSchedule, -1, fragment).show(fragment.getActivity().getSupportFragmentManager(), "dialog");
            });
        } else {
            RegularLectureSchedule.RegularLecture regularLecture = this.regularLecture.get(position);
            holder.title.setText(regularLecture.getName());
            holder.docent.setText(regularLecture.getDocent());
            Log.d("adapter_room", "" + regularLecture.getRooms().size());
            if (regularLecture.getRooms().size() > 0) {
                holder.location.setText(regularLecture.getActiveRoom());
                if (regularLecture.getRooms().size() > 1) {
                    View.OnLongClickListener longClickListener = view -> {
                        Log.d(LOG, "change room");
                        if (fragment.getContext() == null) return false;
                        ChangeRoomDialog dialog = new ChangeRoomDialog(fragment.getContext(), regularLecture);
                        dialog.setOnDismissListener(dialogInterface -> fragment.loadRecyclerView());
                        dialog.show();
                        return true;
                    };
                    holder.linearLayout.setOnLongClickListener(longClickListener);
                    holder.title.setOnLongClickListener(longClickListener);
                    holder.docent.setOnLongClickListener(longClickListener);
                    holder.location.setOnLongClickListener(longClickListener);
                }
            }
            View.OnClickListener clickListener = view -> {
                if (selected == view || (selected != null && selected.getTag() == view.getTag())) {
                    Log.d(LOG, "edit");
                    if (fragment.getActivity() != null)
                        new RegularLectureDialog(regularLectureSchedule, position, fragment).show(fragment.getActivity().getSupportFragmentManager(), "dialog");
                } else {
                    if (selected != null)
                        selected.setBackground(null);
                    selected = holder.linearLayout;
                    selected.setBackgroundResource(R.drawable.textview_border);
                    selected_id = position;
                }
            };
            holder.linearLayout.setOnClickListener(clickListener);
            holder.linearLayout.setTag(position);
            holder.title.setOnClickListener(clickListener);
            holder.title.setTag(position);
            holder.docent.setOnClickListener(clickListener);
            holder.docent.setTag(position);
            holder.location.setOnClickListener(clickListener);
            holder.location.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        return regularLecture.size() + 1;
    }

    /**
     * get the selected lecture
     *
     * @return selected lecture
     */
    @Nullable
    public RegularLectureSchedule.RegularLecture getSelected() {
        return selected == null ? null : regularLecture.get(selected_id);
    }
}

class OwnShowcaseView extends ShowcaseView {

    private Activity activity;

    public OwnShowcaseView(@NonNull Context context) {
        super(context, false);
    }

    protected OwnShowcaseView(@NonNull Activity activity, Target target, String title, String subtitle, @Nullable OnClickListener listener) {
        super(activity, false);
        this.activity = activity;
        this.setOnTouchListener((view, motionEvent) -> {
            this.hide();
            if (listener != null)
                listener.onClick(this);
            this.performClick();
            return true;
        });
        this.hideButton();
        this.setStyle(R.style.CustomShowcaseTheme);
        this.setHideOnTouchOutside(true);
        this.setTarget(target);
        this.setContentTitle(title);
        this.setContentText(subtitle);
        this.setOnClickListener(listener);
        this.setTag(false);
        this.setTitleTextAlignment(Layout.Alignment.ALIGN_CENTER);
        this.setDetailTextAlignment(Layout.Alignment.ALIGN_CENTER);

    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void show() {
        ((ViewGroup) activity.findViewById(android.R.id.content)).addView(this);
        super.show();
    }
}
