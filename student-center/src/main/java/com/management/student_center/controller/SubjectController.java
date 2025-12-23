package com.management.student_center.controller;

import com.management.student_center.dto.CreateSubjectRequest;
import com.management.student_center.dto.SubjectDTO;
import com.management.student_center.dto.UpdateSubjectRequest;
import com.management.student_center.entity.Subject;
import com.management.student_center.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/v1/api")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;
    
    @GetMapping("/subjects/teacher/{userId}")
    public Map<String, Object> getSubjectsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int limit,
            @RequestParam(required = false) String status
    ) {
        return subjectService.getSubjectsByUserId(page, limit, status, userId);
    }
    
 // ------------------- GET ALL SUBJECTS (NO PAGINATION) -------------------
    @GetMapping("/subjects/all")
    public Map<String, Object> getAllSubjectsNoPaging(
            @RequestParam(required = false) String status
    ) {
        return subjectService.getAllSubjectsNoPaging(status);
    }

    @GetMapping("/subjects")
    public Map<String, Object> getSubjects(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int limit,
            @RequestParam(required = false) String status
    ) {
        return subjectService.getAllSubjects(page, limit, status);
    }

    @GetMapping("/subjects/{id}")
    public Map<String, Object> getSubjectById(@PathVariable Long id) {
        SubjectDTO dto = subjectService.getSubjectById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", dto);
        return response;
    }

    // ------------------- DELETE SUBJECT -------------------
    @DeleteMapping("/subjects/{id}")
    public Map<String, Object> deleteSubject(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            subjectService.deleteSubject(id);
            response.put("success", true);
            response.put("message", "Xóa môn học thành công.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    
    @PutMapping("/subjects/{id}")
    public Map<String, Object> updateSubject(
            @PathVariable Long id,
            @RequestBody UpdateSubjectRequest updatedData
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            subjectService.updateSubject(id, updatedData);
            response.put("success", true);
            response.put("message", "Cập nhật môn học thành công.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    @PostMapping("/subjects")
    public Map<String, Object> createSubject(
            @ModelAttribute CreateSubjectRequest request
    ) {
    	System.out.println("maxStudents = " + request.getMaxStudents());
        System.out.println("sessionsPerWeek = " + request.getSessionsPerWeek());
        System.out.println("price = " + request.getPrice());
        Map<String, Object> response = new HashMap<>();
        try {
            Subject subject = subjectService.createSubject(request);
            response.put("success", true);
            response.put("message", "Tạo môn học mới thành công.");
            response.put("data", subject);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

}
