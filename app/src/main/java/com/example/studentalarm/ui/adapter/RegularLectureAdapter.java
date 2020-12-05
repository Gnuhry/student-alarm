package com.example.studentalarm.ui.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.studentalarm.R;
import com.example.studentalarm.regular.RegularLectureSchedule;
import com.example.studentalarm.ui.dialog.ChangeRoomDialog;
import com.example.studentalarm.ui.dialog.RegularLectureDialog;
import com.example.studentalarm.ui.fragments.RegularLectureFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RegularLectureAdapter extends RecyclerView.Adapter<RegularLectureAdapter.ViewHolder> {

    @NonNull
    private final RegularLectureSchedule regularLectureSchedule;
    @NonNull
    private final List<RegularLectureSchedule.RegularLecture> regularLecture;
    private final String LOG = "RegularLectureAdapter";
    @Nullable
    private LinearLayout selected;
    private int selected_id;
    private final RegularLectureFragment fragment;

    public RegularLectureAdapter(@NonNull RegularLectureSchedule regularLectureSchedule, RegularLectureFragment fragment) {
        this.regularLectureSchedule = regularLectureSchedule;
        regularLecture = regularLectureSchedule.getLectures();
        this.fragment = fragment;
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_regular_lecture, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(LOG, position + "," + regularLecture.size());
        if (position == regularLecture.size()) {
            holder.linearLayout.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setOnClickListener(view -> {
                Log.d(LOG, "add regular lecture");
                new RegularLectureDialog(regularLectureSchedule, -1, fragment).show(fragment.getActivity().getSupportFragmentManager(), "dialog");
            });
        } else {
            RegularLectureSchedule.RegularLecture regularLecture = this.regularLecture.get(position);
            holder.title.setText(regularLecture.getName());
            holder.docent.setText(regularLecture.getDocent());
            if (regularLecture.getRooms() != null && regularLecture.getRooms().size() > 0) {
                holder.location.setText(regularLecture.getActiveRoom());
                if (regularLecture.getRooms().size() > 1) {
                    View.OnLongClickListener longClickListener = view -> {
                        Log.d(LOG, "change room");
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
                    new RegularLectureDialog(regularLectureSchedule, position, fragment).show(fragment.getActivity().getSupportFragmentManager(), "dialog");
                } else {
                    if (selected != null)
                        selected.setBackground(null);
                    selected = holder.linearLayout;
                    selected.setBackgroundColor(Color.RED);
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
}
