package com.management.student_center.dto.student;

public class ParentContactDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String relationship;

    // Constructor, Getter, Setter
    public ParentContactDTO() {}
    
    // ... (Generate Getters/Setters)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
}