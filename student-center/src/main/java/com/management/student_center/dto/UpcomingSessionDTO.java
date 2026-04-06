package com.management.student_center.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class UpcomingSessionDTO {

    private String subjectName;
    private String grade;
    private String teacherName;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public UpcomingSessionDTO(String subjectName, String grade, String teacherName,
                              LocalDate sessionDate, LocalTime startTime, LocalTime endTime) {
        this.subjectName = subjectName;
        this.grade = grade;
        this.teacherName = teacherName;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getSubjectName() { return subjectName; }
    public String getGrade() { return grade; }
    public String getTeacherName() { return teacherName; }
    public LocalDate getSessionDate() { return sessionDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
}