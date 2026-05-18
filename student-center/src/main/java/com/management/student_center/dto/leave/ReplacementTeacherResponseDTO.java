package com.management.student_center.dto.leave;

import com.management.student_center.entity.LeaveAffectedSession;

public class ReplacementTeacherResponseDTO {

    private LeaveAffectedSession.ReplacementResponse response;

    public LeaveAffectedSession.ReplacementResponse getResponse() {
        return response;
    }

    public void setResponse(
            LeaveAffectedSession.ReplacementResponse response
    ) {
        this.response = response;
    }
}