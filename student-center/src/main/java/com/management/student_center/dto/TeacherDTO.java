// TeacherDTO.java
package com.management.student_center.dto;

public class TeacherDTO {
    private Long id;
    private String specialty;
    private UserDTO User;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	public UserDTO getUser() { return User; }
	public void setUser(UserDTO user) { this.User = user; }


    // getters & setters
    
}
