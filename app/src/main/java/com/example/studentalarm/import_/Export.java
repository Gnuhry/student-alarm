package com.example.studentalarm.import_;

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

    public static void ExportToICS(@NonNull Context context, @NonNull Activity activity, @NonNull List<Lecture_Schedule.Lecture> list) {
        Log.d("Export", "Start");
        try {
            List<ICS.vEvent> erg = new ArrayList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmssSS", Locale.getDefault());
            for (Lecture_Schedule.Lecture lecture : list) {
                ICS.vEvent event = new ICS.vEvent();
                event.SUMMARY = lecture.getName();
                event.LOCATION = lecture.getLocation();
                event.UID = format.format(Calendar.getInstance().getTime()).replace("-", "T") + "Z-" + lecture.getId();
                event.DTStart = format.format(lecture.getStart()).replace("-", "T");
                event.DTend = format.format(lecture.getEnd()).replace("-", "T");
                event.DTStamp = format.format(Calendar.getInstance().getTime()).replace("-", "T");
                erg.add(event);
            }
            File help = WriteFile(context, ICS.ExportToICS(erg));
            if (help == null) return;
            Log.d("Export", "End");
            Share(context, help, activity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private static File WriteFile(@NonNull Context context, @NonNull String text) throws IOException {
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
        System.out.println("file created: " + file);
        return file;
    }

    private static void Share(@NonNull Context context, @NonNull File file, @NonNull Activity activity) {
        Uri uri = FileProvider.getUriForFile(context, "com.example.studentalarm.fileprovider", file);
        Intent intent = ShareCompat.IntentBuilder.from(activity)
                .setType("*/*")
                .setStream(uri)
                .setChooserTitle("Choose bar")
                .createChooserIntent()
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(intent);
    }

    public static boolean DeleteAll(@NonNull Context context) {
        boolean erg = true;
        File folder = new File(context.getFilesDir(), "share");
        File[] filesInFolder = folder.listFiles();
        if (filesInFolder != null)
            for (File file : filesInFolder)
                if (!file.isDirectory())
                    erg &= file.delete();
        return erg;
    }
}
