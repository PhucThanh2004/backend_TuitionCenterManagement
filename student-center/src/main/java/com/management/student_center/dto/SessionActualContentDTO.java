package com.management.student_center.dto;


public class SessionActualContentDTO {
	private Boolean isFollowPlan = true;
    private Long plannedSessionDetailId; // nếu thay đổi kế hoạch
    private String actualTopic;
    private String actualContent;
    private String actualHomework;
    private String deviationReason;
    private String noteForNextSession;
	public Boolean getIsFollowPlan() {
		return isFollowPlan;
	}
	public void setIsFollowPlan(Boolean isFollowPlan) {
		this.isFollowPlan = isFollowPlan;
	}
	public Long getPlannedSessionDetailId() {
		return plannedSessionDetailId;
	}
	public void setPlannedSessionDetailId(Long plannedSessionDetailId) {
		this.plannedSessionDetailId = plannedSessionDetailId;
	}
	public String getActualTopic() {
		return actualTopic;
	}
	public void setActualTopic(String actualTopic) {
		this.actualTopic = actualTopic;
	}
	public String getActualContent() {
		return actualContent;
	}
	public void setActualContent(String actualContent) {
		this.actualContent = actualContent;
	}
	public String getActualHomework() {
		return actualHomework;
	}
	public void setActualHomework(String actualHomework) {
		this.actualHomework = actualHomework;
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