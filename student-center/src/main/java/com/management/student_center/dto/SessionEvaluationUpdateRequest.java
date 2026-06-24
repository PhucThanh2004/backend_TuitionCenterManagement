package com.management.student_center.dto;

public class SessionEvaluationUpdateRequest {
    private Integer understandingLevel;
    private String completionStatus;
    private String teacherNotes;
    private Integer homeworkQuality;
    private Integer participationLevel;
	public Integer getUnderstandingLevel() {
		return understandingLevel;
	}
	public void setUnderstandingLevel(Integer understandingLevel) {
		this.understandingLevel = understandingLevel;
	}
	public String getCompletionStatus() {
		return completionStatus;
	}
	public void setCompletionStatus(String completionStatus) {
		this.completionStatus = completionStatus;
	}
	public String getTeacherNotes() {
		return teacherNotes;
	}
	public void setTeacherNotes(String teacherNotes) {
		this.teacherNotes = teacherNotes;
	}
	public Integer getHomeworkQuality() {
		return homeworkQuality;
	}
	public void setHomeworkQuality(Integer homeworkQuality) {
		this.homeworkQuality = homeworkQuality;
	}
	public Integer getParticipationLevel() {
		return participationLevel;
	}
	public void setParticipationLevel(Integer participationLevel) {
		this.participationLevel = participationLevel;
	}
    
}