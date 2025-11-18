package com.management.student_center.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ScheduleInfoDTO {
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    // constructors, getters, setters
    public ScheduleInfoDTO() {}
    public ScheduleInfoDTO(Integer dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}
