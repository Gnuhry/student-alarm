package com.example.studentalarm.imports.dhbwMannheim;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.NonNull;

public class CourseCategory implements Comparable<String>, Serializable {
    private final String courseCategory;
    private final List<Course> courses;

    /**
     * CourseCategory initialisation
     *
     * @param CourseCategory name of the Course Category
     * @param Courses        a List of Courses in the Category
     */
    public CourseCategory(String CourseCategory, List<Course> Courses) {
        this.courseCategory = CourseCategory;
        this.courses = Courses;
    }

    /**
     * @return string with name of the Course Category
     */
    public String getCourseCategory() {
        return courseCategory;
    }

    /**
     * @return List<Course> (a List of Courses in the Category)
     */
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
