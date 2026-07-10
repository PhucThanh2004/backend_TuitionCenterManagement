package com.management.student_center.dto.assistant;

public class CreateConsultationResponseDTO {

    private Long consultationRequestId;

    public CreateConsultationResponseDTO(
            Long consultationRequestId
    ) {
        this.consultationRequestId = consultationRequestId;
    }

    public Long getConsultationRequestId() {
        return consultationRequestId;
    }
}