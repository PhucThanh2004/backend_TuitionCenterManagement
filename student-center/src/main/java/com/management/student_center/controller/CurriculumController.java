package com.management.student_center.controller;

import com.management.student_center.dto.*;
import com.management.student_center.service.CurriculumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/curriculums")
public class CurriculumController {

    @Autowired
    private CurriculumService curriculumService;

    // 1. Lấy danh sách tất cả lộ trình (có thể lọc theo subject)
    @GetMapping
    public ResponseEntity<List<CurriculumResponseDTO>> getAllCurriculums(
            @RequestParam(required = false) Long subjectId) {
        List<CurriculumResponseDTO> curriculums = curriculumService.getAllCurriculums(subjectId);
        return ResponseEntity.ok(curriculums);
    }

    // 2. Lấy chi tiết 1 lộ trình (kèm danh sách session details)
    @GetMapping("/{id}")
    public ResponseEntity<CurriculumDetailDTO> getCurriculumById(@PathVariable Long id) {
        CurriculumDetailDTO curriculum = curriculumService.getCurriculumById(id);
        return ResponseEntity.ok(curriculum);
    }

    // 3. Tạo lộ trình mới cho môn học
    @PostMapping("/subjects/{subjectId}")
    public ResponseEntity<CurriculumResponseDTO> createCurriculum(
            @PathVariable Long subjectId,
            @RequestBody CurriculumRequestDTO request) {
        CurriculumResponseDTO newCurriculum = curriculumService.createCurriculum(subjectId, request);
        return new ResponseEntity<>(newCurriculum, HttpStatus.CREATED);
    }

    // 4. Cập nhật lộ trình
    @PutMapping("/{id}")
    public ResponseEntity<CurriculumResponseDTO> updateCurriculum(
            @PathVariable Long id,
            @RequestBody CurriculumRequestDTO request) {
        CurriculumResponseDTO updatedCurriculum = curriculumService.updateCurriculum(id, request);
        return ResponseEntity.ok(updatedCurriculum);
    }

    // 5. Xóa lộ trình (xóa mềm hoặc cứng)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurriculum(@PathVariable Long id) {
        curriculumService.deleteCurriculum(id);
        return ResponseEntity.noContent().build();
    }

    // 6. Cập nhật trạng thái lộ trình
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateCurriculumStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        curriculumService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    // 7. Lấy danh sách lộ trình theo môn học
    @GetMapping("/subjects/{subjectId}")
    public ResponseEntity<List<CurriculumResponseDTO>> getCurriculumsBySubject(
            @PathVariable Long subjectId) {
        List<CurriculumResponseDTO> curriculums = curriculumService.getCurriculumsBySubject(subjectId);
        return ResponseEntity.ok(curriculums);
    }
}