package com.management.student_center.controller;

import com.management.student_center.dto.CurriculumEvaluationDTO;
import com.management.student_center.dto.CurriculumEvaluationUpdateRequest;
import com.management.student_center.dto.SessionEvaluationDTO;
import com.management.student_center.dto.SessionEvaluationUpdateRequest;
import com.management.student_center.service.CurrentUserService;
import com.management.student_center.service.StudentEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/student-evaluations")
public class StudentEvaluationController {
    
    @Autowired
    private StudentEvaluationService evaluationService;
    
    @Autowired
    private CurrentUserService currentUserService;
    
    // ============ Curriculum Evaluation APIs ============
    
    /**
     * Lấy tất cả curriculum evaluations cho một học sinh và môn học
     */
    @GetMapping("/curriculums/{studentId}/{subjectId}")
    public ResponseEntity<List<CurriculumEvaluationDTO>> getCurriculumEvaluations(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {
        List<CurriculumEvaluationDTO> evaluations = evaluationService.getCurriculumEvaluations(studentId, subjectId);
        return ResponseEntity.ok(evaluations);
    }
    
    /**
     * Lấy curriculum evaluation theo curriculumId
     */
    @GetMapping("/curriculum/{studentId}/{curriculumId}")
    public ResponseEntity<CurriculumEvaluationDTO> getCurriculumEvaluation(
            @PathVariable Long studentId,
            @PathVariable Long curriculumId) {
        CurriculumEvaluationDTO evaluation = evaluationService.getCurriculumEvaluation(studentId, curriculumId);
        return ResponseEntity.ok(evaluation);
    }
    
    /**
     * THÊM MỚI: Tạo đánh giá curriculum
     */
    @PostMapping("/curriculum/{studentId}/{curriculumId}")
    public ResponseEntity<CurriculumEvaluationDTO> createCurriculumEvaluation(
            @PathVariable Long studentId,
            @PathVariable Long curriculumId,
            @RequestBody CurriculumEvaluationUpdateRequest request) {
        Long teacherId = currentUserService.getCurrentUserId();
        CurriculumEvaluationDTO evaluation = evaluationService.createCurriculumEvaluation(
            studentId, curriculumId, request, teacherId);
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluation);
    }
    
    /**
     * CẬP NHẬT: Cập nhật đánh giá curriculum
     */
    @PutMapping("/curriculum/{studentId}/{curriculumId}")
    public ResponseEntity<CurriculumEvaluationDTO> updateCurriculumEvaluation(
            @PathVariable Long studentId,
            @PathVariable Long curriculumId,
            @RequestBody CurriculumEvaluationUpdateRequest request) {
        Long teacherId = currentUserService.getCurrentUserId();
        CurriculumEvaluationDTO evaluation = evaluationService.updateCurriculumEvaluation(
            studentId, curriculumId, request, teacherId);
        return ResponseEntity.ok(evaluation);
    }
    
    /**
     * XÓA: Xóa đánh giá curriculum
     */
    @DeleteMapping("/curriculum/{studentId}/{curriculumId}")
    public ResponseEntity<Void> deleteCurriculumEvaluation(
            @PathVariable Long studentId,
            @PathVariable Long curriculumId) {
        evaluationService.deleteCurriculumEvaluation(studentId, curriculumId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * KIỂM TRA: Kiểm tra tồn tại đánh giá curriculum
     */
    @GetMapping("/curriculum/{studentId}/{curriculumId}/exists")
    public ResponseEntity<Boolean> hasCurriculumEvaluation(
            @PathVariable Long studentId,
            @PathVariable Long curriculumId) {
        boolean exists = evaluationService.hasCurriculumEvaluation(studentId, curriculumId);
        return ResponseEntity.ok(exists);
    }
    
    // ============ Session Evaluation APIs ============
    
    /**
     * Lấy session evaluations theo curriculum (trả về map với key là curriculumId)
     */
    @GetMapping("/sessions/by-curriculum/{studentId}/{subjectId}")
    public ResponseEntity<Map<Long, List<SessionEvaluationDTO>>> getSessionEvaluationsByCurriculum(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {
        Map<Long, List<SessionEvaluationDTO>> evaluations = 
            evaluationService.getSessionEvaluationsByCurriculum(studentId, subjectId);
        return ResponseEntity.ok(evaluations);
    }
    
    /**
     * Lấy tất cả session evaluations (gộp từ tất cả curriculums)
     */
    @GetMapping("/sessions/{studentId}/{subjectId}")
    public ResponseEntity<List<SessionEvaluationDTO>> getSessionEvaluations(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {
        List<SessionEvaluationDTO> evaluations = evaluationService.getSessionEvaluations(studentId, subjectId);
        return ResponseEntity.ok(evaluations);
    }
    
    /**
     * Lấy session evaluation theo sessionDetailId
     */
    @GetMapping("/session/{studentId}/{sessionDetailId}")
    public ResponseEntity<SessionEvaluationDTO> getSessionEvaluation(
            @PathVariable Long studentId,
            @PathVariable Long sessionDetailId) {
        SessionEvaluationDTO evaluation = evaluationService.getSessionEvaluation(studentId, sessionDetailId);
        return ResponseEntity.ok(evaluation);
    }
    
    /**
     * THÊM MỚI: Tạo đánh giá session
     */
    @PostMapping("/session/{studentId}/{sessionDetailId}")
    public ResponseEntity<SessionEvaluationDTO> createSessionEvaluation(
            @PathVariable Long studentId,
            @PathVariable Long sessionDetailId,
            @RequestBody SessionEvaluationUpdateRequest request) {
        Long teacherId = currentUserService.getCurrentUserId();
        SessionEvaluationDTO evaluation = evaluationService.createSessionEvaluation(
            studentId, sessionDetailId, request, teacherId);
        return ResponseEntity.status(HttpStatus.CREATED).body(evaluation);
    }
    
    /**
     * CẬP NHẬT: Cập nhật đánh giá session
     */
    @PutMapping("/session/{studentId}/{sessionDetailId}")
    public ResponseEntity<SessionEvaluationDTO> updateSessionEvaluation(
            @PathVariable Long studentId,
            @PathVariable Long sessionDetailId,
            @RequestBody SessionEvaluationUpdateRequest request) {
        Long teacherId = currentUserService.getCurrentUserId();
        SessionEvaluationDTO evaluation = evaluationService.updateSessionEvaluation(
            studentId, sessionDetailId, request, teacherId);
        return ResponseEntity.ok(evaluation);
    }
    
    /**
     * XÓA: Xóa đánh giá session
     */
    @DeleteMapping("/session/{studentId}/{sessionDetailId}")
    public ResponseEntity<Void> deleteSessionEvaluation(
            @PathVariable Long studentId,
            @PathVariable Long sessionDetailId) {
        evaluationService.deleteSessionEvaluation(studentId, sessionDetailId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * KIỂM TRA: Kiểm tra tồn tại đánh giá session
     */
    @GetMapping("/session/{studentId}/{sessionDetailId}/exists")
    public ResponseEntity<Boolean> hasSessionEvaluation(
            @PathVariable Long studentId,
            @PathVariable Long sessionDetailId) {
        boolean exists = evaluationService.hasSessionEvaluation(studentId, sessionDetailId);
        return ResponseEntity.ok(exists);
    }
    
    // ============ Statistics APIs ============
    
    /**
     * Lấy thống kê điểm danh
     */
    @GetMapping("/attendance-stats/{studentId}/{subjectId}")
    public ResponseEntity<Map<String, Integer>> getAttendanceStatistics(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {
        Map<String, Integer> stats = evaluationService.getAttendanceStatistics(studentId, subjectId);
        return ResponseEntity.ok(stats);
    }
}