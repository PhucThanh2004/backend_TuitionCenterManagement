package com.management.student_center.dto.leave;

import java.math.BigDecimal;

public class PreviewReplacementSelectionDTO {

    private Long sessionId;
    private Long replacementTeacherId;
    private BigDecimal salary;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getReplacementTeacherId() {
        return replacementTeacherId;
    }

    public void setReplacementTeacherId(Long replacementTeacherId) {
        this.replacementTeacherId = replacementTeacherId;
    }

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}
    
    
}