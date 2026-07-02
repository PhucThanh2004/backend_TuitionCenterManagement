package com.management.student_center.dto.payroll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PayrollPaymentResponseDTO {
    private Integer paymentId;
    private BigDecimal totalAmount;          // Tổng lương
    private BigDecimal paidAmount;           // Đã thanh toán
    private BigDecimal remainingAmount;      // Còn lại
    private String status;                   // PAID hoặc PARTIAL_PAID
    private LocalDate paymentDate;
    private String paymentNote;
    private List<PaymentDetailStatusDTO> detailStatuses;

    // Getters and Setters
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentNote() {
        return paymentNote;
    }

    public void setPaymentNote(String paymentNote) {
        this.paymentNote = paymentNote;
    }

    public List<PaymentDetailStatusDTO> getDetailStatuses() {
        return detailStatuses;
    }

    public void setDetailStatuses(List<PaymentDetailStatusDTO> detailStatuses) {
        this.detailStatuses = detailStatuses;
    }
}