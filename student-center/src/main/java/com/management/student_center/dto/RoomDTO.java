package com.management.student_center.dto;

public class RoomDTO {
    private String name;

    public RoomDTO() {}
    public RoomDTO(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
