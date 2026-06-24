package com.management.student_center.dto.teacherSuggestion;

import lombok.Data;
import java.util.List;

@Data
public class TeacherSuggestionDTO {
    private Long teacherId;           // ID giáo viên
    private String fullName;          // Họ tên
    private String specialty;         // Chuyên môn
    private String phoneNumber;       // Số điện thoại
    private String email;             // Email
    private String image;             // Ảnh đại diện
    private Boolean gender;           // Giới tính (true: Nam, false: Nữ)
    private Double matchScore;        // Điểm phù hợp (0-100)
    private Integer availableSlots;   // Số buổi trống trong 2 tuần
    private Integer totalSubjects;    // Tổng số môn đã/dang dạy
    private Integer totalStudents;    // Tổng số học sinh đã dạy
    private String reason;            // Lý do đề xuất
    private List<String> strengths;   // Điểm mạnh của giáo viên
	public Long getTeacherId() {
		return teacherId;
	}
	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Boolean getGender() {
		return gender;
	}
	public void setGender(Boolean gender) {
		this.gender = gender;
	}
	public Double getMatchScore() {
		return matchScore;
	}
	public void setMatchScore(Double matchScore) {
		this.matchScore = matchScore;
	}
	public Integer getAvailableSlots() {
		return availableSlots;
	}
	public void setAvailableSlots(Integer availableSlots) {
		this.availableSlots = availableSlots;
	}
	public Integer getTotalSubjects() {
		return totalSubjects;
	}
	public void setTotalSubjects(Integer totalSubjects) {
		this.totalSubjects = totalSubjects;
	}
	public Integer getTotalStudents() {
		return totalStudents;
	}
	public void setTotalStudents(Integer totalStudents) {
		this.totalStudents = totalStudents;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public List<String> getStrengths() {
		return strengths;
	}
	public void setStrengths(List<String> strengths) {
		this.strengths = strengths;
	}
    
    
}