// SubjectDTO.java
package com.management.student_center.dto;

import java.math.BigDecimal;
import java.util.List;

public class SubjectDTO {
    private Long id;
    private String name;
    private String grade;
    private BigDecimal price;
    private String status;
    private Integer maxStudents;
    private Integer sessionsPerWeek;
    private String image;
    private String note;
    private Long currentStudents;
    private List<TeacherSubjectDTO> TeacherSubjects;
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
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getMaxStudents() {
		return maxStudents;
	}
	public void setMaxStudents(Integer maxStudents) {
		this.maxStudents = maxStudents;
	}
	public Integer getSessionsPerWeek() {
		return sessionsPerWeek;
	}
	public void setSessionsPerWeek(Integer sessionsPerWeek) {
		this.sessionsPerWeek = sessionsPerWeek;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Long getCurrentStudents() {
		return currentStudents;
	}
	public void setCurrentStudents(Long currentStudents) {
		this.currentStudents = currentStudents;
	}
	public List<TeacherSubjectDTO> getTeacherSubjects() {
		return TeacherSubjects;
	}
	public void setTeacherSubjects(List<TeacherSubjectDTO> teacherSubjects) {
		this.TeacherSubjects = teacherSubjects;
	}

    
}
