package com.management.student_center.dto.payroll;

public class TeacherPayrollAdjustmentDTO {

    private Integer paymentId;

    private String message;

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


}