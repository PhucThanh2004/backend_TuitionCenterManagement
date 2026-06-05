package com.management.student_center.dto;

import java.time.LocalDateTime;

public class SessionDetailResponseDTO {
 private Long id;
 private Long curriculumId;
 private String curriculumTitle;
 private Integer sessionNumber;
 private Integer displayOrder;
 private String topic;
 private String objectives;
 private String content;
 private String homework;
 private String materials;
 private Integer durationMinutes;
 private String teachingMethod;
 private String assessmentCriteria;
 private LocalDateTime createdAt;
 private LocalDateTime updatedAt;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCurriculumId() {
		return curriculumId;
	}
	public void setCurriculumId(Long curriculumId) {
		this.curriculumId = curriculumId;
	}
	public String getCurriculumTitle() {
		return curriculumTitle;
	}
	public void setCurriculumTitle(String curriculumTitle) {
		this.curriculumTitle = curriculumTitle;
	}
	public Integer getSessionNumber() {
		return sessionNumber;
	}
	public void setSessionNumber(Integer sessionNumber) {
		this.sessionNumber = sessionNumber;
	}
	
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getObjectives() {
		return objectives;
	}
	public void setObjectives(String objectives) {
		this.objectives = objectives;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getHomework() {
		return homework;
	}
	public void setHomework(String homework) {
		this.homework = homework;
	}
	public String getMaterials() {
		return materials;
	}
	public void setMaterials(String materials) {
		this.materials = materials;
	}
	public Integer getDurationMinutes() {
		return durationMinutes;
	}
	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}
	public String getTeachingMethod() {
		return teachingMethod;
	}
	public void setTeachingMethod(String teachingMethod) {
		this.teachingMethod = teachingMethod;
	}
	public String getAssessmentCriteria() {
		return assessmentCriteria;
	}
	public void setAssessmentCriteria(String assessmentCriteria) {
		this.assessmentCriteria = assessmentCriteria;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}

