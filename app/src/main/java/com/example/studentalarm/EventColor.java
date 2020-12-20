package com.example.studentalarm;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class EventColor {
    private static final String LOG = "EventColor";
    /**
     * class to create a adapter with colors for spinner
     */
    private final int name, color;
    private final Context context;

    public EventColor(int color, @NonNull Context context) {
        this.name = 0;
        this.color = color;
        this.context=context;
        Log.d(LOG, "Create 2var, context: "+context+" name: "+name+" Color: "+color);
    }

    public EventColor(int name, int color, @NonNull Context context) {
        this.name = name;
        this.color = color;
        this.context=context;
        Log.d(LOG, "Create 3var, context: "+context+" name: "+name+" Color: "+color);
    }

    public int getColor() {
        return color;
    }
    public int getname() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        Log.d(LOG, "ToString, context: "+context+" name: "+name);
        return context.getString(name);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof EventColor)
            return color == ((EventColor) obj).color;
        return false;
    }
}
