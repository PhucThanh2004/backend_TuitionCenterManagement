package com.management.student_center.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class SubjectScheduleDTO {
    private Long id;
    private Long subjectId;
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;

    public SubjectScheduleDTO() {}

    public SubjectScheduleDTO(Long id, Long subjectId, Integer dayOfWeek, LocalTime startTime,
                              LocalTime endTime, Long roomId, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.subjectId = subjectId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
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

	public Integer getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
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

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

    
}
