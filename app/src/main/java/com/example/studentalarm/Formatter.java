package com.example.studentalarm;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Formatter {
    /**
     * get day formatter [dd.MM.yyyy]
     *
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat dayFormatter() {
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    }

    /**
     * get day formatter [yyyyMMdd-HHmmssSS]
     *
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat dateFormatterWithMilli() {
        return new SimpleDateFormat("yyyyMMdd-HHmmssSS", Locale.getDefault());
    }

    /**
     * get day formatter [yyyyMMdd-HHmmss]
     *
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat dateFormatter() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());
    }

    /**
     * get day formatter [HH:mm]
     *
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat timeFormatter() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault());
    }
}
