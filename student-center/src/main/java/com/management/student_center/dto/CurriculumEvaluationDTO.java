// 5. CurriculumEvaluationDTO.java
package com.management.student_center.dto;

import java.time.LocalDateTime;

public class CurriculumEvaluationDTO {
    private Long curriculumId;
    private String curriculumTitle;
    private Integer understandingLevel;
    private Integer overallProgress;
    private String teacherNotes;
    private String strengths;
    private String weaknesses;
    private String recommendations;
    private Double avgHomeworkQuality;
    private Integer completedSessions;
    private Integer totalSessions;
    private LocalDateTime lastUpdated;
    
    // Constructor
    public CurriculumEvaluationDTO(Long curriculumId, String curriculumTitle, 
                                   Integer understandingLevel, Integer overallProgress,
                                   String teacherNotes, String strengths, String weaknesses,
                                   String recommendations, Double avgHomeworkQuality,
                                   Integer completedSessions, Integer totalSessions,
                                   LocalDateTime lastUpdated) {
        this.curriculumId = curriculumId;
        this.curriculumTitle = curriculumTitle;
        this.understandingLevel = understandingLevel;
        this.overallProgress = overallProgress;
        this.teacherNotes = teacherNotes;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.recommendations = recommendations;
        this.avgHomeworkQuality = avgHomeworkQuality;
        this.completedSessions = completedSessions;
        this.totalSessions = totalSessions;
        this.lastUpdated = lastUpdated;
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

	public Integer getUnderstandingLevel() {
		return understandingLevel;
	}

	public void setUnderstandingLevel(Integer understandingLevel) {
		this.understandingLevel = understandingLevel;
	}

	public Integer getOverallProgress() {
		return overallProgress;
	}

	public void setOverallProgress(Integer overallProgress) {
		this.overallProgress = overallProgress;
	}

	public String getTeacherNotes() {
		return teacherNotes;
	}

	public void setTeacherNotes(String teacherNotes) {
		this.teacherNotes = teacherNotes;
	}

	public String getStrengths() {
		return strengths;
	}

	public void setStrengths(String strengths) {
		this.strengths = strengths;
	}

	public String getWeaknesses() {
		return weaknesses;
	}

	public void setWeaknesses(String weaknesses) {
		this.weaknesses = weaknesses;
	}

	public String getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(String recommendations) {
		this.recommendations = recommendations;
	}

	public Double getAvgHomeworkQuality() {
		return avgHomeworkQuality;
	}

	public void setAvgHomeworkQuality(Double avgHomeworkQuality) {
		this.avgHomeworkQuality = avgHomeworkQuality;
	}

	public Integer getCompletedSessions() {
		return completedSessions;
	}

	public void setCompletedSessions(Integer completedSessions) {
		this.completedSessions = completedSessions;
	}

	public Integer getTotalSessions() {
		return totalSessions;
	}

	public void setTotalSessions(Integer totalSessions) {
		this.totalSessions = totalSessions;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
    
}