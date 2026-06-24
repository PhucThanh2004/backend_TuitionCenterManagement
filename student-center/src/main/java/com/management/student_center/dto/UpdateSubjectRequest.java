package com.management.student_center.dto;

import com.management.student_center.enums.BillingType;
import com.management.student_center.enums.PaymentPlanType;

public class UpdateSubjectRequest {
    private String name;
    private String grade;
    private Double price;
    private String status;
    private Integer maxStudents;
    private Integer sessionsPerWeek;
    private String note;
    private Long teacherId;
    private Double salaryRate;
    private Long subjectTypeId;

    private BillingType billingType;
    private PaymentPlanType paymentPlanType;
    private Integer installmentCount;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getMaxStudents() { return maxStudents; }
    public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }

    public Integer getSessionsPerWeek() { return sessionsPerWeek; }
    public void setSessionsPerWeek(Integer sessionsPerWeek) { this.sessionsPerWeek = sessionsPerWeek; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public Double getSalaryRate() { return salaryRate; }
    public void setSalaryRate(Double salaryRate) { this.salaryRate = salaryRate; }
    
    public Long getSubjectTypeId() { return subjectTypeId; }
    public void setSubjectTypeId(Long subjectTypeId) { this.subjectTypeId = subjectTypeId; }
    
    public BillingType getBillingType() { return billingType; }
    public void setBillingType(BillingType billingType) { this.billingType = billingType; }
    
    public PaymentPlanType getPaymentPlanType() { return paymentPlanType; }
    public void setPaymentPlanType(PaymentPlanType paymentPlanType) { this.paymentPlanType = paymentPlanType; }
    
    public Integer getInstallmentCount() { return installmentCount; }
    public void setInstallmentCount(Integer installmentCount) { this.installmentCount = installmentCount; }
}