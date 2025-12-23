package com.management.student_center.dto;

import java.time.LocalDate;

public class AttendanceStudentDTO {

    private Long studentId;
    private String fullName;
    private String email;
    private String grade;
    private String schoolName;
    private String status;       // absent | late
    private String note;
    private LocalDate sessionDate;
    private Long subjectId;
    private String subjectName;  // Tên lớp / môn học

    public AttendanceStudentDTO(Long studentId, String fullName, String email,
                                String grade, String schoolName, String status,
                                String note, LocalDate sessionDate,
                                Long subjectId, String subjectName) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.grade = grade;
        this.schoolName = schoolName;
        this.status = status;
        this.note = note;
        this.sessionDate = sessionDate;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
    }

    // Getter & Setter
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
}
