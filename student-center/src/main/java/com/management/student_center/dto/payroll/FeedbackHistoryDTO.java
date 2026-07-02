package com.management.student_center.dto.payroll;

import java.time.LocalDateTime;

public class FeedbackHistoryDTO {
    private String message;
    private String type; // REJECT, ADJUSTMENT, CONFIRM, FINALIZE
    private String actor; // TEACHER, ADMIN
    private LocalDateTime createdAt;

    public FeedbackHistoryDTO() {}

    public FeedbackHistoryDTO(String message, String type, String actor, LocalDateTime createdAt) {
        this.message = message;
        this.type = type;
        this.actor = actor;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}