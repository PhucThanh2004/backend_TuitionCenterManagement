package com.management.student_center.dto.tuition;

import java.math.BigDecimal;

public class TuitionPaymentRequest {

	private Long tuitionId;

	private BigDecimal amount;

	private String paymentMethod;

	private String note;

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

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}