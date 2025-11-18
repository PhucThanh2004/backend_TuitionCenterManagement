package com.management.student_center.controller;

import com.management.student_center.dto.StudentDTO;
import com.management.student_center.dto.StudentSubjectRequest;
import com.management.student_center.service.StudentSubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
public class StudentSubjectController {

    private final StudentSubjectService studentSubjectService;

    public StudentSubjectController(StudentSubjectService studentSubjectService) {
        this.studentSubjectService = studentSubjectService;
    }

    @GetMapping("/subject-students/{subjectId}")
    public ResponseEntity<?> getStudentsBySubjectId(@PathVariable Long subjectId) {
        try {
            List<StudentDTO> students = studentSubjectService.getStudentsBySubjectId(subjectId);

            // Bọc dữ liệu trong Map
            Map<String, Object> response = new HashMap();
            response.put("message", "Lấy danh sách học sinh theo môn học thành công");
            response.put("data", students);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Có lỗi xảy ra khi lấy danh sách học sinh");
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/students/by-grade/{grade}")
    public ResponseEntity<?> getStudentsByGrade(@PathVariable String grade) {
        try {
            List<StudentDTO> students = studentSubjectService.getStudentsByGrade(grade);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Lấy danh sách học sinh theo khối thành công");
            response.put("data", students);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Có lỗi xảy ra khi lấy danh sách học sinh theo khối");
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/subject-students")
    public ResponseEntity<?> addStudentToSubject(@RequestBody StudentSubjectRequest req) {
        try {
            var ss = studentSubjectService.addStudentToSubject(req.studentId, req.subjectId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Thêm học sinh vào môn học thành công");
            response.put("data", ss);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/subject-students")
    public ResponseEntity<?> removeStudentFromSubject(@RequestBody StudentSubjectRequest req) {
        try {
            studentSubjectService.removeStudentFromSubject(req.studentId, req.subjectId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Xóa học sinh khỏi môn học thành công");
            response.put("data", null);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }


}
