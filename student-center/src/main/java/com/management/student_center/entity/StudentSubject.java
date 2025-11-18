package com.management.student_center.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "studentsubjects")
public class StudentSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ với Student
    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    // Quan hệ với Subject
    @ManyToOne
    @JoinColumn(name = "subjectId")
    private Subject subject;

    // Ngày đăng ký
    private LocalDate enrollmentDate;

    // Constructor mặc định
    public StudentSubject() {}

    // Constructor đầy đủ
    public StudentSubject(Long id, Student student, Subject subject, LocalDate enrollmentDate) {
        this.id = id;
        this.student = student;
        this.subject = subject;
        this.enrollmentDate = enrollmentDate;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
}
