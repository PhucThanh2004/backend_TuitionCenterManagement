package com.management.student_center.dto.statistics;

import java.math.BigDecimal;

public class SubjectRevenueDTO {
    private String subjectName;
    private BigDecimal totalRevenue;
    private BigDecimal totalExpense;

    public SubjectRevenueDTO(String subjectName, BigDecimal totalRevenue, BigDecimal totalExpense) {
        this.subjectName = subjectName;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.totalExpense = totalExpense != null ? totalExpense : BigDecimal.ZERO;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public BigDecimal getProfit() {
        return totalRevenue.subtract(totalExpense);
    }
}