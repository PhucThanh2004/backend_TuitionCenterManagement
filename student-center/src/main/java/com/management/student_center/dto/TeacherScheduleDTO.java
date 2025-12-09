package com.management.student_center.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class TeacherScheduleDTO {

    private Long sessionId;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;

    private Long subjectId;
    private String subjectName;

    private Long roomId;
    private String roomName;

    public TeacherScheduleDTO() {}

    public TeacherScheduleDTO(Long sessionId, LocalDate sessionDate, LocalTime startTime, LocalTime endTime, String status,
                              Long subjectId, String subjectName,
                              Long roomId, String roomName) {
        this.sessionId = sessionId;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.roomId = roomId;
        this.roomName = roomName;
    }

    // Getter & Setter
    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
}
