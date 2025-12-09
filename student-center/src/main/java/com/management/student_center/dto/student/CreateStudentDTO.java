package com.management.student_center.dto.student;

import com.management.student_center.dto.AddressDTO;
import java.time.LocalDate;
import java.util.List;

public class CreateStudentDTO {
    private String email;
    private String password;
    private String fullName;
    private String roleId; // R2
    private Boolean gender;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    
    private String grade;
    private String schoolName;
    
    private AddressDTO address;
    private List<ParentContactDTO> parents; // Danh sách phụ huynh

    // ... (Generate Getters/Setters cho tất cả các trường)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public Boolean getGender() { return gender; }
    public void setGender(Boolean gender) { this.gender = gender; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
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