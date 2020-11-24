package com.example.studentalarm;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.List;

public class DhbwMannheimCategory {

    private String CourseCategory;
    private List<DhbwMannheimCourse> DHBWCoursesCategory;

    public DhbwMannheimCategory(String CourseCategory, List<DhbwMannheimCourse> Courses){
        this.CourseCategory=CourseCategory;
        this.DHBWCoursesCategory=Courses;
    }
    public String getCourseCategory() {
        return CourseCategory;
    }
    public void add(DhbwMannheimCourse Course){
        DHBWCoursesCategory.add(Course);
    }

    public List<DhbwMannheimCourse> getDHBWCourses() {
        return DHBWCoursesCategory;
    }
    @NonNull
    @Override
    public String toString() {
        return CourseCategory;
    }
}
