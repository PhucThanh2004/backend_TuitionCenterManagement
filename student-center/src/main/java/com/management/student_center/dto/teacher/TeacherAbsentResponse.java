package com.management.student_center.dto.teacher;

import java.time.LocalDate;

public class TeacherAbsentResponse {

    private Long leaveId;

    private Long teacherId;

    private String teacherName;
    
    private String teacherImage;

    private LocalDate startDate;

    private LocalDate endDate;

    private String leaveType;

    private String reason;

    // Constructor đầy đủ
    public TeacherAbsentResponse(Long leaveId,
                                 Long teacherId,
                                 String teacherName,
                                 String teacherImage,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 String leaveType,
                                 String reason) {
        this.leaveId = leaveId;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.teacherImage = teacherImage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
        this.reason = reason;
        
    }

    // Constructor rỗng
    public TeacherAbsentResponse() {
    }

    // Getter & Setter

    public Long getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(Long leaveId) {
        this.leaveId = leaveId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    
    public String getTeacherImage() {
		return teacherImage;
	}

	public void setTeacherImage(String teacherImage) {
		this.teacherImage = teacherImage;
	}

	public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}