// 2. StudentSessionEvaluation.java - Đánh giá từng buổi học
package com.management.student_center.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_session_evaluations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "session_detail_id"}))
public class StudentSessionEvaluation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_id", nullable = false)
    private Long studentId;
    
    @Column(name = "session_detail_id", nullable = false)
    private Long sessionDetailId;
    
    @Column(name = "understanding_level")
    private Integer understandingLevel; // 0-100
    
    @Column(name = "completion_status")
    private String completionStatus; // COMPLETED, PARTIAL, NOT_STARTED
    
    @Column(name = "teacher_notes", columnDefinition = "TEXT")
    private String teacherNotes;
    
    @Column(name = "homework_quality")
    private Integer homeworkQuality; // 0-100
    
    @Column(name = "participation_level")
    private Integer participationLevel; // 0-100
    
    @Column(name = "last_updated_by")
    private Long lastUpdatedBy;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    public StudentSessionEvaluation() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public Long getSessionDetailId() { return sessionDetailId; }
    public void setSessionDetailId(Long sessionDetailId) { this.sessionDetailId = sessionDetailId; }
    
    public Integer getUnderstandingLevel() { return understandingLevel; }
    public void setUnderstandingLevel(Integer understandingLevel) { this.understandingLevel = understandingLevel; }
    
    public String getCompletionStatus() { return completionStatus; }
    public void setCompletionStatus(String completionStatus) { this.completionStatus = completionStatus; }
    
    public String getTeacherNotes() { return teacherNotes; }
    public void setTeacherNotes(String teacherNotes) { this.teacherNotes = teacherNotes; }
    
    public Integer getHomeworkQuality() { return homeworkQuality; }
    public void setHomeworkQuality(Integer homeworkQuality) { this.homeworkQuality = homeworkQuality; }
    
    public Integer getParticipationLevel() { return participationLevel; }
    public void setParticipationLevel(Integer participationLevel) { this.participationLevel = participationLevel; }
    
    public Long getLastUpdatedBy() { return lastUpdatedBy; }
    public void setLastUpdatedBy(Long lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}