package com.management.student_center.dto.teacher;
import java.util.List;

public class WeeklyAbsentTeacherResponse {

    private long totalAbsentTeachers;

    private List<TeacherAbsentResponse> teachers;

    public WeeklyAbsentTeacherResponse(long totalAbsentTeachers,
                                       List<TeacherAbsentResponse> teachers) {
        this.totalAbsentTeachers = totalAbsentTeachers;
        this.teachers = teachers;
    }

    public WeeklyAbsentTeacherResponse() {
    }

    // Getter & Setter

    public long getTotalAbsentTeachers() {
        return totalAbsentTeachers;
    }

    public void setTotalAbsentTeachers(long totalAbsentTeachers) {
        this.totalAbsentTeachers = totalAbsentTeachers;
    }

    public List<TeacherAbsentResponse> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeacherAbsentResponse> teachers) {
        this.teachers = teachers;
    }
}