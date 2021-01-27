package com.example.studentalarm.imports.dhbwMannheim;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class Course implements Comparable<String>, Serializable {
    private final String courseName, courseISCID;

    /**
     * Course initialisation
     *
     * @param CourseName  name of the Course
     * @param CourseISCID necessary ID to load the ISC of this Course
     */
    public Course(String CourseName, String CourseISCID) {
        this.courseName = CourseName;
        this.courseISCID = CourseISCID;
    }

    /**
     * @return string with necessary ID to load the ISC of this Course
     */
    public String getCourseID() {
        return this.courseISCID;
    }

    /**
     * @return string with name of the Course
     */
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
