package com.example.studentalarm.imports.dhbwMannheim;

import androidx.annotation.NonNull;

public class Course implements Comparable<String> {
    private final String courseName;
    private final String courseISCID;

    public Course(String CourseName, String CourseISCID) {
        this.courseName = CourseName;
        this.courseISCID = CourseISCID;
    }

    public String getCourseID() {
        return this.courseISCID;
    }

    public String getCourseName() {
        return courseName;
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
