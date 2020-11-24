package com.example.studentalarm;

import android.util.Log;

import androidx.annotation.NonNull;

public class DhbwMannheimCourse {
    private String CourseCategory;
    private String CourseName;
    private String CourseISCID;
    public DhbwMannheimCourse(String CourseCategory, String CourseName, String CourseISCID){
        this.CourseCategory=CourseCategory;
        this.CourseName=CourseName;
        this.CourseISCID=CourseISCID;
        Log.d("Course", "Neu Angelegt Kategorie: "+CourseCategory+ " Kursname:" + CourseName +" ISCKursID:"+CourseISCID);
    }
    public String getCourseID(){
        return this.CourseISCID;
    }
    public String getCourseName(){
        return this.CourseName;
    }
    public String getCourseCategory() {
        return CourseCategory;
    }

    @NonNull
    @Override
    public String toString() {
        return CourseName;
    }
}
