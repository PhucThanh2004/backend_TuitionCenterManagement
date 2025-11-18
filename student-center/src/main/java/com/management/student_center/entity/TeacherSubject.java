package com.management.student_center.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "teachersubjects")
public class TeacherSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ với Teacher
    @ManyToOne
    @JoinColumn(name = "teacherId")
    private Teacher teacher;

    // Quan hệ với Subject
    @ManyToOne
    @JoinColumn(name = "subjectId")
    private Subject subject;

    // Lương theo giờ
    private BigDecimal salaryRate;

    // Constructor mặc định
    public TeacherSubject() {}

    // Constructor đầy đủ
    public TeacherSubject(Long id, Teacher teacher, Subject subject, BigDecimal salaryRate) {
        this.id = id;
        this.teacher = teacher;
        this.subject = subject;
        this.salaryRate = salaryRate;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public BigDecimal getSalaryRate() { return salaryRate; }
    public void setSalaryRate(BigDecimal salaryRate) { this.salaryRate = salaryRate; }
}
