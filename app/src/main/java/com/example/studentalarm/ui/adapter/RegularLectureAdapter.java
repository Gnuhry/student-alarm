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
import com.example.studentalarm.RegularLectureSchedule;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class RegularLectureAdapter extends RecyclerView.Adapter<RegularLectureAdapter.ViewHolder> {

    private final List<RegularLectureSchedule.RegularLecture> regularLecture;
    private final String LOG = "RegularLectureAdapter";
    @Nullable
    private LinearLayout selected;
    private int selected_id;

    public RegularLectureAdapter(List<RegularLectureSchedule.RegularLecture> regularLecture) {
        this.regularLecture = regularLecture;
    }

    @Nullable
    public RegularLectureSchedule.RegularLecture getSelected() {
        return selected == null ? null : regularLecture.get(selected_id);
    }

//    public void addRegularLecture(RegularLectureSchedule.RegularLecture lecture) {
//        regularLecture.add(lecture);
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RegularLectureAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_regular_lecture, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(LOG, position + "," + regularLecture.size());
        if (position == regularLecture.size()) {
            holder.linearLayout.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setOnClickListener(view -> {
                Log.d(LOG, "add regular lecture");
                //TODO Dialog add regular lecture
            });
        } else {
            RegularLectureSchedule.RegularLecture regularLecture = this.regularLecture.get(position);
            holder.title.setText(regularLecture.getName());
            holder.docent.setText(regularLecture.getDocent());
            if (regularLecture.getRooms() != null && regularLecture.getRooms().size() > 0) {
                holder.location.setText(regularLecture.getRooms().get(0));
                if (regularLecture.getRooms().size() > 1) {
                    holder.linearLayout.setOnLongClickListener(view -> {
                        Log.d(LOG, "change room");
                        //TODO Dialog change room
                        return true;
                    });
                }
            }
            View.OnClickListener clickListener = view -> {
                if (selected == view) {
                    Log.d(LOG, "edit");
                    //TODO Dialog edit regularLecture
                } else {
                    if (selected != null)
                        selected.setBackground(null);
                    selected = holder.linearLayout;
                    selected.setBackgroundColor(Color.RED);
                    selected_id = position;
                }
            };
            holder.linearLayout.setOnClickListener(clickListener);
            holder.title.setOnClickListener(clickListener);
            holder.docent.setOnClickListener(clickListener);
            holder.location.setOnClickListener(clickListener);
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
