package com.example.studentalarm.imports.dhbwMannheim;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

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
