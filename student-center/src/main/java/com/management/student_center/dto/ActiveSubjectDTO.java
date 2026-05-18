package com.management.student_center.dto;

public class ActiveSubjectDTO {

    private Long subjectId;
    private String subjectName;
    private String startTime;
    private String endTime;
    private Long studentCount;

    public ActiveSubjectDTO(Long subjectId, String subjectName,
                            String startTime, String endTime,
                            Long studentCount) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.studentCount = studentCount;
    }

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Long getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(Long studentCount) {
		this.studentCount = studentCount;
	}

    
}