package com.management.student_center.dto.teachersubject;

import java.math.BigDecimal;

public class TeacherSubjectRequestDTO {
    private Long teacherId;
    private Long subjectId;
    private BigDecimal salaryRate;

    // Getters & Setters
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public BigDecimal getSalaryRate() { return salaryRate; }
    public void setSalaryRate(BigDecimal salaryRate) { this.salaryRate = salaryRate; }
}