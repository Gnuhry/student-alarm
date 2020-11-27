package com.example.studentalarm.import_.dhbw_mannheim;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class CourseImport {

    @NonNull
    private final List<CourseCategory> DHBWCourseCategory;
    private List<Course> tempDHBWCourses;
    private static final String link_to_course = "https://vorlesungsplan.dhbw-mannheim.de/ical.php";

    public CourseImport() {//muss in neuem Thread aufgerufen werden new Thread(() -> {CourseNamesDHBW_Mannheim test = new CourseNamesDHBW_Mannheim();}).start();
        Log.d("Info", "Aufgerufen");
        DHBWCourseCategory = new ArrayList<>();
        tempDHBWCourses = new ArrayList<>();
        runSynchronous();
    }

    public void runSynchronous() {
        Request request = new Request.Builder()
                .url(link_to_course)
                .build();

        try (Response response = new OkHttpClient().newCall(request).execute()) {
            if (!response.isSuccessful())
                Log.e("ICS-Synchronous", "Unexpected code " + response);
            ResponseBody body = response.body();
            if (body != null)
                parse(body.string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parse(@NonNull String CourseFile) {
        Log.d("HTMLImport", "ICal Kurs Detail: SUCCESS " + CourseFile);
        for (String import_row : CourseFile.split("\\n")) {
            if (import_row.contains("<form id=\"class_form\" >")) {
                Log.d("HTMLAnalyse", "Relevante Zeile suchen: SUCCESS " + import_row);
                for (String optgroup : import_row.split("<optgroup")) {
                    String[] coursecategory = optgroup.split("\"");// speichert zusätzlichen Array um Category herauszufinden
                    tempDHBWCourses = new ArrayList<>(); //.clear funktioniert hier nicht
                    if (!coursecategory[1].equals("class_form")) {
                        for (String option : optgroup.split("<option|>")) {
                            if (option.contains("label") && option.contains("value")) {
                                Log.d("HTMLZeilenanalyse", "Zeilensegment ausgewählt: SUCCESS" + option);
                                String[] course = option.split("\"");// Log beim Anlegen DhbwMannheimCourse
                                tempDHBWCourses.add(new Course(course[1], course[3]));
                            }
                        }
                        DHBWCourseCategory.add(new CourseCategory(coursecategory[1], tempDHBWCourses));
                    }
//                    else {
//                        DHBWCourseCategory.add(new CourseCategory("Course Category", tempDHBWCourses)); //muss noch als variabler zugriff realisiert werden
//                    }
                }

            }
        }
    }

    @NonNull
    public List<CourseCategory> getDHBWCourses() {
        return DHBWCourseCategory;
    }
}
