package com.example.studentalarm.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.studentalarm.R;
import com.example.studentalarm.regular.RegularLectureSchedule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private final RegularLectureSchedule.RegularLecture lecture;
    @NonNull
    private final List<ViewHolder> holders;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final EditText room;
        private final LinearLayout llRoom;
        private final ImageView add, remove;

        public ViewHolder(@NonNull View view) {
            super(view);
            room = view.findViewById(R.id.edTRoom);
            llRoom = view.findViewById(R.id.LLRom);
            add = view.findViewById(R.id.imVAdd);
            remove = view.findViewById(R.id.imVDelete);
        }
    }

    public RoomAdapter(RegularLectureSchedule.RegularLecture lecture) {
        this.lecture = lecture;
        this.holders = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_room, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (lecture.getRooms().size() == position) {
            holder.llRoom.setVisibility(View.GONE);
            holder.add.setVisibility(View.VISIBLE);
            holder.add.setOnClickListener(view -> {
                lecture.getRooms().add("");
                for (int f = 0; f < holders.size(); f++)
                    lecture.getRooms().set((int) holders.get(f).room.getTag(), holders.get(f).room.getText().toString());
                holders.clear();
                notifyDataSetChanged();
            });
        } else {
            holders.add(holder);
            holder.room.setTag(position);
            holder.llRoom.setVisibility(View.VISIBLE);
            holder.add.setVisibility(View.GONE);
            holder.room.setText(lecture.getRooms().get(position));
            holder.remove.setOnClickListener(view -> {
                for (int f = 0; f < holders.size(); f++)
                    lecture.getRooms().set((int) holders.get(f).room.getTag(), holders.get(f).room.getText().toString());
                lecture.getRooms().remove(position);
                holders.clear();
                notifyDataSetChanged();
            });
        }
    }

    @Override
    public int getItemCount() {
        return lecture.getRooms().size() + 1;
    }

    @NonNull
    public List<String> getAllRooms() {
        for (int f = 0; f < holders.size(); f++)
            lecture.getRooms().set((int) holders.get(f).room.getTag(), holders.get(f).room.getText().toString());
        for (Iterator<String> iterator = lecture.getRooms().iterator(); iterator.hasNext(); )
            if (iterator.next().equals(""))
                iterator.remove();
        Log.d("all rooms", lecture.getRooms().size() + "");
        return lecture.getRooms();
    }


}
