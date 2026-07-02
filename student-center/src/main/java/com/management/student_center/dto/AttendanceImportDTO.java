package com.management.student_center.dto;

import java.time.LocalDate;

public class AttendanceImportDTO {
    private Long studentId;
    private Long subjectId;      
    private LocalDate sessionDate;
    private String status;
    private String note;
    
    public AttendanceImportDTO() {}
    
    public AttendanceImportDTO(Long studentId, Long subjectId, LocalDate sessionDate, 
                               String status, String note) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.sessionDate = sessionDate;
        this.status = status;
        this.note = note;
    }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}