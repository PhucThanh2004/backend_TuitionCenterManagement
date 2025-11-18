package com.management.student_center.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "parentcontacts")
public class ParentContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    private String phoneNumber;

    private String relationship;

    // Quan hệ với Student
    @ManyToOne
    @JoinColumn(name = "studentId")
    private Student student;

    // Constructor mặc định
    public ParentContact() {}

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
}
