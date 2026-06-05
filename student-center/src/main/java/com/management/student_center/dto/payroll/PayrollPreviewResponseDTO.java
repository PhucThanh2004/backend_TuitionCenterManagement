package com.management.student_center.dto.payroll;

import java.math.BigDecimal;
import java.util.List;

public class PayrollPreviewResponseDTO {

	private Long teacherId;

	private String teacherName;

	private Integer month;

	private Integer year;

	private Integer totalSessions;

	private BigDecimal totalAmount;

	private List<PayrollSessionDetailDTO> sessions;

	public PayrollPreviewResponseDTO() {
	}

	public Long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
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

	public Integer getTotalSessions() {
		return totalSessions;
	}

	public void setTotalSessions(Integer totalSessions) {
		this.totalSessions = totalSessions;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public List<PayrollSessionDetailDTO> getSessions() {
		return sessions;
	}

	public void setSessions(List<PayrollSessionDetailDTO> sessions) {
		this.sessions = sessions;
	}
}