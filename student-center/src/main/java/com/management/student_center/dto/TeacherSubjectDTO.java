// TeacherSubjectDTO.java
package com.management.student_center.dto;

import java.math.BigDecimal;

public class TeacherSubjectDTO {
    private BigDecimal salaryRate;
    private TeacherDTO Teacher;
	public BigDecimal getSalaryRate() {
		return salaryRate;
	}
	public void setSalaryRate(BigDecimal salaryRate) {
		this.salaryRate = salaryRate;
	}
	public TeacherDTO getTeacher() {
		return Teacher;
	}
	public void setTeacher(TeacherDTO teacher) {
		this.Teacher = teacher;
	}

    // getters & setters
    
}
