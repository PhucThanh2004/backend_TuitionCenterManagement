package com.management.student_center.dto.leave;

public class LeaveAffectedSessionDTO {
    private Long id;
    private Long sessionId;
    private String sessionDate;
    private String status;
    private String replacementResponse;

    private Long leaveId;
    private Long originalTeacherId;
    
    private Long replacementTeacherId;
    private String replacementTeacherName;
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSessionId() {
		return sessionId;
	}
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}
	public String getSessionDate() {
		return sessionDate;
	}
	public void setSessionDate(String sessionDate) {
		this.sessionDate = sessionDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReplacementResponse() {
		return replacementResponse;
	}
	public void setReplacementResponse(String replacementResponse) {
		this.replacementResponse = replacementResponse;
	}
	public Long getLeaveId() {
		return leaveId;
	}
	public void setLeaveId(Long leaveId) {
		this.leaveId = leaveId;
	}
	public Long getOriginalTeacherId() {
		return originalTeacherId;
	}
	public void setOriginalTeacherId(Long originalTeacherId) {
		this.originalTeacherId = originalTeacherId;
	}
	
	public Long getReplacementTeacherId() {
	    return replacementTeacherId;
	}

	public void setReplacementTeacherId(Long replacementTeacherId) {
	    this.replacementTeacherId = replacementTeacherId;
	}

	public String getReplacementTeacherName() {
	    return replacementTeacherName;
	}

	public void setReplacementTeacherName(String replacementTeacherName) {
	    this.replacementTeacherName = replacementTeacherName;
	}
}