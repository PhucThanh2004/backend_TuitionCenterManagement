package com.management.student_center.service;

import com.management.student_center.dto.*;
import com.management.student_center.entity.*;
import com.management.student_center.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CurriculumService {

    @Autowired
    private CurriculumRepository curriculumRepository;
    
    @Autowired
    private SubjectRepository subjectRepository;
    
    @Autowired
    private SessionDetailRepository sessionDetailRepository;

    public List<CurriculumResponseDTO> getAllCurriculums(Long subjectId) {
        List<Curriculum> curriculums;
        if (subjectId != null) {
            curriculums = curriculumRepository.findBySubjectId(subjectId);
        } else {
            curriculums = curriculumRepository.findAll();
        }
        return curriculums.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public CurriculumDetailDTO getCurriculumById(Long id) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lộ trình với id: " + id));
        return convertToDetailDTO(curriculum);
    }

    public CurriculumResponseDTO createCurriculum(Long subjectId, CurriculumRequestDTO request) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với id: " + subjectId));
        
        Curriculum curriculum = new Curriculum();
        curriculum.setSubject(subject);
        curriculum.setTitle(request.getTitle());
        curriculum.setDescription(request.getDescription());
        curriculum.setOrderIndex(request.getOrderIndex() != null ? request.getOrderIndex() : 0);
        curriculum.setExpectedSessions(request.getExpectedSessions() != null ? request.getExpectedSessions() : 0);
        curriculum.setStatus(request.getStatus() != null ? request.getStatus() : "active");
        curriculum.setSemester(request.getSemester());
        curriculum.setSchoolYear(request.getSchoolYear());
        
        Curriculum savedCurriculum = curriculumRepository.save(curriculum);
        return convertToResponseDTO(savedCurriculum);
    }

    public CurriculumResponseDTO updateCurriculum(Long id, CurriculumRequestDTO request) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lộ trình với id: " + id));
        
        curriculum.setTitle(request.getTitle());
        curriculum.setDescription(request.getDescription());
        curriculum.setOrderIndex(request.getOrderIndex());
        curriculum.setExpectedSessions(request.getExpectedSessions());
        curriculum.setStatus(request.getStatus());
        curriculum.setSemester(request.getSemester());
        curriculum.setSchoolYear(request.getSchoolYear());
        curriculum.setUpdatedAt(LocalDateTime.now());
        
        Curriculum updatedCurriculum = curriculumRepository.save(curriculum);
        return convertToResponseDTO(updatedCurriculum);
    }

    public void deleteCurriculum(Long id) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lộ trình với id: " + id));
        
        // Kiểm tra xem có session nào đang sử dụng không
        long usingCount = sessionDetailRepository.countByCurriculumIdAndIsUsed(id);
        if (usingCount > 0) {
            throw new RuntimeException("Không thể xóa lộ trình đã có buổi học được tạo");
        }
        
        curriculumRepository.delete(curriculum);
    }

    public void updateStatus(Long id, String status) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lộ trình với id: " + id));
        curriculum.setStatus(status);
        curriculum.setUpdatedAt(LocalDateTime.now());
        curriculumRepository.save(curriculum);
    }

    public List<CurriculumResponseDTO> getCurriculumsBySubject(Long subjectId) {
        List<Curriculum> curriculums = curriculumRepository.findBySubjectIdOrderByOrderIndex(subjectId);
        return curriculums.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // Conversion methods
    private CurriculumResponseDTO convertToResponseDTO(Curriculum curriculum) {
        CurriculumResponseDTO dto = new CurriculumResponseDTO();
        dto.setId(curriculum.getId());
        dto.setSubjectId(curriculum.getSubject().getId());
        dto.setSubjectName(curriculum.getSubject().getName());
        dto.setTitle(curriculum.getTitle());
        dto.setDescription(curriculum.getDescription());
        dto.setOrderIndex(curriculum.getOrderIndex());
        dto.setExpectedSessions(curriculum.getExpectedSessions());
        dto.setStatus(curriculum.getStatus());
        dto.setSemester(curriculum.getSemester());
        dto.setSchoolYear(curriculum.getSchoolYear());
        dto.setTotalSessionDetails(curriculum.getSessionDetails().size());
        dto.setCreatedAt(curriculum.getCreatedAt());
        dto.setUpdatedAt(curriculum.getUpdatedAt());
        return dto;
    }
    
    private CurriculumDetailDTO convertToDetailDTO(Curriculum curriculum) {
        CurriculumDetailDTO dto = new CurriculumDetailDTO();
        dto.setId(curriculum.getId());
        dto.setSubjectId(curriculum.getSubject().getId());
        dto.setSubjectName(curriculum.getSubject().getName());
        dto.setTitle(curriculum.getTitle());
        dto.setDescription(curriculum.getDescription());
        dto.setOrderIndex(curriculum.getOrderIndex());
        dto.setExpectedSessions(curriculum.getExpectedSessions());
        dto.setStatus(curriculum.getStatus());
        dto.setSemester(curriculum.getSemester());
        dto.setSchoolYear(curriculum.getSchoolYear());
        dto.setTotalSessionDetails(curriculum.getSessionDetails().size());
        dto.setCreatedAt(curriculum.getCreatedAt());
        dto.setUpdatedAt(curriculum.getUpdatedAt());
        
        List<SessionDetailResponseDTO> sessionDetails = curriculum.getSessionDetails().stream()
                .map(this::convertToSessionDetailDTO)
                .collect(Collectors.toList());
        dto.setSessionDetails(sessionDetails);
        
        return dto;
    }
    
    private SessionDetailResponseDTO convertToSessionDetailDTO(SessionDetail detail) {
        SessionDetailResponseDTO dto = new SessionDetailResponseDTO();
        dto.setId(detail.getId());
        dto.setCurriculumId(detail.getCurriculum().getId());
        dto.setCurriculumTitle(detail.getCurriculum().getTitle());
        dto.setSessionNumber(detail.getSessionNumber());
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