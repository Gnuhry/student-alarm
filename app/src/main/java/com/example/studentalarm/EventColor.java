package com.example.studentalarm;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * class for the Event color
 */
public class EventColor {
    private static final String LOG = "EventColor";

    private final int name, color;
    private Context context;

    /**
     *only used for Comparison => no context needed.
     */
    public EventColor(int color) {
        this.name = 0;
        this.color = color;
        Log.d(LOG, "Create 2var, name: "+name+" Color: "+color);
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
    public int getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        Log.d(LOG, "ToString, context: "+context+" name: "+name);
        return (name!=0) ? context.getString(name) : context.getString(R.string.error);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return (obj instanceof EventColor) && color == ((EventColor) obj).color;
    }

    public static List<EventColor> possibleColors (@NonNull Context context) {
        Log.d(LOG, "Create possibleColor List context:"+context);
        List<EventColor> colors = new ArrayList<>();
        colors.add(new EventColor(R.string.red, Color.RED,context));//String  Farbname wird übergeben
        colors.add(new EventColor(R.string.green, Color.GREEN,context));
        colors.add(new EventColor(R.string.blue, Color.BLUE,context));
        colors.add(new EventColor(R.string.yellow, Color.YELLOW,context));
        Log.d(LOG, "Finished creating possibleColor List");
        return colors;
    }
}
