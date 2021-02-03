package com.example.studentalarm.imports.dhbwMannheim;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class Course implements Comparable<String>, Serializable {
    private final String courseName, courseISCID;

    public Course(String CourseName, String CourseISCID) {
        this.courseName = CourseName;
        this.courseISCID = CourseISCID;
    }

    public String getCourseID() {
        return this.courseISCID;
    }

    public String getCourseName() {
        return this.courseName;
    }

    @NonNull
    @Override
    public String toString() {
        return courseName;
    }

    @Override
    public int compareTo(@NonNull String o) {
        return courseName.compareTo(o);
    }
}
