package com.management.student_center.controller;

import com.management.student_center.dto.*;
import com.management.student_center.service.SessionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/session-details")
public class SessionDetailController {

    @Autowired
    private SessionDetailService sessionDetailService;

    // 1. Lấy danh sách chi tiết buổi học của 1 lộ trình
    @GetMapping("/curriculums/{curriculumId}")
    public ResponseEntity<List<SessionDetailResponseDTO>> getSessionDetailsByCurriculum(
            @PathVariable Long curriculumId) {
        List<SessionDetailResponseDTO> details = sessionDetailService.getSessionDetailsByCurriculum(curriculumId);
        return ResponseEntity.ok(details);
    }

    // 2. Lấy chi tiết 1 buổi học theo id
    @GetMapping("/{id}")
    public ResponseEntity<SessionDetailResponseDTO> getSessionDetailById(@PathVariable Long id) {
        SessionDetailResponseDTO detail = sessionDetailService.getSessionDetailById(id);
        return ResponseEntity.ok(detail);
    }

    // 3. Tạo mới chi tiết buổi học (thêm vào lộ trình)
    @PostMapping("/curriculums/{curriculumId}")
    public ResponseEntity<SessionDetailResponseDTO> createSessionDetail(
            @PathVariable Long curriculumId,
            @RequestBody SessionDetailRequestDTO request) {
        SessionDetailResponseDTO newDetail = sessionDetailService.createSessionDetail(curriculumId, request);
        return new ResponseEntity<>(newDetail, HttpStatus.CREATED);
    }

    // 4. Cập nhật chi tiết buổi học
    @PutMapping("/{id}")
    public ResponseEntity<SessionDetailResponseDTO> updateSessionDetail(
            @PathVariable Long id,
            @RequestBody SessionDetailRequestDTO request) {
        SessionDetailResponseDTO updatedDetail = sessionDetailService.updateSessionDetail(id, request);
        return ResponseEntity.ok(updatedDetail);
    }

    // 5. Xóa chi tiết buổi học
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionDetail(@PathVariable Long id) {
        sessionDetailService.deleteSessionDetail(id);
        return ResponseEntity.noContent().build();
    }

    // 6. Tạo hàng loạt session details cho 1 lộ trình
    @PostMapping("/curriculums/{curriculumId}/batch")
    public ResponseEntity<List<SessionDetailResponseDTO>> createBatchSessionDetails(
            @PathVariable Long curriculumId,
            @RequestBody List<SessionDetailRequestDTO> requests) {
        List<SessionDetailResponseDTO> details = sessionDetailService.createBatchSessionDetails(curriculumId, requests);
        return new ResponseEntity<>(details, HttpStatus.CREATED);
    }

    // 7. Sao chép session details từ lộ trình khác
    @PostMapping("/curriculums/{targetCurriculumId}/copy-from/{sourceCurriculumId}")
    public ResponseEntity<List<SessionDetailResponseDTO>> copySessionDetails(
            @PathVariable Long targetCurriculumId,
            @PathVariable Long sourceCurriculumId,
            @RequestParam(defaultValue = "false") boolean overrideExisting) {
        List<SessionDetailResponseDTO> details = sessionDetailService.copySessionDetails(
                targetCurriculumId, sourceCurriculumId, overrideExisting);
        return new ResponseEntity<>(details, HttpStatus.CREATED);
    }

    // 8. Sắp xếp lại thứ tự các buổi
    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorderSessionDetails(
            @RequestParam Long curriculumId,
            @RequestBody List<Long> sessionDetailIdsInOrder) {
        sessionDetailService.reorderSessionDetails(curriculumId, sessionDetailIdsInOrder);
        return ResponseEntity.ok().build();
    }
}