package com.management.student_center.dto.payroll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PayrollPaymentRequestDTO {
    private Integer paymentId;
    private BigDecimal paidAmount;          // Số tiền thanh toán
    private String paymentNote;              // Ghi chú thanh toán
    private LocalDate paymentDate;           // Ngày thanh toán (nếu null thì lấy ngày hiện tại)
    private List<Integer> detailIds;         // Danh sách ID chi tiết môn học được thanh toán (nếu thanh toán theo môn)
    private Boolean payAllDetails;           // True nếu thanh toán toàn bộ các môn còn lại

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPaymentNote() {
        return paymentNote;
    }

    public void setPaymentNote(String paymentNote) {
        this.paymentNote = paymentNote;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public List<Integer> getDetailIds() {
        return detailIds;
    }

    public void setDetailIds(List<Integer> detailIds) {
        this.detailIds = detailIds;
    }

    public Boolean getPayAllDetails() {
        return payAllDetails;
    }

    public void setPayAllDetails(Boolean payAllDetails) {
        this.payAllDetails = payAllDetails;
    }
}