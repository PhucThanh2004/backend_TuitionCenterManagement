package com.management.student_center.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateSubjectRequest {

    private String name;
    private String grade;
    private Double price;
    private String status;
    private String maxStudents;
    private String sessionsPerWeek;
    private String note;
    private Long teacherId;
    private Long subjectTypeId;

    private MultipartFile image;
    private String imageUrl;

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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMaxStudents() {
		return maxStudents;
	}

	public void setMaxStudents(String maxStudents) {
		this.maxStudents = maxStudents;
	}

	public String getSessionsPerWeek() {
		return sessionsPerWeek;
	}

	public void setSessionsPerWeek(String sessionsPerWeek) {
		this.sessionsPerWeek = sessionsPerWeek;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}

	public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}
	
	public String getImageUrl() {
	    return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
	    this.imageUrl = imageUrl;
	}

	public Long getSubjectTypeId() {
		return subjectTypeId;
	}

	public void setSubjectTypeId(Long subjectTypeId) {
		this.subjectTypeId = subjectTypeId;
	}
    
}

