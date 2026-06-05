package com.management.student_center.dto;

import java.util.List;

public class CurriculumDetailDTO extends CurriculumResponseDTO {
    private List<SessionDetailResponseDTO> sessionDetails;
    
    public List<SessionDetailResponseDTO> getSessionDetails() {
        return sessionDetails;
    }
    
    public void setSessionDetails(List<SessionDetailResponseDTO> sessionDetails) {
        this.sessionDetails = sessionDetails;
    }
}