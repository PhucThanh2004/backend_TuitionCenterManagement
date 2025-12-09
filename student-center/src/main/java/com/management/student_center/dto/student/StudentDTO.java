package com.management.student_center.dto.student;

import com.management.student_center.dto.AddressDTO;
import java.time.LocalDate;
import java.util.List;

public class StudentDTO {
    private Long id; // userId
    private String email;
    private String fullName;
    private String phoneNumber;
    private Boolean gender;
    private String image;
    private String roleId;
    private String roleName;
    
    private LocalDate dateOfBirth;
    private String grade;
    private String schoolName;
    
    private AddressDTO address;
    private List<ParentContactDTO> parents;
    // Có thể thêm List<SubjectDTO> subjects nếu cần

    // ... (Generate Getters/Setters)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public Boolean getGender() { return gender; }
    public void setGender(Boolean gender) { this.gender = gender; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public AddressDTO getAddress() { return address; }
    public void setAddress(AddressDTO address) { this.address = address; }
    public List<ParentContactDTO> getParents() { return parents; }
    public void setParents(List<ParentContactDTO> parents) { this.parents = parents; }
}