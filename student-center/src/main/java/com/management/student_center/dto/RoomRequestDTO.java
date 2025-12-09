package com.management.student_center.dto;

public class RoomRequestDTO {
    private String name;
    private Integer seatCapacity;

    public RoomRequestDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getSeatCapacity() { return seatCapacity; }
    public void setSeatCapacity(Integer seatCapacity) { this.seatCapacity = seatCapacity; }
}
