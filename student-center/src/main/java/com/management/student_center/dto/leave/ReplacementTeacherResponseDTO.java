package com.management.student_center.dto.leave;

import com.management.student_center.entity.LeaveAffectedSession;

public class ReplacementTeacherResponseDTO {

	private LeaveAffectedSession.ReplacementResponse response;
	private String reason;

	public LeaveAffectedSession.ReplacementResponse getResponse() {
		return response;
	}

	public void setResponse(LeaveAffectedSession.ReplacementResponse response) {
		this.response = response;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}