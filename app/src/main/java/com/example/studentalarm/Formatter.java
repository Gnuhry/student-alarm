package com.example.studentalarm;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Formatter {
    public static SimpleDateFormat dayFormatter() {
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    }
    public static SimpleDateFormat dateFormatter() {
        return new SimpleDateFormat("yyyyMMdd-HHmmssSS", Locale.getDefault());
    }

    public static SimpleDateFormat timeFormatter(){
        return new SimpleDateFormat("HH:mm", Locale.getDefault());
    }
}
