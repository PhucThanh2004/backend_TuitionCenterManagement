package com.management.student_center.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer seatCapacity;

    // Quan hệ 1 Room có nhiều SubjectSchedule
    @OneToMany(mappedBy = "room")
    private List<SubjectSchedule> subjectSchedules;

    // Quan hệ 1 Room có nhiều Session
    @OneToMany(mappedBy = "room")
    private List<Session> sessions;

    // Constructor mặc định
    public Room() {}

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getSeatCapacity() { return seatCapacity; }
    public void setSeatCapacity(Integer seatCapacity) { this.seatCapacity = seatCapacity; }

    public List<SubjectSchedule> getSubjectSchedules() { return subjectSchedules; }
    public void setSubjectSchedules(List<SubjectSchedule> subjectSchedules) { this.subjectSchedules = subjectSchedules; }

    public List<Session> getSessions() { return sessions; }
    public void setSessions(List<Session> sessions) { this.sessions = sessions; }
}
