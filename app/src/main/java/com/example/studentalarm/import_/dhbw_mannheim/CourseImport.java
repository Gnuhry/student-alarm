package com.example.studentalarm.import_.dhbw_mannheim;

import android.util.Log;

import com.example.studentalarm.import_.Import;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;


public class CourseImport {

    @NonNull
    private final List<CourseCategory> DHBWCourseCategory;
    private List<Course> tempDHBWCourses;
    private static final String link_to_course = "https://vorlesungsplan.dhbw-mannheim.de/ical.php";

    public CourseImport() {
        DHBWCourseCategory = new ArrayList<>();
        tempDHBWCourses = new ArrayList<>();
        parse(Import.runSynchronous(link_to_course));
    }

    private void parse(@NonNull String CourseFile) {
        Log.d("HTMLImport", "ICal Kurs Detail: SUCCESS " + CourseFile);
        for (String import_row : CourseFile.split("\\n"))
            if (import_row.contains("<form id=\"class_form\" >")) {
                Log.d("HTMLAnalyse", "Relevante Zeile suchen: SUCCESS " + import_row);
                for (String optgroup : import_row.split("<optgroup")) {
                    String[] coursecategory = optgroup.split("\"");// speichert zusätzlichen Array um Category herauszufinden
                    tempDHBWCourses = new ArrayList<>(); //.clear funktioniert hier nicht
                    if (!coursecategory[1].equals("class_form")) {
                        for (String option : optgroup.split("<option|>"))
                            if (option.contains("label") && option.contains("value")) {
                                Log.d("HTMLZeilenanalyse", "Zeilensegment ausgewählt: SUCCESS" + option);
                                String[] course = option.split("\"");// Log beim Anlegen DhbwMannheimCourse
                                tempDHBWCourses.add(new Course(course[1], course[3]));
                            }
                        DHBWCourseCategory.add(new CourseCategory(coursecategory[1], tempDHBWCourses));
                    }
                }

            }
    }

    @NonNull
    public List<CourseCategory> getDHBWCourses() {
        return DHBWCourseCategory;
    }
}
