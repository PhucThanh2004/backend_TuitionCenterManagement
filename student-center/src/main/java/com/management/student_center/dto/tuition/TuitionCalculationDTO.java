package com.management.student_center.dto.tuition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TuitionCalculationDTO {
	
	private Long tuitionId;
    private Long studentId;
    
    private String fullName;
    private String phoneNumber;
    private String grade;     
    private String status;     

    // --- Các trường tiền tệ ---
    private BigDecimal totalAmount; 
    private BigDecimal paidAmount;     
    private BigDecimal remainingAmount;
    
    // Danh sách chi tiết từng môn
    private List<SubjectTuitionDTO> subjects = new ArrayList<>();

    // --- Getters & Setters ---
    public Long getTuitionId() { return tuitionId; }
    public void setTuitionId(Long tuitionId) { this.tuitionId = tuitionId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }

    public List<SubjectTuitionDTO> getSubjects() { return subjects; }
    public void setSubjects(List<SubjectTuitionDTO> subjects) { this.subjects = subjects; }

    // Class con lưu chi tiết từng môn
    public static class SubjectTuitionDTO {
        private Long subjectId;
        private String subjectName;
        private BigDecimal hourlyRate; // Giá 1 giờ
        private int totalSessions;     // Tổng số buổi
        private float totalHours;      // Tổng số giờ
        private BigDecimal totalMoney; // Thành tiền

        // --- Getters & Setters ---
        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
        public BigDecimal getHourlyRate() { return hourlyRate; }
        public void setHourlyRate(BigDecimal hourlyRate) { this.hourlyRate = hourlyRate; }
        public int getTotalSessions() { return totalSessions; }
        public void setTotalSessions(int totalSessions) { this.totalSessions = totalSessions; }
        public float getTotalHours() { return totalHours; }
        public void setTotalHours(float totalHours) { this.totalHours = totalHours; }
        public BigDecimal getTotalMoney() { return totalMoney; }
        public void setTotalMoney(BigDecimal totalMoney) { this.totalMoney = totalMoney; }
    }
}