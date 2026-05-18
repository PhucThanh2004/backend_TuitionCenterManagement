package com.management.student_center.dto.teacher;


public class TeacherIdResponse {
    private Long teacherId;
    private Long userId;
    private String message;

    public TeacherIdResponse(Long teacherId, Long userId, String message) {
        this.teacherId = teacherId;
        this.userId = userId;
        this.message = message;
    }

    // Getters and Setters
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}