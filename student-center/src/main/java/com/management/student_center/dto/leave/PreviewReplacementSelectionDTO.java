package com.management.student_center.dto.leave;

public class PreviewReplacementSelectionDTO {

    private Long sessionId;
    private Long replacementTeacherId;

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
}