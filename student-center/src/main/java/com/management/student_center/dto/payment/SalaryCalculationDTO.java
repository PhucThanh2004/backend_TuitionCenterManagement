package com.management.student_center.dto.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SalaryCalculationDTO {
    private Long teacherId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private BigDecimal totalAmount; // Tổng lương
    private List<SubjectSalaryDTO> subjects = new ArrayList<>();

    // Getters & Setters
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public List<SubjectSalaryDTO> getSubjects() { return subjects; }
    public void setSubjects(List<SubjectSalaryDTO> subjects) { this.subjects = subjects; }

    public static class SubjectSalaryDTO {
        private Long subjectId;
        private String subjectName;
        private Float salaryRate;
        private int totalSessions;
        private Float totalHours;
        private Float totalMoney;

        // Getters & Setters
        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
        public Float getSalaryRate() { return salaryRate; }
        public void setSalaryRate(Float salaryRate) { this.salaryRate = salaryRate; }
        public int getTotalSessions() { return totalSessions; }
        public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }
        public Float getTotalHours() { return totalHours; }
        public void setTotalHours(Float totalHours) { this.totalHours = totalHours; }
        public Float getTotalMoney() { return totalMoney; }
        public void setTotalMoney(Float totalMoney) { this.totalMoney = totalMoney; }
    }
}