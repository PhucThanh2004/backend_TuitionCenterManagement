package com.management.student_center.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TeacherAttendanceResponseDTO {

    private Long subjectId;
    private List<SessionDTO> sessions;
    private List<TeacherAttendanceDTO> teachers;

    public record SessionDTO(Long sessionId, LocalDate date, LocalTime startTime, LocalTime endTime) {}
    public record AttendanceItem(Long sessionId, String status, String note) {}
    public record TeacherAttendanceDTO(Long teacherId, String fullName, List<AttendanceItem> attendances) {}

    // Getter & Setter
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public List<SessionDTO> getSessions() { return sessions; }
    public void setSessions(List<SessionDTO> sessions) { this.sessions = sessions; }

    public List<TeacherAttendanceDTO> getTeachers() { return teachers; }
    public void setTeachers(List<TeacherAttendanceDTO> teachers) { this.teachers = teachers; }
}
