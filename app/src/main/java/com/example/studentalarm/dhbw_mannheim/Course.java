package com.example.studentalarm.dhbw_mannheim;

import android.util.Log;

import androidx.annotation.NonNull;

public class Course {
    private final String CourseName;
    private final String CourseISCID;
    public Course(String CourseName, String CourseISCID){
        this.CourseName=CourseName;
        this.CourseISCID=CourseISCID;
        Log.d("Course", "Neu Angelegt Kursname:" + CourseName +" ISCKursID:"+CourseISCID);
    }
    public String getCourseID(){
        return this.CourseISCID;
    }

    @NonNull
    @Override
    public String toString() {
        return CourseName;
    }
}
