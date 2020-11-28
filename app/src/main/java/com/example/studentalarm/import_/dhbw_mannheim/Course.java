package com.example.studentalarm.import_.dhbw_mannheim;

import androidx.annotation.NonNull;

public class Course implements Comparable<String> {
    private final String CourseName;
    private final String CourseISCID;

    public Course(String CourseName, String CourseISCID) {
        this.CourseName = CourseName;
        this.CourseISCID = CourseISCID;
    }

    public String getCourseID() {
        return this.CourseISCID;
    }

    public String getCourseName() {
        return CourseName;
    }

    @NonNull
    @Override
    public String toString() {
        return CourseName;
    }

    @Override
    public int compareTo(@NonNull String o) {
        return CourseName.compareTo(o);
    }
}
