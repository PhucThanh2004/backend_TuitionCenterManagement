package com.management.student_center.entity;

import jakarta.persistence.*;
import java.util.List;

import com.management.student_center.enums.RoomManualStatus;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Hoặc Integer tùy theo database của bạn

    // ===== BASIC INFO =====
    private String name;

    @Column(name = "seatCapacity")  // Mapping với cột seatCapacity trong DB
    private Integer seatCapacity;

    // ===== MANUAL STATUS (override) =====
    @Enumerated(EnumType.STRING)
    @Column(name = "manual_status")  // Thêm annotation này để mapping với cột manual_status
    private RoomManualStatus manualStatus = RoomManualStatus.ACTIVE;

    // ===== RELATIONSHIPS =====
    @OneToMany(mappedBy = "room")
    private List<SubjectSchedule> subjectSchedules;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Session> sessions;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Device> devices;

    // ===== CONSTRUCTOR =====
    public Room() {}

    // ===== GETTER / SETTER =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getSeatCapacity() { return seatCapacity; }
    public void setSeatCapacity(Integer seatCapacity) { this.seatCapacity = seatCapacity; }

    public RoomManualStatus getManualStatus() { return manualStatus; }
    public void setManualStatus(RoomManualStatus manualStatus) { this.manualStatus = manualStatus; }

    public List<SubjectSchedule> getSubjectSchedules() { return subjectSchedules; }
    public void setSubjectSchedules(List<SubjectSchedule> subjectSchedules) { this.subjectSchedules = subjectSchedules; }

    public List<Session> getSessions() { return sessions; }
    public void setSessions(List<Session> sessions) { this.sessions = sessions; }

    public List<Device> getDevices() { return devices; }
    public void setDevices(List<Device> devices) { this.devices = devices; }
}