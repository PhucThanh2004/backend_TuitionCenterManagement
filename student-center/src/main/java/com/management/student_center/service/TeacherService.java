package com.management.student_center.service;

import com.management.student_center.dto.TeacherBasicDTO;
import com.management.student_center.entity.Teacher;
import com.management.student_center.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<TeacherBasicDTO> getTeacherBasicList() {
        return teacherRepository.findAll()
                .stream()
                .filter(t -> t.getUserInfo() != null && "R1".equals(t.getUserInfo().getRoleId()))
                .map(t -> new TeacherBasicDTO(
                        t.getId(),
                        t.getUserInfo().getId(),
                        t.getUserInfo().getFullName(),
                        t.getUserInfo().getEmail(),
                        t.getUserInfo().getPhoneNumber(),
                        t.getUserInfo().getGender(),
                        t.getSpecialty()
                ))
                .collect(Collectors.toList());
    }
}
