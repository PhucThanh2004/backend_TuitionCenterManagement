package com.management.student_center.dto.subject;

import com.management.student_center.dto.AcademicLevelDTO;

public class SubjectTypeDTO {
    private Long id;
    private String name;
    private AcademicLevelDTO academicLevel;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public AcademicLevelDTO getAcademicLevel() {
		return academicLevel;
	}
	public void setAcademicLevel(AcademicLevelDTO academicLevel) {
		this.academicLevel = academicLevel;
	}

    
}