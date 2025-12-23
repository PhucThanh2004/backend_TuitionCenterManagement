package com.management.student_center.dto;

import java.time.LocalTime;
import java.util.List;

public class DailySessionDTO {

    private Long sessionId;
    private String subjectName;
    private LocalTime startTime;
    private LocalTime endTime;

    private String roomName;

    private List<String> teacherNames;

    // constructor
    public DailySessionDTO(Long sessionId, String subjectName,
                           LocalTime startTime, LocalTime endTime,
                           String roomName, List<String> teacherNames) {
        this.sessionId = sessionId;
        this.subjectName = subjectName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomName = roomName;
        this.teacherNames = teacherNames;
    }

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
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

	public List<String> getTeacherNames() {
		return teacherNames;
	}

	public void setTeacherNames(List<String> teacherNames) {
		this.teacherNames = teacherNames;
	}
}
