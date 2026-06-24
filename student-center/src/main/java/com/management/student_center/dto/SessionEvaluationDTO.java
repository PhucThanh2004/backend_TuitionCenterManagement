package com.management.student_center.dto;

import java.time.LocalDateTime;

public class SessionEvaluationDTO {
    private Long sessionDetailId;  
    private Integer sessionNumber;
    private String topic;
    private Integer understandingLevel;
    private String completionStatus;
    private String teacherNotes;
    private Integer homeworkQuality;
    private Integer participationLevel;
    private LocalDateTime updatedAt; 
    
    // Constructor
    public SessionEvaluationDTO(Long sessionDetailId, Integer sessionNumber, String topic,
                                Integer understandingLevel, String completionStatus,
                                String teacherNotes, Integer homeworkQuality,
                                Integer participationLevel, LocalDateTime updatedAt) {
        this.sessionDetailId = sessionDetailId;
        this.sessionNumber = sessionNumber;
        this.topic = topic;
        this.understandingLevel = understandingLevel;
        this.completionStatus = completionStatus;
        this.teacherNotes = teacherNotes;
        this.homeworkQuality = homeworkQuality;
        this.participationLevel = participationLevel;
        this.updatedAt = updatedAt; 
    }

    public Long getSessionDetailId() {
        return sessionDetailId;
    }

    public void setSessionDetailId(Long sessionDetailId) {
        this.sessionDetailId = sessionDetailId;
    }

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

    // ✅ Đổi từ getLastUpdated() sang getUpdatedAt()
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}