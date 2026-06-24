package com.management.student_center.dto.payroll;

public class TeacherPayrollRejectDTO {
    private Integer paymentId;
    private String reason;
	public Integer getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
}