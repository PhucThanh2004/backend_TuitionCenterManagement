// 7. Request DTOs
package com.management.student_center.dto;


public class CurriculumEvaluationUpdateRequest {
    private Integer understandingLevel;
    private Integer overallProgress;
    private String teacherNotes;
    private String strengths;
    private String weaknesses;
    private String recommendations;
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
    
}
