package com.management.student_center.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Table(name = "session_details")
public class SessionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id", nullable = false)
    @JsonIgnore
    private Curriculum curriculum;

    @Column(name = "session_number", nullable = true)
    private Integer sessionNumber;
    
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false, length = 500)
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String objectives;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String homework;

    @Column(columnDefinition = "TEXT")
    private String materials;

    @Column(name = "duration_minutes")
    private Integer durationMinutes = 90;

    @Column(name = "teaching_method")
    private String teachingMethod;

    @Column(name = "assessment_criteria")
    private String assessmentCriteria;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SessionDetail() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Curriculum getCurriculum() { return curriculum; }
    public void setCurriculum(Curriculum curriculum) { this.curriculum = curriculum; }

    public Integer getSessionNumber() { return sessionNumber; }
    public void setSessionNumber(Integer sessionNumber) { this.sessionNumber = sessionNumber; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getObjectives() { return objectives; }
    public void setObjectives(String objectives) { this.objectives = objectives; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getHomework() { return homework; }
    public void setHomework(String homework) { this.homework = homework; }

    public String getMaterials() { return materials; }
    public void setMaterials(String materials) { this.materials = materials; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getTeachingMethod() { return teachingMethod; }
    public void setTeachingMethod(String teachingMethod) { this.teachingMethod = teachingMethod; }

    public String getAssessmentCriteria() { return assessmentCriteria; }
    public void setAssessmentCriteria(String assessmentCriteria) { this.assessmentCriteria = assessmentCriteria; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}