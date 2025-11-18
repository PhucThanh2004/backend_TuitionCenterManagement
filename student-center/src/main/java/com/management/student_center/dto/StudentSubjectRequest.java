package com.management.student_center.dto;

public class StudentSubjectRequest {
    public Long studentId;
    public Long subjectId;
	public Long getStudentId() {
		return studentId;
	}
	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}
	public Long getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}
	public StudentSubjectRequest(Long studentId, Long subjectId) {
		super();
		this.studentId = studentId;
		this.subjectId = subjectId;
	}
    
    
}
