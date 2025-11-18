package com.management.student_center.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MaterialDTO {
    private Long id;
    private String title;
    private String fileURL;
    private String type;
    private LocalDateTime uploadedAt;
    private Long subjectId;
    private String subjectName;
    private Long uploadedById;
    private String uploadedByName;
    private String fileSize; // MB
    
    private UserDTO User;

    public static class UserDTO {
        private Long id;
        private String fullName;
        private String email;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getFullName() {
			return fullName;
		}
		public void setFullName(String fullName) {
			this.fullName = fullName;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
       
    }

    public MaterialDTO() {}

    public MaterialDTO(Long id, String title, String fileURL, String type, LocalDateTime uploadedAt,
                       Long subjectId, String subjectName, Long uploadedById, String uploadedByName, String fileSize) {
        this.id = id;
        this.title = title;
        this.fileURL = fileURL;
        this.type = type;
        this.uploadedAt = uploadedAt;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.uploadedById = uploadedById;
        this.uploadedByName = uploadedByName;
        this.fileSize = fileSize;
    }

    @JsonProperty("User")
    public UserDTO getUser() {
        return User;
    }

    public void setUser(UserDTO user) {
        this.User = user;
    }

    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFileURL() {
		return fileURL;
	}

	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public LocalDateTime getUploadedAt() {
		return uploadedAt;
	}

	public void setUploadedAt(LocalDateTime uploadedAt) {
		this.uploadedAt = uploadedAt;
	}

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public Long getUploadedById() {
		return uploadedById;
	}

	public void setUploadedById(Long uploadedById) {
		this.uploadedById = uploadedById;
	}

	public String getUploadedByName() {
		return uploadedByName;
	}

	public void setUploadedByName(String uploadedByName) {
		this.uploadedByName = uploadedByName;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

    
}
