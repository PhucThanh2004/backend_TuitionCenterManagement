package com.management.student_center.dto.payroll;

import java.math.BigDecimal;

public class PayrollSummaryDTO {

    private Integer totalTeachers;

    private Integer totalSessions;

    private BigDecimal totalAmount;

    public PayrollSummaryDTO() {
    }

    public Integer getTotalTeachers() {
        return totalTeachers;
    }

    public void setTotalTeachers(Integer totalTeachers) {
        this.totalTeachers = totalTeachers;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}