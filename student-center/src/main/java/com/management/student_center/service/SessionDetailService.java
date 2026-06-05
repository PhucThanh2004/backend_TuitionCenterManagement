package com.management.student_center.service;

import com.management.student_center.dto.*;
import com.management.student_center.entity.*;
import com.management.student_center.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SessionDetailService {

    @Autowired
    private SessionDetailRepository sessionDetailRepository;
    
    @Autowired
    private CurriculumRepository curriculumRepository;
    
    @Autowired
    private SessionRepository sessionRepository;

    // ✅ Lấy danh sách theo display_order
    public List<SessionDetailResponseDTO> getSessionDetailsByCurriculum(Long curriculumId) {
        List<SessionDetail> details = sessionDetailRepository.findByCurriculumIdOrderByDisplayOrderAsc(curriculumId);
        return details.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public SessionDetailResponseDTO getSessionDetailById(Long id) {
        SessionDetail detail = sessionDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết buổi học với id: " + id));
        return convertToResponseDTO(detail);
    }

    // ✅ Tạo mới với display_order tự động
    public SessionDetailResponseDTO createSessionDetail(Long curriculumId, SessionDetailRequestDTO request) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lộ trình với id: " + curriculumId));
        
        // Tính display_order mới
        Integer maxDisplayOrder = sessionDetailRepository.findMaxDisplayOrderByCurriculumId(curriculumId);
        int newDisplayOrder = (maxDisplayOrder == null ? 0 : maxDisplayOrder + 1);
        
        // Kiểm tra sessionNumber nếu có (không bắt buộc)
        if (request.getSessionNumber() != null) {
            if (sessionDetailRepository.existsByCurriculumIdAndSessionNumber(curriculumId, request.getSessionNumber())) {
                throw new RuntimeException("Buổi thứ " + request.getSessionNumber() + " đã tồn tại trong lộ trình này");
            }
        }
        
        SessionDetail detail = new SessionDetail();
        detail.setCurriculum(curriculum);
        detail.setSessionNumber(request.getSessionNumber()); // Có thể null
        detail.setDisplayOrder(newDisplayOrder);
        detail.setTopic(request.getTopic());
        detail.setObjectives(request.getObjectives());
        detail.setContent(request.getContent());
        detail.setHomework(request.getHomework());
        detail.setMaterials(request.getMaterials());
        detail.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 90);
        detail.setTeachingMethod(request.getTeachingMethod());
        detail.setAssessmentCriteria(request.getAssessmentCriteria());
        
        SessionDetail savedDetail = sessionDetailRepository.save(detail);
        
        // Cập nhật expected_sessions của curriculum
        curriculum.setExpectedSessions(curriculum.getSessionDetails().size());
        curriculumRepository.save(curriculum);
        
        return convertToResponseDTO(savedDetail);
    }

    // ✅ Tạo mới tại vị trí chỉ định
    public SessionDetailResponseDTO createSessionDetailAtPosition(Long curriculumId, SessionDetailRequestDTO request, Integer position) {
        Curriculum curriculum = curriculumRepository.findById(curriculumId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lộ trình với id: " + curriculumId));
        
        // Kiểm tra sessionNumber nếu có
        if (request.getSessionNumber() != null) {
            if (sessionDetailRepository.existsByCurriculumIdAndSessionNumber(curriculumId, request.getSessionNumber())) {
                throw new RuntimeException("Buổi thứ " + request.getSessionNumber() + " đã tồn tại");
            }
        }
        
        // Lấy tất cả details hiện tại
        List<SessionDetail> existingDetails = sessionDetailRepository.findByCurriculumIdOrderByDisplayOrderAsc(curriculumId);
        
        // Tạo detail mới
        SessionDetail newDetail = new SessionDetail();
        newDetail.setCurriculum(curriculum);
        newDetail.setSessionNumber(request.getSessionNumber());
        newDetail.setTopic(request.getTopic());
        newDetail.setObjectives(request.getObjectives());
        newDetail.setContent(request.getContent());
        newDetail.setHomework(request.getHomework());
        newDetail.setMaterials(request.getMaterials());
        newDetail.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 90);
        newDetail.setTeachingMethod(request.getTeachingMethod());
        newDetail.setAssessmentCriteria(request.getAssessmentCriteria());
        
        // Lưu tạm để có ID
        SessionDetail saved = sessionDetailRepository.save(newDetail);
        
        // Chèn vào vị trí mong muốn
        int insertPosition = (position == null || position >= existingDetails.size()) ? existingDetails.size() : position;
        
        // Cập nhật display_order cho các item bị ảnh hưởng
        for (SessionDetail detail : existingDetails) {
            if (detail.getDisplayOrder() >= insertPosition) {
                detail.setDisplayOrder(detail.getDisplayOrder() + 1);
                sessionDetailRepository.save(detail);
            }
        }
        saved.setDisplayOrder(insertPosition);
        sessionDetailRepository.save(saved);
        
        // Cập nhật expected_sessions
        curriculum.setExpectedSessions(curriculum.getSessionDetails().size());
        curriculumRepository.save(curriculum);
        
        return convertToResponseDTO(saved);
    }

    // ✅ Cập nhật session detail (không cho update display_order)
    public SessionDetailResponseDTO updateSessionDetail(Long id, SessionDetailRequestDTO request) {
        SessionDetail detail = sessionDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết buổi học với id: " + id));
        
        detail.setTopic(request.getTopic());
        detail.setObjectives(request.getObjectives());
        detail.setContent(request.getContent());
        detail.setHomework(request.getHomework());
        detail.setMaterials(request.getMaterials());
        detail.setDurationMinutes(request.getDurationMinutes());
        detail.setTeachingMethod(request.getTeachingMethod());
        detail.setAssessmentCriteria(request.getAssessmentCriteria());
        detail.setUpdatedAt(LocalDateTime.now());
        
        // Nếu cập nhật session_number
        if (request.getSessionNumber() != null && !request.getSessionNumber().equals(detail.getSessionNumber())) {
            Long curriculumId = detail.getCurriculum().getId();
            if (sessionDetailRepository.existsByCurriculumIdAndSessionNumber(curriculumId, request.getSessionNumber())) {
                throw new RuntimeException("Buổi thứ " + request.getSessionNumber() + " đã tồn tại");
            }
            detail.setSessionNumber(request.getSessionNumber());
        }
        
        SessionDetail updatedDetail = sessionDetailRepository.save(detail);
        return convertToResponseDTO(updatedDetail);
    }

    // ✅ Cập nhật display_order (cho drag & drop)
    @Transactional
    public void updateDisplayOrder(Long id, Integer newDisplayOrder) {
        SessionDetail detail = sessionDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết buổi học với id: " + id));
        
        Long curriculumId = detail.getCurriculum().getId();
        Integer oldDisplayOrder = detail.getDisplayOrder();
        
        if (oldDisplayOrder.equals(newDisplayOrder)) {
            return;
        }
        
        List<SessionDetail> details = sessionDetailRepository.findByCurriculumIdOrderByDisplayOrderAsc(curriculumId);
        
        if (newDisplayOrder < 0) newDisplayOrder = 0;
        if (newDisplayOrder >= details.size()) newDisplayOrder = details.size() - 1;
        
        // Xóa detail khỏi list
        details.remove(detail);
        // Chèn vào vị trí mới
        details.add(newDisplayOrder, detail);
        
        // Cập nhật lại display_order cho tất cả
        for (int i = 0; i < details.size(); i++) {
            details.get(i).setDisplayOrder(i);
            sessionDetailRepository.save(details.get(i));
        }
    }

    // ✅ Xóa session detail
    public void deleteSessionDetail(Long id) {
        SessionDetail detail = sessionDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết buổi học với id: " + id));
        
        // Kiểm tra xem có session thực tế nào đang sử dụng không
        boolean isUsed = sessionRepository.existsByPlannedSessionDetailId(id);
        if (isUsed) {
            throw new RuntimeException("Không thể xóa vì đã có buổi học thực tế sử dụng nội dung này");
        }
        
        Long curriculumId = detail.getCurriculum().getId();
        Integer deletedOrder = detail.getDisplayOrder();
        
        sessionDetailRepository.delete(detail);
        
        // Cập nhật lại display_order cho các detail còn lại
        List<SessionDetail> remainingDetails = sessionDetailRepository.findByCurriculumIdOrderByDisplayOrderAsc(curriculumId);
        for (SessionDetail remaining : remainingDetails) {
            if (remaining.getDisplayOrder() > deletedOrder) {
                remaining.setDisplayOrder(remaining.getDisplayOrder() - 1);
                sessionDetailRepository.save(remaining);
            }
        }
        
        // Cập nhật lại expected_sessions
        Curriculum curriculum = curriculumRepository.findById(curriculumId).orElse(null);
        if (curriculum != null) {
            curriculum.setExpectedSessions(curriculum.getSessionDetails().size());
            curriculumRepository.save(curriculum);
        }
    }

    // ✅ Tạo batch session details
    public List<SessionDetailResponseDTO> createBatchSessionDetails(Long curriculumId, List<SessionDetailRequestDTO> requests) {
        List<SessionDetailResponseDTO> results = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            try {
                SessionDetailResponseDTO created = createSessionDetail(curriculumId, requests.get(i));
                results.add(created);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi khi tạo buổi thứ " + (i + 1) + ": " + e.getMessage());
            }
        }
        return results;
    }

    // ✅ Copy session details từ lộ trình khác
    public List<SessionDetailResponseDTO> copySessionDetails(Long targetCurriculumId, Long sourceCurriculumId, boolean overrideExisting) {
        Curriculum targetCurriculum = curriculumRepository.findById(targetCurriculumId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lộ trình đích"));
        
        List<SessionDetail> sourceDetails = sessionDetailRepository.findByCurriculumIdOrderByDisplayOrderAsc(sourceCurriculumId);
        
        if (!overrideExisting && sessionDetailRepository.countByCurriculumId(targetCurriculumId) > 0) {
            throw new RuntimeException("Lộ trình đích đã có nội dung. Hãy set overrideExisting=true để ghi đè");
        }
        
        if (overrideExisting) {
            List<SessionDetail> existingDetails = sessionDetailRepository.findByCurriculumId(targetCurriculumId);
            sessionDetailRepository.deleteAll(existingDetails);
        }
        
        List<SessionDetailResponseDTO> results = new ArrayList<>();
        for (int i = 0; i < sourceDetails.size(); i++) {
            SessionDetail source = sourceDetails.get(i);
            SessionDetail newDetail = new SessionDetail();
            newDetail.setCurriculum(targetCurriculum);
            newDetail.setSessionNumber(source.getSessionNumber());
            newDetail.setDisplayOrder(i); // Gán display_order theo thứ tự
            newDetail.setTopic(source.getTopic());
            newDetail.setObjectives(source.getObjectives());
            newDetail.setContent(source.getContent());
            newDetail.setHomework(source.getHomework());
            newDetail.setMaterials(source.getMaterials());
            newDetail.setDurationMinutes(source.getDurationMinutes());
            newDetail.setTeachingMethod(source.getTeachingMethod());
            newDetail.setAssessmentCriteria(source.getAssessmentCriteria());
            
            SessionDetail saved = sessionDetailRepository.save(newDetail);
            results.add(convertToResponseDTO(saved));
        }
        
        targetCurriculum.setExpectedSessions(results.size());
        curriculumRepository.save(targetCurriculum);
        
        return results;
    }

    // ✅ Sắp xếp lại thứ tự (dùng display_order)
    @Transactional
    public void reorderSessionDetails(Long curriculumId, List<Long> sessionDetailIdsInOrder) {
        // Kiểm tra tất cả id đều thuộc curriculum này
        for (Long detailId : sessionDetailIdsInOrder) {
            SessionDetail detail = sessionDetailRepository.findById(detailId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy detail với id: " + detailId));
            if (!detail.getCurriculum().getId().equals(curriculumId)) {
                throw new RuntimeException("Detail id " + detailId + " không thuộc lộ trình này");
            }
        }
        
        // Cập nhật display_order theo thứ tự mới
        for (int i = 0; i < sessionDetailIdsInOrder.size(); i++) {
            Long detailId = sessionDetailIdsInOrder.get(i);
            SessionDetail detail = sessionDetailRepository.findById(detailId).get();
            detail.setDisplayOrder(i);
            detail.setUpdatedAt(LocalDateTime.now());
            sessionDetailRepository.save(detail);
        }
    }
    
    // ✅ Sắp xếp lại theo session_number (dành cho trường công có số buổi cứng)
    @Transactional
    public void reorderBySessionNumber(Long curriculumId) {
        List<SessionDetail> details = sessionDetailRepository.findByCurriculumIdOrderBySessionNumberAsc(curriculumId);
        for (int i = 0; i < details.size(); i++) {
            details.get(i).setDisplayOrder(i);
            sessionDetailRepository.save(details.get(i));
        }
    }

    // ✅ Convert to DTO
    private SessionDetailResponseDTO convertToResponseDTO(SessionDetail detail) {
        SessionDetailResponseDTO dto = new SessionDetailResponseDTO();
        dto.setId(detail.getId());
        dto.setCurriculumId(detail.getCurriculum().getId());
        dto.setCurriculumTitle(detail.getCurriculum().getTitle());
        dto.setSessionNumber(detail.getSessionNumber());
        dto.setDisplayOrder(detail.getDisplayOrder());
        dto.setTopic(detail.getTopic());
        dto.setObjectives(detail.getObjectives());
        dto.setContent(detail.getContent());
        dto.setHomework(detail.getHomework());
        dto.setMaterials(detail.getMaterials());
        dto.setDurationMinutes(detail.getDurationMinutes());
        dto.setTeachingMethod(detail.getTeachingMethod());
        dto.setAssessmentCriteria(detail.getAssessmentCriteria());
        dto.setCreatedAt(detail.getCreatedAt());
        dto.setUpdatedAt(detail.getUpdatedAt());
        return dto;
    }
}