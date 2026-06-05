package com.management.student_center.dto;

import java.time.LocalDateTime;

// Request DTO
public class SessionDetailRequestDTO {
    private Integer sessionNumber;
    private String topic;
    private String objectives;
    private String content;
    private String homework;
    private String materials;
    private Integer durationMinutes;
    private String teachingMethod;
    private String assessmentCriteria;
	public Integer getSessionNumber() {
		return sessionNumber;
	}
	public void setSessionNumber(Integer sessionNumber) {
		this.sessionNumber = sessionNumber;
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
}

