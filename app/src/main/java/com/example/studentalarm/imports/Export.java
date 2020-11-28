package com.example.studentalarm.imports;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

public class Export {

    private static final String LOG="Export";
    /**
     * export event to ICS
     *
     * @param context  context of application
     * @param activity activity of app
     * @param list     events list
     */
    public static void exportToICS(@NonNull Context context, @NonNull Activity activity, @NonNull List<LectureSchedule.Lecture> list) {
        Log.d(LOG, "Start");
        try {
            List<ICS.vEvent> erg = new ArrayList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmssSS", Locale.getDefault());
            for (LectureSchedule.Lecture lecture : list) {
                ICS.vEvent event = new ICS.vEvent();
                event.SUMMARY = lecture.getName();
                event.LOCATION = lecture.getLocation();
                event.UID = format.format(Calendar.getInstance().getTime()).replace("-", "T") + "Z-" + lecture.getId();
                event.DTStart = format.format(lecture.getStart()).replace("-", "T");
                event.DTend = format.format(lecture.getEnd()).replace("-", "T");
                event.DTStamp = format.format(Calendar.getInstance().getTime()).replace("-", "T");
                erg.add(event);
            }
            File help = writeFile(context, ICS.exportToICS(erg));
            if (help == null) return;
            Log.d(LOG, "End");
            share(context, help, activity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the ics string to a file
     *
     * @param context context of application
     * @param text    text of ics
     * @return the ics file
     */
    @Nullable
    private static File writeFile(@NonNull Context context, @NonNull String text) throws IOException {
        Log.d(LOG, "Write to file");
        File documentsPath = new File(context.getFilesDir(), "share/");
        if (!documentsPath.exists() && !documentsPath.mkdir()) return null;

        File file = new File(documentsPath.getAbsolutePath() +
                File.separator +
                new SimpleDateFormat("yyyyMMddHHmmssSS", Locale.getDefault()).format(Calendar.getInstance().getTime())
                + "_output.ics");
        if (!file.exists() && !file.createNewFile()) return null;
        OutputStream fo = new FileOutputStream(file);
        fo.write(text.getBytes());
        fo.close();
        Log.d(LOG, "file created "+file);
        return file;
    }

    /**
     * File share dialog
     *
     * @param context  context of application
     * @param file     file to share
     * @param activity activity of application
     */
    public static void share(@NonNull Context context, @NonNull File file, @NonNull Activity activity) {
        Log.d(LOG, "sharing");
        Uri uri = FileProvider.getUriForFile(context, "com.example.studentalarm.fileprovider", file);
        Intent intent = ShareCompat.IntentBuilder.from(activity)
                .setType("*/*")
                .setStream(uri)
                .setChooserTitle("Choose bar")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(intent);
    }
}
