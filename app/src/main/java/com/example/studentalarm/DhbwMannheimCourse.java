package com.example.studentalarm;

import android.util.Log;

import androidx.annotation.NonNull;

public class DhbwMannheimCourse {
    private String CourseName;
    private String CourseISCID;
    public DhbwMannheimCourse(String CourseName, String CourseISCID){
        this.CourseName=CourseName;
        this.CourseISCID=CourseISCID;
        Log.d("Course", "Neu Angelegt Kursname:" + CourseName +" ISCKursID:"+CourseISCID);
    }
    public String getCourseID(){
        return this.CourseISCID;
    }
    public String getCourseName(){
        return this.CourseName;
    }

    @NonNull
    @Override
    public String toString() {
        return CourseName;
    }
}
