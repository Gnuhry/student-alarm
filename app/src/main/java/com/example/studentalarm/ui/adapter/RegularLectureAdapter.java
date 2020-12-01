package com.example.studentalarm.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.studentalarm.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RegularLectureAdapter extends RecyclerView.Adapter<RegularLectureAdapter.ViewHolder> {

    public RegularLectureAdapter(){

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RegularLectureAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lecture_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title, docent, location;

        public ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.txVTitle);
            docent = view.findViewById(R.id.txVDocent);
            location = view.findViewById(R.id.txVLocation);
        }
    }
}
