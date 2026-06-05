package com.management.student_center.dto;

import java.time.LocalDateTime;
import java.util.List;

// Request DTO
public class CurriculumRequestDTO {
    private String title;
    private String description;
    private Integer orderIndex;
    private Integer expectedSessions;
    private String status;
    private String semester;
    private String schoolYear;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getOrderIndex() {
		return orderIndex;
	}
	public void setOrderIndex(Integer orderIndex) {
		this.orderIndex = orderIndex;
	}
	public Integer getExpectedSessions() {
		return expectedSessions;
	}
	public void setExpectedSessions(Integer expectedSessions) {
		this.expectedSessions = expectedSessions;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSemester() {
		return semester;
	}
	public void setSemester(String semester) {
		this.semester = semester;
	}
	public String getSchoolYear() {
		return schoolYear;
	}
	public void setSchoolYear(String schoolYear) {
		this.schoolYear = schoolYear;
	}
}


