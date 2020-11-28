package com.example.studentalarm.imports.dhbwMannheim;

import java.util.List;

import androidx.annotation.NonNull;

public class CourseCategory implements Comparable<String> {
    private final String courseCategory;
    private final List<Course> courses;

    public CourseCategory(String CourseCategory, List<Course> Courses) {
        this.courseCategory = CourseCategory;
        this.courses = Courses;
    }

    public String getCourseCategory() {
        return courseCategory;
    }

    public List<Course> getCourses() {
        return courses;
    }

    @NonNull
    @Override
    public String toString() {
        return courseCategory;
    }

    @Override
    public int compareTo(@NonNull String o) {
        return courseCategory.compareTo(o);
    }
}
