package com.example.studentalarm.imports.dhbwMannheim;

import android.content.Context;
import android.util.Log;

import com.example.studentalarm.imports.Import;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DhbwCourses {
    private static final String FILE_NAME = "DHBW_COURSES", LOG = "Dhbw_Courses";

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
        return Import.checkConnection(context, false) ? CourseImport.importCourse(context) : null;
    }

    static class SaveCourse implements Serializable {
        public List<CourseCategory> save;
    }
}