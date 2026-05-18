package com.management.student_center.dto;

public class UpdateSubjectRequest {
    private String name;
    private String grade;
    private Double price;
    private String status;
    private Integer maxStudents;
    private Integer sessionsPerWeek;
    private String note;
    private Long teacherId;      // Có thể null hoặc "" ở frontend
    private Double salaryRate;   // Có thể null, default = 0
    private Long subjectTypeId;

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getMaxStudents() { return maxStudents; }
    public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }

    public Integer getSessionsPerWeek() { return sessionsPerWeek; }
    public void setSessionsPerWeek(Integer sessionsPerWeek) { this.sessionsPerWeek = sessionsPerWeek; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public Double getSalaryRate() { return salaryRate; }
    public void setSalaryRate(Double salaryRate) { this.salaryRate = salaryRate; }
	public Long getSubjectTypeId() {
		return subjectTypeId;
	}
	public void setSubjectTypeId(Long subjectTypeId) {
		this.subjectTypeId = subjectTypeId;
	}
    
    
}
