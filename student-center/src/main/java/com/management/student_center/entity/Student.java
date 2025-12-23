package com.management.student_center.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ với User
    @OneToOne
    @JoinColumn(name = "userId")
    private User userInfo;

    // Quan hệ với Address
    @ManyToOne
    @JoinColumn(name = "addressId")
    private Address addressInfo;

    private LocalDate dateOfBirth;

    private String grade;

    private String schoolName;

    // Quan hệ với ParentContact
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ParentContact> parentContacts;

    // Quan hệ với StudentSubject
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StudentSubject> studentSubjects;

    // Thêm createdAt và updatedAt
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructor mặc định
    public Student() {}

    // Constructor đầy đủ
    public Student(Long id, User userInfo, Address addressInfo, LocalDate dateOfBirth,
                   String grade, String schoolName,
                   List<ParentContact> parentContacts,
                   List<StudentSubject> studentSubjects,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userInfo = userInfo;
        this.addressInfo = addressInfo;
        this.dateOfBirth = dateOfBirth;
        this.grade = grade;
        this.schoolName = schoolName;
        this.parentContacts = parentContacts;
        this.studentSubjects = studentSubjects;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getter và Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUserInfo() { return userInfo; }
    public void setUserInfo(User userInfo) { this.userInfo = userInfo; }

    public Address getAddressInfo() { return addressInfo; }
    public void setAddressInfo(Address addressInfo) { this.addressInfo = addressInfo; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public List<ParentContact> getParentContacts() { return parentContacts; }
    public void setParentContacts(List<ParentContact> parentContacts) { this.parentContacts = parentContacts; }

    public List<StudentSubject> getStudentSubjects() { return studentSubjects; }
    public void setStudentSubjects(List<StudentSubject> studentSubjects) { this.studentSubjects = studentSubjects; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
