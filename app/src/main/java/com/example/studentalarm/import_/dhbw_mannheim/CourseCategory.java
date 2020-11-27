package com.example.studentalarm.import_.dhbw_mannheim;

import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;

public class CourseCategory implements Comparable<String> {
    private final String CourseCategory;
    private final List<Course> DHBWCoursesCategory;

    public CourseCategory(String CourseCategory, List<Course> Courses) {
        this.CourseCategory = CourseCategory;
        this.DHBWCoursesCategory = Courses;
        Log.d("CourseCategory", "Neu Angelegt Courscategory:" + CourseCategory);
    }

    public String getCourseCategory() {
        return CourseCategory;
    }

    public List<Course> getDHBWCourses() {
        return DHBWCoursesCategory;
    }

    @NonNull
    @Override
    public String toString() {
        return CourseCategory;
    }

    @Override
    public int compareTo(@NonNull String o) {
        return CourseCategory.compareTo(o);
    }
}
