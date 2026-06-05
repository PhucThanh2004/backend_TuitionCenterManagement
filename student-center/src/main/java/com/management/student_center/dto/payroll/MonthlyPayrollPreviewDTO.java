package com.management.student_center.dto.payroll;


import java.math.BigDecimal;
import java.util.List;

public class MonthlyPayrollPreviewDTO {

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

	public Integer getTotalTeachers() {
		return totalTeachers;
	}

	public void setTotalTeachers(Integer totalTeachers) {
		this.totalTeachers = totalTeachers;
	}

	public BigDecimal getTotalPayrollAmount() {
		return totalPayrollAmount;
	}

	public void setTotalPayrollAmount(BigDecimal totalPayrollAmount) {
		this.totalPayrollAmount = totalPayrollAmount;
	}

	public List<MonthlyPayrollTeacherDTO> getTeachers() {
		return teachers;
	}

	public void setTeachers(List<MonthlyPayrollTeacherDTO> teachers) {
		this.teachers = teachers;
	}

	private Integer month;

    private Integer year;

    private Integer totalTeachers;

    private BigDecimal totalPayrollAmount;

    private List<MonthlyPayrollTeacherDTO> teachers;
}