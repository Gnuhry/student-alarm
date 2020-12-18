package com.example.studentalarm.imports.dhbwMannheim;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.studentalarm.R;
import com.example.studentalarm.imports.Import;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;


public class CourseImport {

    private static final String LINK_TO_COURSE = "https://vorlesungsplan.dhbw-mannheim.de/ical.php";
    private static ArrayList<CourseCategory> dhbwCourseCategory;
    private static ArrayList<Course> tempDHBWCourses;

    public static List<CourseCategory> impcourse(@NonNull Context context) {
        dhbwCourseCategory = new ArrayList<>();
        tempDHBWCourses = new ArrayList<>();
        String parse = Import.runSynchronous(LINK_TO_COURSE);
        if (parse != null)
            parse(parse);
        else {
            Toast.makeText(context, context.getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
        }
        return dhbwCourseCategory;
    }

    /**
     * parse string to course category and course
     *
     * @param courseFile string to parse
     */
    private static void parse(@NonNull String courseFile) {
        Log.d("HTMLImport", "ICal Kurs Detail: SUCCESS " + courseFile);
        for (String import_row : courseFile.split("\\n")) {
            if (import_row.contains("<form id=\"class_form\" >")) {
                Log.d("HTMLAnalyse", "Relevante Zeile suchen: SUCCESS " + import_row);
                for (String opt_group : import_row.split("<optgroup")) {
                    String[] course_category = opt_group.split("\"");// speichert zusätzlichen Array um Category herauszufinden
                    tempDHBWCourses = new ArrayList<>(); //.clear funktioniert hier nicht
                    if (!course_category[1].equals("class_form")) {
                        for (String option : opt_group.split("<option|>"))
                            if (option.contains("label") && option.contains("value")) {
                                Log.d("HTMLZeilenanalyse", "Zeilensegment ausgewählt: SUCCESS" + option);
                                String[] course = option.split("\"");// Log beim Anlegen DhbwMannheimCourse
                                tempDHBWCourses.add(new Course(course[1], course[3]));
                            }
                        dhbwCourseCategory.add(new CourseCategory(course_category[1], tempDHBWCourses));
                    }
                }

            }
        }
    }
}
