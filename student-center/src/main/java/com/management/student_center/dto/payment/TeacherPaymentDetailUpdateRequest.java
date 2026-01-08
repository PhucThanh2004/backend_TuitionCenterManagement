package com.management.student_center.dto.payment;

import java.math.BigDecimal;

public class TeacherPaymentDetailUpdateRequest {
    private Long detailId;
    private Integer totalSessions; // Số buổi mới
    private BigDecimal totalMoney; // Số tiền mới (Optional - manual override)
    private BigDecimal bonus;
    private String note;

    // Getter & Setter
    public Long getDetailId() { return detailId; }
    public void setDetailId(Long detailId) { this.detailId = detailId; }
    public Integer getTotalSessions() { return totalSessions; }
    public void setTotalSessions(Integer totalSessions) { this.totalSessions = totalSessions; }
    public BigDecimal getTotalMoney() { return totalMoney; }
    public void setTotalMoney(BigDecimal totalMoney) { this.totalMoney = totalMoney; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
	public BigDecimal getBonus() {
		return bonus;
	}
	public void setBonus(BigDecimal bonus) {
		this.bonus = bonus;
	}
}