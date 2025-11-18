package com.management.student_center.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionDTO {
    private Long id;
    private Long subjectId;
    private Long scheduleId;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long roomId;
    private String status;

    @JsonProperty("Room")
    private RoomDTO Room; // giống Express
    
    @JsonProperty("SubjectSchedule")
    private ScheduleInfoDTO SubjectSchedule; // giống Express

    public SessionDTO() {}

    public SessionDTO(Long id, Long subjectId, Long scheduleId,
                      LocalDate sessionDate, LocalTime startTime, LocalTime endTime,
                      Long roomId, String status,
                      RoomDTO room, ScheduleInfoDTO subjectSchedule) {
        this.id = id;
        this.subjectId = subjectId;
        this.scheduleId = scheduleId;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomId = roomId;
        this.status = status;
        this.Room = room;
        this.SubjectSchedule = subjectSchedule;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
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

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("Room")
	public RoomDTO getRoom() {
	    return Room;
	}

	@JsonProperty("Room")
	public void setRoom(RoomDTO room) {
	    Room = room;
	}

	@JsonProperty("SubjectSchedule")
	public ScheduleInfoDTO getSubjectSchedule() {
	    return SubjectSchedule;
	}

	@JsonProperty("SubjectSchedule")
	public void setSubjectSchedule(ScheduleInfoDTO subjectSchedule) {
	    SubjectSchedule = subjectSchedule;
	}

}
