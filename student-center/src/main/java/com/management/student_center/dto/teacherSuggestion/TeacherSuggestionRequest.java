package com.management.student_center.dto.teacherSuggestion;

import lombok.Data;
import java.util.List;

@Data
public class TeacherSuggestionRequest {
    private String subjectName;              // Tên môn học (VD: "Toán")
    private String grade;                    // Khối lớp (VD: "12")
    private Long subjectTypeId;              // Loại môn học (optional)
    private List<TimeSlot> preferredTimeSlots; // Khung giờ mong muốn (optional)
    private Integer limit = 5;               // Số lượng gợi ý trả về, mặc định 5
	public String getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public Long getSubjectTypeId() {
		return subjectTypeId;
	}
	public void setSubjectTypeId(Long subjectTypeId) {
		this.subjectTypeId = subjectTypeId;
	}
	public List<TimeSlot> getPreferredTimeSlots() {
		return preferredTimeSlots;
	}
	public void setPreferredTimeSlots(List<TimeSlot> preferredTimeSlots) {
		this.preferredTimeSlots = preferredTimeSlots;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
    
    
}