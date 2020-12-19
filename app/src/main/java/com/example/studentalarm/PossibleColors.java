package com.example.studentalarm;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.studentalarm.ui.dialog.RegularLectureDialog;

import java.util.ArrayList;
import java.util.List;

public class PossibleColors {

    private final List<RegularLectureDialog.EventColor> colors;
    colors = new ArrayList<>();
        colors.add(new EventColor(R.string.red, Color.RED));
        colors.add(new EventColor(R.string.green, Color.GREEN));
        colors.add(new EventColor(R.string.blue, Color.BLUE));
        colors.add(new EventColor(R.string.yellow, Color.YELLOW));

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
            if (obj instanceof RegularLectureDialog.EventColor)
                return color == ((RegularLectureDialog.EventColor) obj).color;
            return false;
        }
    }
}
