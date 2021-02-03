package com.example.studentalarm;

import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.annotation.NonNull;

public class Formatter {
    /**
     * get day formatter [dd.MM.yyyy]
     *
     * @return SimpleDateFormat
     */
    @NonNull
    public static SimpleDateFormat dayFormatter() {
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    }

    /**
     * get day formatter [yyyyMMdd-HHmmssSS]
     *
     * @return SimpleDateFormat
     */
    @NonNull
    public static SimpleDateFormat dateFormatterWithMilli() {
        return new SimpleDateFormat("yyyyMMdd-HHmmssSS", Locale.getDefault());
    }

    /**
     * get day formatter [yyyyMMdd-HHmmss]
     *
     * @return SimpleDateFormat
     */
    @NonNull
    public static SimpleDateFormat dateFormatter() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());
    }

    /**
     * get day formatter [HH:mm]
     *
     * @return SimpleDateFormat
     */
    @NonNull
    public static SimpleDateFormat timeFormatter() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault());
    }
}
