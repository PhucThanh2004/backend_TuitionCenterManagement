package com.management.student_center.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class AttendanceExportDTO {
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String grade;
    private String schoolName;
    private String status;
    private String note;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String subjectName;
    private String subjectId;
    private String roomName;
    
    public AttendanceExportDTO() {}
    
    public AttendanceExportDTO(Long studentId, String studentName, String studentEmail, 
                               String grade, String schoolName, String status, String note,
                               LocalDate sessionDate, LocalTime startTime, LocalTime endTime,
                               String subjectName, String subjectId, String roomName) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.grade = grade;
        this.schoolName = schoolName;
        this.status = status;
        this.note = note;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subjectName = subjectName;
        this.subjectId = subjectId;
        this.roomName = roomName;
    }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
}