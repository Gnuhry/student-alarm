package com.example.studentalarm.Import;

import android.util.Log;

import com.example.studentalarm.DhbwMannheimCategory;
import com.example.studentalarm.DhbwMannheimCourse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DhbwMannheimCourseImport {
    private List<DhbwMannheimCategory> DHBWCoursecategory;
    private List<DhbwMannheimCourse> tempDHBWCourses;
    private final OkHttpClient client = new OkHttpClient();
    private boolean successful=false;

    public DhbwMannheimCourseImport() {//muss in neuem Thread aufgerufen werden new Thread(() -> {CourseNamesDHBW_Mannheim test = new CourseNamesDHBW_Mannheim();}).start();
        Log.d("Info","Aufgerufen");
        runSynchronous();
    }

    public void runSynchronous() {
        DHBWCoursecategory=new ArrayList<>();
        tempDHBWCourses=new ArrayList<>();
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
                for (String optgroup: importrow.split("<optgroup")) {
                    String[] coursecategory = optgroup.split("\"");// speichert zusätzlichen Array um Category herauszufinden

                    for (String option : importrow.split("<option|>")) {
                        if (option.contains("label") && option.contains("value")) {
                            Log.d("HTMLZeilenanalyse", "Zeilensegment ausgewählt: SUCCESS" + option);
                            String[] course = option.split("\"");// Log beim Anlegen DhbwMannheimCourse
                            tempDHBWCourses.add(new DhbwMannheimCourse(course[1], course[3]));
                        }
                        DHBWCoursecategory.add(new DhbwMannheimCategory(coursecategory[1],tempDHBWCourses));
                        tempDHBWCourses=new ArrayList<>();
                    }
                }

            }
        }
    }

    public List<DhbwMannheimCategory> getDHBWCourses() {
        return DHBWCoursecategory;
    }
}
