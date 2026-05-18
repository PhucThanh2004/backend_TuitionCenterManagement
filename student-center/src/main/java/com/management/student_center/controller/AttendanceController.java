package com.management.student_center.controller;

import com.management.student_center.dto.AttendanceResponseDTO;
import com.management.student_center.dto.AttendanceStudentDTO;
import com.management.student_center.dto.TodayAttendanceDTO;
import com.management.student_center.entity.Student;
import com.management.student_center.service.AttendanceService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }
    
    @GetMapping("/subjects/{subjectId}/attendance")
    public ResponseEntity<?> getAttendanceByDate(
            @PathVariable Long subjectId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        TodayAttendanceDTO data = attendanceService.getAttendanceByDate(subjectId, date);

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Lấy điểm danh thành công");
        res.put("data", data);

        return ResponseEntity.ok(res);
    }
    
    @GetMapping("/attendance/absent-or-late")
    public List<AttendanceStudentDTO> getAbsentOrLateStudents(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return attendanceService.getAbsentOrLateStudentsInDateRange(startDate, endDate);
    }


    @GetMapping("/subject/{subjectId}/attendance")
    public ResponseEntity<?> getAttendance(@PathVariable Long subjectId) {
        AttendanceResponseDTO data = attendanceService.getAttendanceBySubject(subjectId);
        return ResponseEntity.ok(Map.of(
            "message", "Lấy danh sách học sinh và điểm danh thành công",
            "data", data
        ));
    }

 // PUT /v1/api/attendance/status
    @PutMapping("/attendance/status")
    public ResponseEntity<?> updateStatus(@RequestBody StatusRequest req) {
        String message = attendanceService.updateStatus(req.studentId, req.sessionId, req.status);
        return ResponseEntity.ok(message);
    }

    // PUT /v1/api/attendance/note
    @PutMapping("/attendance/note")
    public ResponseEntity<?> updateNote(@RequestBody NoteRequest req) {
        String message = attendanceService.updateNote(req.studentId, req.sessionId, req.note);
        return ResponseEntity.ok(message);
    }


    public record StatusRequest(Long sessionId, Long studentId, String status) {}
    public record NoteRequest(Long sessionId, Long studentId, String note) {}
}

