package com.management.student_center.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate sessionDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String status = "scheduled"; // scheduled | completed | canceled

    // Quan hệ với Subject
    @ManyToOne
    @JoinColumn(name = "subjectId", nullable = false)
    private Subject subject;

    // Quan hệ với SubjectSchedule
    @ManyToOne
    @JoinColumn(name = "scheduleId")
    private SubjectSchedule schedule;

    // Quan hệ với Room
    @ManyToOne
    @JoinColumn(name = "roomId")
    private Room room;

    // Một session có nhiều attendance học sinh
    @OneToMany(mappedBy = "session")
    private List<AttendanceStudent> attendanceStudents;

    // Một session có nhiều TeacherAttendance
    @OneToMany(mappedBy = "session")
    private List<TeacherAttendance> teacherAttendances;

    // Feedback của học sinh
    @OneToMany(mappedBy = "session")
    private List<Feedback> feedbacks;

    public Session() {}

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public SubjectSchedule getSchedule() { return schedule; }
    public void setSchedule(SubjectSchedule schedule) { this.schedule = schedule; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public List<AttendanceStudent> getAttendanceStudents() { return attendanceStudents; }
    public void setAttendanceStudents(List<AttendanceStudent> attendanceStudents) { this.attendanceStudents = attendanceStudents; }

    public List<TeacherAttendance> getTeacherAttendances() { return teacherAttendances; }
    public void setTeacherAttendances(List<TeacherAttendance> teacherAttendances) { this.teacherAttendances = teacherAttendances; }

    public List<Feedback> getFeedbacks() { return feedbacks; }
    public void setFeedbacks(List<Feedback> feedbacks) { this.feedbacks = feedbacks; }
}
