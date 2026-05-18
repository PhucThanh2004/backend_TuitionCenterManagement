package com.management.student_center.dto;

public class TodayAttendanceDTO {

	private Long sessionId;
	private String date;

	private String teacherStatus;

	private long totalStudents;
	private long presentStudents;
	private long absentStudents;
	private long lateStudents;

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTeacherStatus() {
		return teacherStatus;
	}

	public void setTeacherStatus(String teacherStatus) {
		this.teacherStatus = teacherStatus;
	}

	public long getTotalStudents() {
		return totalStudents;
	}

	public void setTotalStudents(long totalStudents) {
		this.totalStudents = totalStudents;
	}

	public long getPresentStudents() {
		return presentStudents;
	}

	public void setPresentStudents(long presentStudents) {
		this.presentStudents = presentStudents;
	}

	public long getAbsentStudents() {
		return absentStudents;
	}

	public void setAbsentStudents(long absentStudents) {
		this.absentStudents = absentStudents;
	}

	public long getLateStudents() {
		return lateStudents;
	}

	public void setLateStudents(long lateStudents) {
		this.lateStudents = lateStudents;
	}

}