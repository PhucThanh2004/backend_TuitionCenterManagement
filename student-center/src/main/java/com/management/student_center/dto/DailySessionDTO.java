package com.management.student_center.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class DailySessionDTO {

    private Long sessionId;

    private LocalDate sessionDate;

    private String subjectName;

    private LocalTime startTime;

    private LocalTime endTime;

    private String roomName;

    private TeacherInfo teacher;

    private String status;
    
    public static class TeacherInfo {
        private Long id;
        private String fullName;

        public TeacherInfo(Long id, String fullName) {
            this.id = id;
            this.fullName = fullName;
        }

        public Long getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }
    }

    public DailySessionDTO(
            Long sessionId,
            LocalDate sessionDate,
            String subjectName,
            LocalTime startTime,
            LocalTime endTime,
            String roomName,
            TeacherInfo teacher,
            String status,
            TeacherLeaveInfo teacherLeaveInfo
    ) {
        this.sessionId = sessionId;
        this.sessionDate = sessionDate;
        this.subjectName = subjectName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomName = roomName;
        this.teacher = teacher;
        this.status = status;
        this.teacherLeaveInfo = teacherLeaveInfo;
    }

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public LocalDate getSessionDate() {
		return sessionDate;
	}

	public void setSessionDate(LocalDate sessionDate) {
		this.sessionDate = sessionDate;
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

	public TeacherInfo getTeacher() {
		return teacher;
	}

	public void setTeacher(TeacherInfo teacher) {
		this.teacher = teacher;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	private TeacherLeaveInfo teacherLeaveInfo;

	public static class TeacherLeaveInfo {
	    private String type;
	    private Long replacementTeacherId;
	    private String replacementTeacherName;

	    public TeacherLeaveInfo(String type, Long replacementTeacherId, String replacementTeacherName) {
	        this.type = type;
	        this.replacementTeacherId = replacementTeacherId;
	        this.replacementTeacherName = replacementTeacherName;
	    }

	    public String getType() { return type; }
	    public Long getReplacementTeacherId() { return replacementTeacherId; }
	    public String getReplacementTeacherName() { return replacementTeacherName; }
	}
	
	public TeacherLeaveInfo getTeacherLeaveInfo() {
	    return teacherLeaveInfo;
	}

	public void setTeacherLeaveInfo(TeacherLeaveInfo teacherLeaveInfo) {
	    this.teacherLeaveInfo = teacherLeaveInfo;
	}
}