package com.example.studentalarm.dhbw_mannheim;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.studentalarm.dhbw_mannheim.Course;

import java.util.List;

public class CourseCategory {

    private String CourseCategory;
    private List<Course> DHBWCoursesCategory;

    public CourseCategory(String CourseCategory, List<Course> Courses){
        Log.d("CourseCategory", "Neu Angelegt Courscategory:" + CourseCategory);
        this.CourseCategory=CourseCategory;
        this.DHBWCoursesCategory=Courses;
    }
    public String getCourseCategory() {
        return CourseCategory;
    }
    public void add(Course Course){
        DHBWCoursesCategory.add(Course);
    }

    public List<Course> getDHBWCourses() {
        return DHBWCoursesCategory;
    }
    @NonNull
    @Override
    public String toString() {
        return CourseCategory;
    }
}
