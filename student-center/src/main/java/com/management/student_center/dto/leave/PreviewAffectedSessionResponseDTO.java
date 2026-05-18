package com.management.student_center.dto.leave;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PreviewAffectedSessionResponseDTO {

    private Long sessionId;

    private String className;

    private String subjectName;

    private LocalDate sessionDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String roomName;
    private Long replacementTeacherId;
    private String replacementTeacherName;

	public Long getReplacementTeacherId() {
		return replacementTeacherId;
	}

	public void setReplacementTeacherId(Long replacementTeacherId) {
		this.replacementTeacherId = replacementTeacherId;
	}

	public String getReplacementTeacherName() {
		return replacementTeacherName;
	}

	public void setReplacementTeacherName(String replacementTeacherName) {
		this.replacementTeacherName = replacementTeacherName;
	}

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
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

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
}