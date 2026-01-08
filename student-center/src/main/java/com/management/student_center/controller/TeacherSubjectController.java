package com.management.student_center.controller;

import com.management.student_center.dto.teachersubject.TeacherSubjectRequestDTO;
import com.management.student_center.dto.teachersubject.TeacherSubjectResponseDTO;
import com.management.student_center.service.TeacherSubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/teacher-subjects")
public class TeacherSubjectController {

    private final TeacherSubjectService service;

    public TeacherSubjectController(TeacherSubjectService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
    		@RequestParam(required = false) Integer grade,
            @RequestParam(required = false) String teacherName,
            @RequestParam(required = false) String subjectName
            ) {
        try {
            List<TeacherSubjectResponseDTO> list = service.searchTeacherSubjects(grade, teacherName, subjectName);
            return createSuccessResponse(list);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        try {
            TeacherSubjectResponseDTO dto = service.getTeacherSubjectById(id);
            return createSuccessResponse(dto);
        } catch (Exception e) {
            return createErrorResponse(e, 404);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody TeacherSubjectRequestDTO dto) {
        try {
            service.createTeacherSubject(dto);
            Map<String, Object> response = new HashMap<>();
            response.put("errCode", 0);
            response.put("message", "Tạo thỏa thuận lương thành công");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return createErrorResponse(e, 400);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody TeacherSubjectRequestDTO dto) {
        try {
            service.updateTeacherSubject(id, dto);
            Map<String, Object> response = new HashMap<>();
            response.put("errCode", 0);
            response.put("message", "Cập nhật thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(e, 400);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        try {
            service.deleteTeacherSubject(id);
            Map<String, Object> response = new HashMap<>();
            response.put("errCode", 0);
            response.put("message", "Xóa thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(e, 400);
        }
    }

    // Helper Response
    private ResponseEntity<Map<String, Object>> createSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("errCode", 0);
        response.put("message", "OK");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(Exception e, int status) {
        Map<String, Object> response = new HashMap<>();
        response.put("errCode", status == 500 ? 1 : 1); // Code lỗi tùy bạn định nghĩa
        response.put("message", e.getMessage());
        return ResponseEntity.status(status).body(response);
    }
    
    private ResponseEntity<Map<String, Object>> createErrorResponse(Exception e) {
        return createErrorResponse(e, 500);
    }
}