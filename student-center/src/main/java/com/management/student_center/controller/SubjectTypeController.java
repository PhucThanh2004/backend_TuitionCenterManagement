package com.management.student_center.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.management.student_center.dto.subject.SubjectTypeDTO;
import com.management.student_center.service.SubjectTypeService;

@RestController
@RequestMapping("/v1/api/subjects/types")
public class SubjectTypeController {

    @Autowired
    private SubjectTypeService subjectTypeService;

    @GetMapping
    public List<SubjectTypeDTO> getAll() {
        return subjectTypeService.getAll();
    }
}