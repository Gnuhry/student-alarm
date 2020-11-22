package com.example.studentalarm.Import;

import android.util.Log;

import com.example.studentalarm.DhbwMannheimCourse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DhbwMannheimCourseImport {
    private List<DhbwMannheimCourse> DHBWCourses;
    private final OkHttpClient client = new OkHttpClient();
    private boolean successful=false;

    public DhbwMannheimCourseImport() {//muss in neuem Thread aufgerufen werden new Thread(() -> {CourseNamesDHBW_Mannheim test = new CourseNamesDHBW_Mannheim();}).start();
        Log.d("Info","Aufgerufen");
        DHBWCourses=new ArrayList<>();
        runSynchronous();
    }

    public void runSynchronous() {
        successful=false;
        Request request = new Request.Builder()
                .url("https://vorlesungsplan.dhbw-mannheim.de/ical.php")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                Log.e("ICS-Synchronous", "Unexpected code " + response);
            parse(response.body().string());
            successful=true;
        } catch (IOException e) {
            e.printStackTrace();
            successful=false;
        }
    }

    private void parse(String CourseFile) {
        Log.d("HTMLImport","ICal Kurs Detail: SUCCESS "+CourseFile);
        for (String importrow : CourseFile.split("\\n")) {
            if (importrow.contains("<form id=\"class_form\" >")) {
                Log.d("HTMLAnalyse", "Relevante Zeile suchen: SUCCESS " + importrow);
                for (String option: importrow.split("<option|>")){
                    if (option.contains("label")&&option.contains("value")){
                        Log.d("HTMLZeilenanalyse", "Zeilensegment ausgewählt: SUCCESS" + option);
                        String[] course = option.split("\"");// Log beim Anlegen DhbwMannheimCourse
                        DHBWCourses.add(new DhbwMannheimCourse("TEST",course[1],course[3]));
                    }
                }

            }
        }
    }

    public List<DhbwMannheimCourse> getDHBWCourses() {
        return DHBWCourses;
    }
}
