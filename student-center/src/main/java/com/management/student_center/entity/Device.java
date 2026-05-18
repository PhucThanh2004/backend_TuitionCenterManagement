package com.management.student_center.entity;

import com.management.student_center.enums.DeviceType;
import jakarta.persistence.*;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DeviceType type;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    // ===== CONSTRUCTORS =====
    public Device() {}

    public Device(DeviceType type, Room room) {
        this.type = type;
        this.room = room;
    }

    // ===== GETTERS =====
    public Long getId() {
        return id;
    }

    public DeviceType getType() {
        return type;
    }

    public Room getRoom() {
        return room;
    }

    // ===== SETTERS =====
    public void setId(Long id) {
        this.id = id;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}