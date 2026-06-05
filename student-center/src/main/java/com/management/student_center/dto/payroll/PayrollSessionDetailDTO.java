package com.management.student_center.dto.payroll;

import com.management.student_center.enums.AssignmentType;
import com.management.student_center.enums.SalaryType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class PayrollSessionDetailDTO {

	private Long sessionTeacherId;

	private Long sessionId;

	private String subjectName;

	private LocalDate sessionDate;

	private LocalTime startTime;

	private LocalTime endTime;

	private AssignmentType assignmentType;

	private SalaryType salaryType;

	private BigDecimal salaryRate;

	private BigDecimal workedHours;

	private BigDecimal amount;

	private Boolean replacement;

	private String note;

	public PayrollSessionDetailDTO() {
	}

	public Long getSessionTeacherId() {
		return sessionTeacherId;
	}

	public void setSessionTeacherId(Long sessionTeacherId) {
		this.sessionTeacherId = sessionTeacherId;
	}

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public LocalDate getSessionDate() {
		return sessionDate;
	}

	public void setSessionDate(LocalDate sessionDate) {
		this.sessionDate = sessionDate;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public AssignmentType getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(AssignmentType assignmentType) {
		this.assignmentType = assignmentType;
	}

	public SalaryType getSalaryType() {
		return salaryType;
	}

	public void setSalaryType(SalaryType salaryType) {
		this.salaryType = salaryType;
	}

	public BigDecimal getSalaryRate() {
		return salaryRate;
	}

	public void setSalaryRate(BigDecimal salaryRate) {
		this.salaryRate = salaryRate;
	}

	public BigDecimal getWorkedHours() {
		return workedHours;
	}

	public void setWorkedHours(BigDecimal workedHours) {
		this.workedHours = workedHours;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Boolean getReplacement() {
		return replacement;
	}

	public void setReplacement(Boolean replacement) {
		this.replacement = replacement;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}