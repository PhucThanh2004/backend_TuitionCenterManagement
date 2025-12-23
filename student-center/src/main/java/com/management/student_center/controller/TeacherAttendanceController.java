package com.management.student_center.controller;

import com.management.student_center.dto.TeacherAttendanceResponseDTO;
import com.management.student_center.service.TeacherAttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/teacher-attendance")
public class TeacherAttendanceController {

    private final TeacherAttendanceService teacherAttendanceService;

    public TeacherAttendanceController(TeacherAttendanceService teacherAttendanceService) {
        this.teacherAttendanceService = teacherAttendanceService;
    }

    @GetMapping("/subject/{subjectId}/teacher-attendance")
    public ResponseEntity<?> getAttendance(@PathVariable Long subjectId) {
        TeacherAttendanceResponseDTO data = teacherAttendanceService.getAttendanceBySubject(subjectId);
        return ResponseEntity.ok(Map.of(
                "message", "Lấy danh sách giáo viên và điểm danh thành công",
                "data", data
        ));
    }

    @PutMapping("/teacher-attendance/status")
    public ResponseEntity<?> updateStatus(@RequestBody StatusRequest req) {
        String message = teacherAttendanceService.updateStatus(req.teacherId, req.sessionId, req.status);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/teacher-attendance/note")
    public ResponseEntity<?> updateNote(@RequestBody NoteRequest req) {
        String message = teacherAttendanceService.updateNote(req.teacherId, req.sessionId, req.note);
        return ResponseEntity.ok(message);
    }

    public record StatusRequest(Long sessionId, Long teacherId, String status) {}
    public record NoteRequest(Long sessionId, Long teacherId, String note) {}
}
