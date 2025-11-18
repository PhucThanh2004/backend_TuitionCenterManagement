package com.management.student_center.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer rating; // 1-5

    private LocalDateTime createdAt;

    // Quan hệ với Session
    @ManyToOne
    @JoinColumn(name = "sessionId", nullable = false)
    private Session session;

    // Quan hệ với Student
    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    private Student student;

    public Feedback() {
        this.createdAt = LocalDateTime.now();
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
}
