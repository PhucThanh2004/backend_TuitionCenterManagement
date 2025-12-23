package com.management.student_center.dto.tuition;

import java.math.BigDecimal;

public class TuitionDetailUpdateRequest {
    private Long detailId;
    private Integer attendedSessions; // Số buổi sửa lại
    private BigDecimal totalMoney;    // Số tiền sửa lại (cho phép override giá gốc)
    private String note;              // Ghi chú lý do sửa

    // Getters and Setters
    public Long getDetailId() { return detailId; }
    public void setDetailId(Long detailId) { this.detailId = detailId; }
    public Integer getAttendedSessions() { return attendedSessions; }
    public void setAttendedSessions(Integer attendedSessions) { this.attendedSessions = attendedSessions; }
    public BigDecimal getTotalMoney() { return totalMoney; }
    public void setTotalMoney(BigDecimal totalMoney) { this.totalMoney = totalMoney; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}