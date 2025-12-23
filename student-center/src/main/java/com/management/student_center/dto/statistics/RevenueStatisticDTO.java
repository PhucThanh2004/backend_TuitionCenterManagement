package com.management.student_center.dto.statistics;

import java.math.BigDecimal;

public class RevenueStatisticDTO {
    private int month;
    private BigDecimal totalRevenue; // Thu từ học phí
    private BigDecimal totalExpense; // Chi lương giáo viên

    public RevenueStatisticDTO(int month, BigDecimal totalRevenue, BigDecimal totalExpense) {
        this.month = month;
        this.totalRevenue = totalRevenue;
        this.totalExpense = totalExpense;
    }

    // Getters and Setters
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getTotalExpense() { return totalExpense; }
    public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }
}