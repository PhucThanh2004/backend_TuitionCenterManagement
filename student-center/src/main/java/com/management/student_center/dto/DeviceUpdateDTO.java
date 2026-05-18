package com.management.student_center.dto;

public class DeviceUpdateDTO {
    private Long id;        // null nếu là thêm mới
    private String type;    // Loại thiết bị
    private String action;  // "ADD", "UPDATE", "DELETE"
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}