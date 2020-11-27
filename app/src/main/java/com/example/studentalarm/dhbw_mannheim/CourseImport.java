package com.example.studentalarm.dhbw_mannheim;

import android.util.Log;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.example.studentalarm.R;
import androidx.preference.Preference;



public class CourseImport {

    private List<CourseCategory> DHBWCoursecategory;
    private List<Course> tempDHBWCourses;
    private final OkHttpClient client = new OkHttpClient();
    private boolean successful=false;

    public CourseImport() {//muss in neuem Thread aufgerufen werden new Thread(() -> {CourseNamesDHBW_Mannheim test = new CourseNamesDHBW_Mannheim();}).start();
        Log.d("Info","Aufgerufen");
        DHBWCoursecategory=new ArrayList<>();
        tempDHBWCourses=new ArrayList<>();
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
                for (String optgroup: importrow.split("<optgroup")) {
                    String[] coursecategory = optgroup.split("\"");// speichert zusätzlichen Array um Category herauszufinden
                    tempDHBWCourses=new ArrayList<>(); //.clear funktioniert hier nicht
                    if (!coursecategory[1].equals("class_form")) {
                        for (String option : optgroup.split("<option|>")) {
                            if (option.contains("label") && option.contains("value")) {
                                Log.d("HTMLZeilenanalyse", "Zeilensegment ausgewählt: SUCCESS" + option);
                                String[] course = option.split("\"");// Log beim Anlegen DhbwMannheimCourse
                                tempDHBWCourses.add(new Course(course[1], course[3]));
                            }
                        }
                        DHBWCoursecategory.add(new CourseCategory(coursecategory[1],tempDHBWCourses));
                    }else{
                        DHBWCoursecategory.add(new CourseCategory("Course Category",tempDHBWCourses)); //muss noch als variabler zugriff realisiert werden
                    }
                }

            }
        }
    }

    public List<CourseCategory> getDHBWCourses() {
        return DHBWCoursecategory;
    }
}
