package com.management.student_center.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.management.student_center.dto.AcademicLevelDTO;
import com.management.student_center.dto.subject.SubjectTypeDTO;
import com.management.student_center.entity.SubjectType;
import com.management.student_center.repository.SubjectTypeRepository;

@Service
public class SubjectTypeService {

    @Autowired
    private SubjectTypeRepository subjectTypeRepository;

    public List<SubjectTypeDTO> getAll() {
        List<SubjectType> list = subjectTypeRepository.findAll();

        return list.stream().map(st -> {
            SubjectTypeDTO dto = new SubjectTypeDTO();
            dto.setId(st.getId());
            dto.setName(st.getName());

            if (st.getAcademicLevel() != null) {
                AcademicLevelDTO levelDTO = new AcademicLevelDTO();
                levelDTO.setId(st.getAcademicLevel().getId());
                levelDTO.setName(st.getAcademicLevel().getName());

                dto.setAcademicLevel(levelDTO);
            }

            return dto;
        }).toList();
    }
}
