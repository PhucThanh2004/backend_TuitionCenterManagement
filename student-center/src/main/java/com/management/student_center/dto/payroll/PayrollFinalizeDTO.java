package com.management.student_center.dto.payroll;

public class PayrollFinalizeDTO {

	private Integer paymentId;

	private String payrollNote;

	public PayrollFinalizeDTO() {
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public String getPayrollNote() {
		return payrollNote;
	}

	public void setPayrollNote(String payrollNote) {
		this.payrollNote = payrollNote;
	}
}