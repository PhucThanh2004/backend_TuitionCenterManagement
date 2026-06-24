package com.management.student_center.service;

import com.management.student_center.dto.CurriculumEvaluationDTO;
import com.management.student_center.dto.CurriculumEvaluationUpdateRequest;
import com.management.student_center.dto.SessionEvaluationDTO;
import com.management.student_center.dto.SessionEvaluationUpdateRequest;
import com.management.student_center.entity.*;
import com.management.student_center.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentEvaluationService {
    
    @Autowired
    private StudentCurriculumEvaluationRepository curriculumEvaluationRepository;
    
    @Autowired
    private StudentSessionEvaluationRepository sessionEvaluationRepository;
    
    @Autowired
    private CurriculumRepository curriculumRepository;
    
    @Autowired
    private SessionDetailRepository sessionDetailRepository;
    
    @Autowired
    private AttendanceStudentRepository attendanceRepository;
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    /**
     * Lấy đánh giá tổng thể cho tất cả curriculum của học sinh
     */
    public List<CurriculumEvaluationDTO> getCurriculumEvaluations(Long studentId, Long subjectId) {
        List<Curriculum> curriculums = curriculumRepository.findBySubjectId(subjectId);
        
        if (curriculums.isEmpty()) {
            return new ArrayList<>();
        }
        
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh"));
        
        List<Session> subjectSessions = sessionRepository.findBySubjectId(subjectId);
        List<AttendanceStudent> attendances = attendanceRepository
            .findByStudentIdAndSubjectId(studentId, subjectId);
        
        // Lấy tất cả session evaluations cho student
        List<StudentSessionEvaluation> allSessionEvaluations = sessionEvaluationRepository
            .findByStudentId(studentId);
        
        // Xử lý từng curriculum
        return curriculums.stream()
            .map(curriculum -> buildCurriculumEvaluationDTO(studentId, curriculum, allSessionEvaluations))
            .collect(Collectors.toList());
    }
    
    /**
     * Lấy đánh giá tổng thể cho một curriculum cụ thể
     */
    public CurriculumEvaluationDTO getCurriculumEvaluation(Long studentId, Long curriculumId) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy curriculum"));
        
        List<StudentSessionEvaluation> allSessionEvaluations = sessionEvaluationRepository
            .findByStudentId(studentId);
        
        return buildCurriculumEvaluationDTO(studentId, curriculum, allSessionEvaluations);
    }
    
    /**
     * Helper method để build CurriculumEvaluationDTO
     */
    private CurriculumEvaluationDTO buildCurriculumEvaluationDTO(Long studentId, Curriculum curriculum, 
                                                                  List<StudentSessionEvaluation> allSessionEvaluations) {
        // Lấy session details của curriculum này
        List<SessionDetail> sessionDetails = curriculum.getSessionDetails();
        List<Long> sessionDetailIds = sessionDetails.stream()
            .map(SessionDetail::getId)
            .collect(Collectors.toList());
        
        // Lọc session evaluations cho curriculum này
        List<StudentSessionEvaluation> curriculumSessionEvals = allSessionEvaluations.stream()
            .filter(e -> sessionDetailIds.contains(e.getSessionDetailId()))
            .collect(Collectors.toList());
        
        // Tính toán cho curriculum này
        double avgUnderstanding = curriculumSessionEvals.stream()
            .mapToInt(e -> e.getUnderstandingLevel() != null ? e.getUnderstandingLevel() : 0)
            .average()
            .orElse(0);
        
        double avgHomeworkQuality = curriculumSessionEvals.stream()
            .filter(e -> e.getHomeworkQuality() != null)
            .mapToInt(StudentSessionEvaluation::getHomeworkQuality)
            .average()
            .orElse(0);
        
        Optional<StudentCurriculumEvaluation> savedEvaluation = curriculumEvaluationRepository
            .findByStudentIdAndCurriculumId(studentId, curriculum.getId());
        
        // Tính progress riêng cho curriculum này (dựa trên sessions của curriculum)
        int curriculumTotalSessions = sessionDetails.size();
        long curriculumCompletedSessions = curriculumSessionEvals.stream()
            .filter(e -> "COMPLETED".equals(e.getCompletionStatus()))
            .count();
        int curriculumProgress = curriculumTotalSessions > 0 ? 
            (int) (curriculumCompletedSessions * 100 / curriculumTotalSessions) : 0;
        
        return new CurriculumEvaluationDTO(
            curriculum.getId(),
            curriculum.getTitle(),
            savedEvaluation.map(StudentCurriculumEvaluation::getUnderstandingLevel).orElse((int) avgUnderstanding),
            savedEvaluation.map(StudentCurriculumEvaluation::getOverallProgress).orElse(curriculumProgress),
            savedEvaluation.map(StudentCurriculumEvaluation::getTeacherNotes).orElse(null),
            savedEvaluation.map(StudentCurriculumEvaluation::getStrengths).orElse(null),
            savedEvaluation.map(StudentCurriculumEvaluation::getWeaknesses).orElse(null),
            savedEvaluation.map(StudentCurriculumEvaluation::getRecommendations).orElse(null),
            avgHomeworkQuality,
            (int) curriculumCompletedSessions,
            curriculumTotalSessions,
            savedEvaluation.map(StudentCurriculumEvaluation::getUpdatedAt).orElse(null)
        );
    }
    
    /**
     * THÊM MỚI: Tạo đánh giá tổng thể curriculum (chỉ khi chưa có)
     */
    @Transactional
    public CurriculumEvaluationDTO createCurriculumEvaluation(
            Long studentId,
            Long curriculumId,
            CurriculumEvaluationUpdateRequest request,
            Long teacherId) {
        
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy curriculum"));
        
        // Kiểm tra xem đã có đánh giá chưa
        Optional<StudentCurriculumEvaluation> existing = curriculumEvaluationRepository
            .findByStudentIdAndCurriculumId(studentId, curriculum.getId());
        
        if (existing.isPresent()) {
            throw new RuntimeException("Đánh giá đã tồn tại. Vui lòng sử dụng API cập nhật!");
        }
        
        StudentCurriculumEvaluation evaluation = new StudentCurriculumEvaluation();
        evaluation.setStudentId(studentId);
        evaluation.setCurriculumId(curriculum.getId());
        evaluation.setUnderstandingLevel(request.getUnderstandingLevel());
        evaluation.setOverallProgress(request.getOverallProgress());
        evaluation.setTeacherNotes(request.getTeacherNotes());
        evaluation.setStrengths(request.getStrengths());
        evaluation.setWeaknesses(request.getWeaknesses());
        evaluation.setRecommendations(request.getRecommendations());
        evaluation.setLastUpdatedBy(teacherId);
        evaluation.setUpdatedAt(LocalDateTime.now());
        
        curriculumEvaluationRepository.save(evaluation);
        
        return getCurriculumEvaluation(studentId, curriculumId);
    }
    
    /**
     * CẬP NHẬT: Cập nhật đánh giá tổng thể curriculum (tạo mới nếu chưa có)
     */
    @Transactional
    public CurriculumEvaluationDTO updateCurriculumEvaluation(
            Long studentId,
            Long curriculumId,
            CurriculumEvaluationUpdateRequest request,
            Long teacherId) {
        
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy curriculum"));
        
        StudentCurriculumEvaluation evaluation = curriculumEvaluationRepository
            .findByStudentIdAndCurriculumId(studentId, curriculum.getId())
            .orElse(new StudentCurriculumEvaluation());
        
        evaluation.setStudentId(studentId);
        evaluation.setCurriculumId(curriculum.getId());
        evaluation.setUnderstandingLevel(request.getUnderstandingLevel());
        evaluation.setOverallProgress(request.getOverallProgress());
        evaluation.setTeacherNotes(request.getTeacherNotes());
        evaluation.setStrengths(request.getStrengths());
        evaluation.setWeaknesses(request.getWeaknesses());
        evaluation.setRecommendations(request.getRecommendations());
        evaluation.setLastUpdatedBy(teacherId);
        evaluation.setUpdatedAt(LocalDateTime.now());
        
        curriculumEvaluationRepository.save(evaluation);
        
        return getCurriculumEvaluation(studentId, curriculumId);
    }
    
    /**
     * Lấy danh sách đánh giá cho từng session detail theo curriculum
     */
    public Map<Long, List<SessionEvaluationDTO>> getSessionEvaluationsByCurriculum(Long studentId, Long subjectId) {
        List<Curriculum> curriculums = curriculumRepository.findBySubjectId(subjectId);
        
        if (curriculums.isEmpty()) {
            return new HashMap<>();
        }
        
        // Lấy tất cả session evaluations của student
        List<StudentSessionEvaluation> allSavedEvaluations = sessionEvaluationRepository
            .findByStudentId(studentId);
        
        Map<Long, StudentSessionEvaluation> evaluationMap = allSavedEvaluations.stream()
            .collect(Collectors.toMap(StudentSessionEvaluation::getSessionDetailId, e -> e));
        
        Map<Long, List<SessionEvaluationDTO>> result = new LinkedHashMap<>();
        
        for (Curriculum curriculum : curriculums) {
            List<SessionDetail> sessionDetails = curriculum.getSessionDetails();
            
            List<SessionEvaluationDTO> sessionDTOs = sessionDetails.stream()
                .map(sessionDetail -> {
                    StudentSessionEvaluation eval = evaluationMap.get(sessionDetail.getId());
                    return new SessionEvaluationDTO(
                        sessionDetail.getId(),
                        sessionDetail.getSessionNumber(),
                        sessionDetail.getTopic(),
                        eval != null ? eval.getUnderstandingLevel() : null,
                        eval != null ? eval.getCompletionStatus() : "NOT_STARTED",
                        eval != null ? eval.getTeacherNotes() : null,
                        eval != null ? eval.getHomeworkQuality() : null,
                        eval != null ? eval.getParticipationLevel() : null,
                        eval != null ? eval.getUpdatedAt() : null
                    );
                })
                .sorted(Comparator.comparing(
                    SessionEvaluationDTO::getSessionNumber,
                    Comparator.nullsLast(Integer::compareTo)
                ))
                .collect(Collectors.toList());
            
            result.put(curriculum.getId(), sessionDTOs);
        }
        
        return result;
    }
    
    /**
     * Lấy danh sách đánh giá cho tất cả session details (không phân biệt curriculum)
     */
    public List<SessionEvaluationDTO> getSessionEvaluations(Long studentId, Long subjectId) {
        Map<Long, List<SessionEvaluationDTO>> byCurriculum = getSessionEvaluationsByCurriculum(studentId, subjectId);
        // Gộp tất cả session evaluations từ các curriculum
        return byCurriculum.values().stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }
    
    /**
     * Lấy đánh giá cho một session detail cụ thể
     */
    public SessionEvaluationDTO getSessionEvaluation(Long studentId, Long sessionDetailId) {
        StudentSessionEvaluation evaluation = sessionEvaluationRepository
            .findByStudentIdAndSessionDetailId(studentId, sessionDetailId)
            .orElse(null);
        
        SessionDetail sessionDetail = sessionDetailRepository.findById(sessionDetailId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy session detail"));
        
        return new SessionEvaluationDTO(
            sessionDetailId,
            sessionDetail.getSessionNumber(),
            sessionDetail.getTopic(),
            evaluation != null ? evaluation.getUnderstandingLevel() : null,
            evaluation != null ? evaluation.getCompletionStatus() : "NOT_STARTED",
            evaluation != null ? evaluation.getTeacherNotes() : null,
            evaluation != null ? evaluation.getHomeworkQuality() : null,
            evaluation != null ? evaluation.getParticipationLevel() : null,
            evaluation != null ? evaluation.getUpdatedAt() : null
        );
    }
    
    /**
     * THÊM MỚI: Tạo đánh giá cho một session detail (chỉ khi chưa có)
     */
    @Transactional
    public SessionEvaluationDTO createSessionEvaluation(
            Long studentId,
            Long sessionDetailId,
            SessionEvaluationUpdateRequest request,
            Long teacherId) {
        
        // Kiểm tra xem đã có đánh giá chưa
        Optional<StudentSessionEvaluation> existing = sessionEvaluationRepository
            .findByStudentIdAndSessionDetailId(studentId, sessionDetailId);
        
        if (existing.isPresent()) {
            throw new RuntimeException("Đánh giá buổi học đã tồn tại. Vui lòng sử dụng API cập nhật!");
        }
        
        StudentSessionEvaluation evaluation = new StudentSessionEvaluation();
        evaluation.setStudentId(studentId);
        evaluation.setSessionDetailId(sessionDetailId);
        evaluation.setUnderstandingLevel(request.getUnderstandingLevel());
        evaluation.setCompletionStatus(request.getCompletionStatus());
        evaluation.setTeacherNotes(request.getTeacherNotes());
        evaluation.setHomeworkQuality(request.getHomeworkQuality());
        evaluation.setParticipationLevel(request.getParticipationLevel());
        evaluation.setLastUpdatedBy(teacherId);
        evaluation.setUpdatedAt(LocalDateTime.now());
        
        sessionEvaluationRepository.save(evaluation);
        
        return getSessionEvaluation(studentId, sessionDetailId);
    }
    
    /**
     * CẬP NHẬT: Cập nhật đánh giá cho một session detail (tạo mới nếu chưa có)
     */
    @Transactional
    public SessionEvaluationDTO updateSessionEvaluation(
            Long studentId,
            Long sessionDetailId,
            SessionEvaluationUpdateRequest request,
            Long teacherId) {
        
        StudentSessionEvaluation evaluation = sessionEvaluationRepository
            .findByStudentIdAndSessionDetailId(studentId, sessionDetailId)
            .orElse(new StudentSessionEvaluation());
        
        evaluation.setStudentId(studentId);
        evaluation.setSessionDetailId(sessionDetailId);
        evaluation.setUnderstandingLevel(request.getUnderstandingLevel());
        evaluation.setCompletionStatus(request.getCompletionStatus());
        evaluation.setTeacherNotes(request.getTeacherNotes());
        evaluation.setHomeworkQuality(request.getHomeworkQuality());
        evaluation.setParticipationLevel(request.getParticipationLevel());
        evaluation.setLastUpdatedBy(teacherId);
        evaluation.setUpdatedAt(LocalDateTime.now());
        
        sessionEvaluationRepository.save(evaluation);
        
        return getSessionEvaluation(studentId, sessionDetailId);
    }
    
    /**
     * Helper method để lấy attendance statistics
     */
    public Map<String, Integer> getAttendanceStatistics(Long studentId, Long subjectId) {
        Integer totalSessions = attendanceRepository.countTotalSessionsByStudentAndSubject(studentId, subjectId);
        Integer presentCount = attendanceRepository.countPresentByStudentAndSubject(studentId, subjectId);
        Integer absentCount = attendanceRepository.countAbsentByStudentAndSubject(studentId, subjectId);
        Integer lateCount = attendanceRepository.countLateByStudentAndSubject(studentId, subjectId);
        
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalSessions", totalSessions != null ? totalSessions : 0);
        stats.put("presentCount", presentCount != null ? presentCount : 0);
        stats.put("absentCount", absentCount != null ? absentCount : 0);
        stats.put("lateCount", lateCount != null ? lateCount : 0);
        
        return stats;
    }
    
    /**
     * KIỂM TRA: Kiểm tra xem đã có đánh giá curriculum chưa
     */
    public boolean hasCurriculumEvaluation(Long studentId, Long curriculumId) {
        return curriculumEvaluationRepository
            .findByStudentIdAndCurriculumId(studentId, curriculumId)
            .isPresent();
    }
    
    /**
     * KIỂM TRA: Kiểm tra xem đã có đánh giá session chưa
     */
    public boolean hasSessionEvaluation(Long studentId, Long sessionDetailId) {
        return sessionEvaluationRepository
            .findByStudentIdAndSessionDetailId(studentId, sessionDetailId)
            .isPresent();
    }
    
    /**
     * XÓA: Xóa đánh giá curriculum
     */
    @Transactional
    public void deleteCurriculumEvaluation(Long studentId, Long curriculumId) {
        curriculumEvaluationRepository
            .findByStudentIdAndCurriculumId(studentId, curriculumId)
            .ifPresent(curriculumEvaluationRepository::delete);
    }
    
    /**
     * XÓA: Xóa đánh giá session
     */
    @Transactional
    public void deleteSessionEvaluation(Long studentId, Long sessionDetailId) {
        sessionEvaluationRepository
            .findByStudentIdAndSessionDetailId(studentId, sessionDetailId)
            .ifPresent(sessionEvaluationRepository::delete);
    }
}