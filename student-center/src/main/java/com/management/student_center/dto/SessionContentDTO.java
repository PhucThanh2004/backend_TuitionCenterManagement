package com.management.student_center.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class SessionContentDTO {
    private Long sessionId;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    
    // Nội dung hiển thị (thực tế hoặc kế hoạch)
    private String displayTopic;
    private String displayContent;
    private String displayHomework;
    
    private Boolean isFollowingPlan;
    private String plannedTopic; // để so sánh
    private String deviationReason;
    private String noteForNextSession;
	public Long getSessionId() {
		return sessionId;
	}
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
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
	public String getDisplayTopic() {
		return displayTopic;
	}
	public void setDisplayTopic(String displayTopic) {
		this.displayTopic = displayTopic;
	}
	public String getDisplayContent() {
		return displayContent;
	}
	public void setDisplayContent(String displayContent) {
		this.displayContent = displayContent;
	}
	public String getDisplayHomework() {
		return displayHomework;
	}
	public void setDisplayHomework(String displayHomework) {
		this.displayHomework = displayHomework;
	}
	public Boolean getIsFollowingPlan() {
		return isFollowingPlan;
	}
	public void setIsFollowingPlan(Boolean isFollowingPlan) {
		this.isFollowingPlan = isFollowingPlan;
	}
	public String getPlannedTopic() {
		return plannedTopic;
	}
	public void setPlannedTopic(String plannedTopic) {
		this.plannedTopic = plannedTopic;
	}
	public String getDeviationReason() {
		return deviationReason;
	}
	public void setDeviationReason(String deviationReason) {
		this.deviationReason = deviationReason;
	}
	public String getNoteForNextSession() {
		return noteForNextSession;
	}
	public void setNoteForNextSession(String noteForNextSession) {
		this.noteForNextSession = noteForNextSession;
	}
    
    
}