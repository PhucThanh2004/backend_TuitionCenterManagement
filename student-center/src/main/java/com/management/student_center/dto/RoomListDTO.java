package com.management.student_center.dto;

public class RoomListDTO {

    private Long id;
    private String name;
    private Integer seatCapacity;

    public RoomListDTO() {}

    public RoomListDTO(Long id, String name, Integer seatCapacity) {
        this.id = id;
        this.name = name;
        this.seatCapacity = seatCapacity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getSeatCapacity() { return seatCapacity; }
    public void setSeatCapacity(Integer seatCapacity) { this.seatCapacity = seatCapacity; }
}
