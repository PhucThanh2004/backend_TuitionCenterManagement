package com.management.student_center.dto.payroll;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.management.student_center.enums.TeacherPaymentStatus;

public class PayrollListItemDTO {

	private Integer paymentId;

    private Long teacherId;

    public Long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}

	private String teacherName;

    private Integer month;

    private Integer year;

    private BigDecimal amount;

    private Integer totalSessions;

    private TeacherPaymentStatus status;

    private LocalDate paymentDate;

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}


	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getTotalSessions() {
		return totalSessions;
	}

	public void setTotalSessions(Integer totalSessions) {
		this.totalSessions = totalSessions;
	}

	public TeacherPaymentStatus getStatus() {
		return status;
	}

	public void setStatus(TeacherPaymentStatus status) {
		this.status = status;
	}

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}
}
