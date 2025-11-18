package com.management.student_center.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "student_assignments")
public class StudentAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assignmentId", nullable = false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private Student student;

    private String submittedStatus = "pending"; // pending | submitted | late

    @Column(columnDefinition = "TEXT")
    private String feedback;

    public StudentAssignment() {}

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getSubmittedStatus() { return submittedStatus; }
    public void setSubmittedStatus(String submittedStatus) { this.submittedStatus = submittedStatus; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
