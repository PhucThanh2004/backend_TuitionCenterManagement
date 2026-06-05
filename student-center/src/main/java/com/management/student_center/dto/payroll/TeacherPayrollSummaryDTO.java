package com.management.student_center.dto.payroll;

import com.management.student_center.enums.TeacherPaymentStatus;

import java.math.BigDecimal;

public class TeacherPayrollSummaryDTO {

    private Integer paymentId;

    private Integer month;

    private Integer year;

    private BigDecimal amount;

    private Integer totalSessions;

    private TeacherPaymentStatus status;

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public TeacherPaymentStatus getStatus() {
        return status;
    }

    public void setStatus(TeacherPaymentStatus status) {
        this.status = status;
    }
}