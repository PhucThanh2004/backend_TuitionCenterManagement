package com.management.student_center.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.management.student_center.dto.assistant.CreateConsultationRequestDTO;
import com.management.student_center.dto.assistant.CreateConsultationResponseDTO;
import com.management.student_center.entity.ConsultationRequest;
import com.management.student_center.service.ConsultationService;

@RestController
@RequestMapping("/v1/api/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    public ConsultationController(
            ConsultationService consultationService
    ) {
        this.consultationService = consultationService;
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody
            CreateConsultationRequestDTO request
    ) {

        Long id =
                consultationService.create(request);

        return ResponseEntity.ok(
                new CreateConsultationResponseDTO(id)
        );
    }
    
    @GetMapping
    public ResponseEntity<List<ConsultationRequest>> getAll() {
        return ResponseEntity.ok(
                consultationService.getAll()
        );
    }

    /**
     * Admin đánh dấu đã liên hệ phụ huynh
     */
    @PatchMapping("/{id}/contacted")
    public ResponseEntity<String> markAsContacted(
            @PathVariable Long id
    ) {

        consultationService.markAsContacted(id);

        return ResponseEntity.ok(
                "Đã cập nhật trạng thái liên hệ thành công."
        );
    }
    
    @GetMapping("/pending-count")
    public ResponseEntity<Long> getPendingCount() {
        return ResponseEntity.ok(
                consultationService.countPendingConsultations()
        );
    }
}