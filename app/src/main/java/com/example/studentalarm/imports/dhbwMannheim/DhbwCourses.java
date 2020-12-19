package com.example.studentalarm.imports.dhbwMannheim;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.studentalarm.imports.Import;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
/*
public class DhbwCourses implements Serializable {

    private final String FILENAME ="DHBWCOURSES";
    private List<CourseCategory> courseCategorys;

    public DhbwCourses(){}

    public DhbwCourses(List<CourseCategory> courseCategorys){
        this.courseCategorys=courseCategorys;
    }

    public List<CourseCategory> getCourseCategorys() {
        return courseCategorys;
    }

    public void save (@NonNull Context context){
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
            Log.d("SAVE", "Save Coursedata in DHBWCOURSES: SUCCESS");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load (@NonNull Context context) {
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.courseCategorys = ((DhbwCourses) ois.readObject()).getCourseCategorys();
            Log.d("LOAD", "Loaded Coursedata from " + FILENAME + ": SUCCESS");
            fis.close();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
*/

public class DhbwCourses{
    private static final String FILENAME ="DHBWCOURSES";
    private static void save (@NonNull Context context,@NonNull List<CourseCategory> courseCatergories){
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            SaveCourse save=new SaveCourse();
            save.save=courseCatergories;
            oos.writeObject(save);
            oos.close();
            fos.close();
            Log.d("SAVE", "Save Coursedata in DHBWCOURSES: SUCCESS");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static List<CourseCategory> loadFromPhone (@NonNull Context context) {
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<CourseCategory> courseCategories = ((SaveCourse) ois.readObject()).save;
            Log.d("LOAD", "Loaded Coursedata from " + FILENAME + ": SUCCESS");
            fis.close();
            ois.close();
            return courseCategories;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<CourseCategory> load (@NonNull Context context){
        List<CourseCategory> erg=loadFromPhone(context);
        if(erg==null){
            erg=reloadFromInternet(context);
        }
        return erg;
    }

    public static List<CourseCategory> reloadFromInternet (@NonNull Context context){
        List<CourseCategory> erg = loadFromInternet(context);
            if(erg!=null)
                save(context, erg);
        return erg;
    }

    private static List<CourseCategory> loadFromInternet(@NonNull Context context){
        if (Import.checkConnection(context,false)) {
            Log.i("Courses", "Import startet");
            return CourseImport.impcourse(context);
        }else{return null;}
    }

    static class SaveCourse implements Serializable {
        public List<CourseCategory>save;
    }
}
/*String[] files = getContext().fileList();
            List<String> list = Arrays.asList(files);
            Log.d("Check", "Is DHBWCOURSES in: "+list);
            if (!list.contains("DHBWCOURSES")){
                Log.i(LOG, "Import from WEB");
                dhbwCourseImport();
            }else{
                Log.i(LOG, "Import from DHBWCOURSES");
                dhbwCourses.load(getContext());
                if (dhbwCourses==null||dhbwCourses.getCourseCategorys()==null){
                    dhbwCourseImport();
                }
            }
            if (dhbwCourses==null||dhbwCourses.getCourseCategorys()==null)
                return;


                private void dhbwCourseImport() {

    }
 */