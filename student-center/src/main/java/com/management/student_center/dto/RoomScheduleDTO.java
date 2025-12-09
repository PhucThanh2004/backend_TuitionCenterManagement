package com.management.student_center.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class RoomScheduleDTO {

    private Long roomId;
    private String roomName;
    private Integer seatCapacity;

    private Long scheduleId;
    private String subjectName;
    private Integer dayOfWeek;
    private LocalTime scheduleStartTime;
    private LocalTime scheduleEndTime;

    private Long sessionId;
    private LocalDate sessionDate;
    private LocalTime sessionStartTime;
    private LocalTime sessionEndTime;
    private String sessionStatus;

    // Constructor đầy đủ
    public RoomScheduleDTO(Long roomId, String roomName, Integer seatCapacity,
                           Long scheduleId, String subjectName, Integer dayOfWeek,
                           LocalTime scheduleStartTime, LocalTime scheduleEndTime,
                           Long sessionId, LocalDate sessionDate, LocalTime sessionStartTime,
                           LocalTime sessionEndTime, String sessionStatus) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.seatCapacity = seatCapacity;
        this.scheduleId = scheduleId;
        this.subjectName = subjectName;
        this.dayOfWeek = dayOfWeek;
        this.scheduleStartTime = scheduleStartTime;
        this.scheduleEndTime = scheduleEndTime;
        this.sessionId = sessionId;
        this.sessionDate = sessionDate;
        this.sessionStartTime = sessionStartTime;
        this.sessionEndTime = sessionEndTime;
        this.sessionStatus = sessionStatus;
    }

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public Integer getSeatCapacity() {
		return seatCapacity;
	}

	public void setSeatCapacity(Integer seatCapacity) {
		this.seatCapacity = seatCapacity;
	}

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Integer getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public LocalTime getScheduleStartTime() {
		return scheduleStartTime;
	}

	public void setScheduleStartTime(LocalTime scheduleStartTime) {
		this.scheduleStartTime = scheduleStartTime;
	}

	public LocalTime getScheduleEndTime() {
		return scheduleEndTime;
	}

	public void setScheduleEndTime(LocalTime scheduleEndTime) {
		this.scheduleEndTime = scheduleEndTime;
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

	public LocalTime getSessionStartTime() {
		return sessionStartTime;
	}

	public void setSessionStartTime(LocalTime sessionStartTime) {
		this.sessionStartTime = sessionStartTime;
	}

	public LocalTime getSessionEndTime() {
		return sessionEndTime;
	}

	public void setSessionEndTime(LocalTime sessionEndTime) {
		this.sessionEndTime = sessionEndTime;
	}

	public String getSessionStatus() {
		return sessionStatus;
	}

	public void setSessionStatus(String sessionStatus) {
		this.sessionStatus = sessionStatus;
	}

    
}
