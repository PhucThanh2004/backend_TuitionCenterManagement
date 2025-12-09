package com.management.student_center.dto.teachersubject;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TeacherSubjectResponseDTO {
    private Long id;
    private Long teacherId;
    private String teacherName;
    private String email;
    private LocalDate dateOfBirth;
    private String specialty;
    private String subjectName;
    private String grade;
    private String salaryRate; // Format string "xxx VNĐ/giờ"
    private String teacherAvatar; // URL ảnh
    private String createdAt;
    private Long subjectId;
    // Constructor rỗng
    public TeacherSubjectResponseDTO() {}

    // Getters & Setters... (Bạn tự generate nhé, rất đơn giản)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getSalaryRate() { return salaryRate; }
    public void setSalaryRate(String salaryRate) { this.salaryRate = salaryRate; }
    public String getTeacherAvatar() { return teacherAvatar; }
    public void setTeacherAvatar(String teacherAvatar) { this.teacherAvatar = teacherAvatar; }
    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
}