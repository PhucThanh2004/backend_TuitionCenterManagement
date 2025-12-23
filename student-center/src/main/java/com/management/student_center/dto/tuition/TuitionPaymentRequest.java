package com.management.student_center.dto.tuition;

import java.math.BigDecimal;

public class TuitionPaymentRequest {
	
	private Long tuitionId;       // ID của hóa đơn cần thanh toán
    private BigDecimal amount;
    
    private Long studentId;
    private int month;
    private int year;

    // Constructor mặc định (Bắt buộc để Jackson deserialize JSON)
    public TuitionPaymentRequest() {
    }

    // Constructor đầy đủ (Tùy chọn, tiện cho test)
    public TuitionPaymentRequest(Long studentId, int month, int year) {
        this.studentId = studentId;
        this.month = month;
        this.year = year;
    }

    // Getters & Setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    
    public Long getTuitionId() {
        return tuitionId;
    }

    public void setTuitionId(Long tuitionId) {
        this.tuitionId = tuitionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}