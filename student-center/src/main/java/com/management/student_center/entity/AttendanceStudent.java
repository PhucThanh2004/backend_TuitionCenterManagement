package com.management.student_center.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "attendance_students")
public class AttendanceStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status; // present | absent | late

    @Column(columnDefinition = "TEXT")
    private String note;

    // Quan hệ với Session
    @ManyToOne
    @JoinColumn(name = "sessionId", nullable = false)
    private Session session;

    // Quan hệ với Student
    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private Student student;

    public AttendanceStudent() {}

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
}
