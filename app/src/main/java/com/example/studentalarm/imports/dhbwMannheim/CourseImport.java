package com.example.studentalarm.imports.dhbwMannheim;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.studentalarm.R;
import com.example.studentalarm.imports.Import;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CourseImport {
    private static final String FILE_NAME = "DHBW_COURSES", LOG = "Dhbw_Courses", LINK_TO_COURSE = "https://vorlesungsplan.dhbw-mannheim.de/ical.php";
    private static ArrayList<CourseCategory> dhbwCourseCategory;

    /**
     * load dhbwCourse from phone or internet
     *
     * @param context context of application
     * @return list of course categories
     */
    @Nullable
    public static List<CourseCategory> load(@NonNull Context context) {
        List<CourseCategory> erg = loadFromPhone(context);
        if (erg == null)
            erg = reloadFromInternet(context);
        return erg;
    }

    /**
     * load dhbwCourse from internet and save it
     *
     * @param context context of application
     * @return list of course categories
     */
    @Nullable
    public static List<CourseCategory> reloadFromInternet(@NonNull Context context) {
        List<CourseCategory> erg = loadFromInternet(context);
        if (erg != null)
            save(context, erg);
        return erg;
    }

    /**
     * saving the course categories
     *
     * @param context          context of application
     * @param courseCategories list to save
     */
    private static void save(@NonNull Context context, @NonNull List<CourseCategory> courseCategories) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            SaveCourse save = new SaveCourse();
            save.save = courseCategories;
            oos.writeObject(save);
            oos.close();
            fos.close();
            Log.d(LOG, "Save Course data: SUCCESS");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * load the data from phone
     *
     * @param context context of application
     * @return list of course categories
     */
    @Nullable
    private static List<CourseCategory> loadFromPhone(@NonNull Context context) {
        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<CourseCategory> courseCategories = ((SaveCourse) ois.readObject()).save;
            Log.d(LOG, "Load Course data: SUCCESS");
            fis.close();
            ois.close();
            return courseCategories;
        } catch (@NonNull IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * load dhbwCourse from internet
     *
     * @param context context of application
     * @return list of course categories
     */
    @Nullable
    private static List<CourseCategory> loadFromInternet(@NonNull Context context) {
        return Import.checkConnection(context, false) ? importCourse(context) : null;
    }

    /**
     * import the course and categories from the internet
     *
     * @param context context of application
     * @return list of all course categories
     */
    @Nullable
    private static List<CourseCategory> importCourse(@NonNull Context context) {
        dhbwCourseCategory = new ArrayList<>();
        String parse = Import.runSynchronous(LINK_TO_COURSE);
        if (parse != null)
            parse(parse);
        else
            Toast.makeText(context, context.getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
        return dhbwCourseCategory.equals(new ArrayList<>()) ? null : dhbwCourseCategory;
    }

    /**
     * parse string to course category and course
     *
     * @param courseFile string to parse
     */
    private static void parse(@NonNull String courseFile) {
        ArrayList<Course> tempDHBWCourses;
        Log.d("HTMLImport", "ICal Course Detail: SUCCESS " + courseFile);
        for (String import_row : courseFile.split("\\n")) {
            if (import_row.contains("<form id=\"class_form\" >")) {
                Log.d("HTMLAnalyse", "Search for important column: SUCCESS " + import_row);
                for (String opt_group : import_row.split("<optgroup")) {
                    String[] course_category = opt_group.split("\"");
                    tempDHBWCourses = new ArrayList<>(); //.clear doesn't work
                    if (!course_category[1].equals("class_form")) {
                        for (String option : opt_group.split("<option|>"))
                            if (option.contains("label") && option.contains("value")) {
                                Log.d("HTML column analyse ", "selected column segment: SUCCESS" + option);
                                String[] course = option.split("\"");
                                tempDHBWCourses.add(new Course(course[1], course[3]));
                            }
                        dhbwCourseCategory.add(new CourseCategory(course_category[1], tempDHBWCourses));
                    }
                }
                return;
            }
        }
    }

    static class SaveCourse implements Serializable {
        public List<CourseCategory> save;
    }
}