package com.example.studentalarm;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PossibleColors {
    private static final String LOG = "PossibleColors";
    private final Context context;
    private final List<EventColor> colors;

    public PossibleColors(@NonNull Context context) {
        Log.d(LOG, "Create possibleColor List context:"+context);
        this.context = context;
        colors = new ArrayList<>();
        colors.add(new EventColor(R.string.red, Color.RED,this.context));//String  Farbname wird übergeben
        colors.add(new EventColor(R.string.green, Color.GREEN,this.context));
        colors.add(new EventColor(R.string.blue, Color.BLUE,this.context));
        colors.add(new EventColor(R.string.yellow, Color.YELLOW,this.context));
        Log.d(LOG, "Finished creating possibleColor List");
    }

    public List<EventColor> colorList(){
        Log.d(LOG, "return color List");
        return colors;
    }




}
