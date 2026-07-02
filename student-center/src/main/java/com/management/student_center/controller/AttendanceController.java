package com.management.student_center.controller;

import com.management.student_center.dto.AttendanceExportDTO;
import com.management.student_center.dto.AttendanceImportDTO;
import com.management.student_center.dto.AttendanceResponseDTO;
import com.management.student_center.dto.AttendanceStudentDTO;
import com.management.student_center.dto.ImportResultDTO;
import com.management.student_center.dto.TodayAttendanceDTO;
import com.management.student_center.dto.studentSubjectDto.AttendanceStatisticsDTO;
import com.management.student_center.entity.Student;
import com.management.student_center.service.AttendanceExportService;
import com.management.student_center.service.AttendanceService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api")
public class AttendanceController {

    private final AttendanceService attendanceService;
    
    @Autowired
    private AttendanceExportService attendanceExportService;

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
    
    @GetMapping("/attendance/statistics/student/{studentId}/subject/{subjectId}")
    public ResponseEntity<AttendanceStatisticsDTO> getStudentAttendanceStatistics(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {
        
        AttendanceStatisticsDTO statistics = attendanceService.getStudentAttendanceBySubject(studentId, subjectId);
        return ResponseEntity.ok(statistics);
    }
    
    @GetMapping("/attendance/export/subject/{subjectId}")
    public ResponseEntity<?> exportAttendanceBySubject(
            @PathVariable Long subjectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "excel") String format) throws IOException {
        
        List<AttendanceExportDTO> data;
        
        // Nếu có startDate và endDate → export theo khoảng thời gian
        if (startDate != null && endDate != null) {
            data = attendanceService.exportAttendanceBySubjectAndDateRange(subjectId, startDate, endDate);
        } else {
            // Nếu không → export tất cả
            data = attendanceService.exportAttendanceBySubject(subjectId);
        }
        
        if (data.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                "message", "Không có dữ liệu điểm danh để export",
                "data", Collections.emptyList()
            ));
        }
        
        if ("excel".equalsIgnoreCase(format)) {
            byte[] excelBytes = attendanceExportService.exportToExcel(data);
            String filename = "attendance_subject_" + subjectId + 
                (startDate != null ? "_" + startDate + "_to_" + endDate : "") + ".xlsx";
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
        } else if ("csv".equalsIgnoreCase(format)) {
            String csvContent = attendanceExportService.exportToCsv(data);
            String filename = "attendance_subject_" + subjectId + 
                (startDate != null ? "_" + startDate + "_to_" + endDate : "") + ".csv";
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                .body(csvContent);
        } else {
            return ResponseEntity.ok(Map.of(
                "data", data,
                "count", data.size(),
                "message", "Export attendance data successfully"
            ));
        }
    }
    
    // ==================== IMPORT ENDPOINT ====================
    
    /**
     * Import attendance từ file
     * POST /v1/api/attendance/import
     * Body: multipart/form-data với file
     */
    @PostMapping("/attendance/import")
    public ResponseEntity<?> importAttendance(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(defaultValue = "excel") String format) throws IOException {
        
        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "File không được để trống"
            ));
        }
        
        // Validate file size (max 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "File quá lớn. Vui lòng chọn file nhỏ hơn 10MB"
            ));
        }
        
        // Parse file
        List<AttendanceImportDTO> importData;
        try {
            if ("excel".equalsIgnoreCase(format)) {
                importData = attendanceExportService.importFromExcel(file.getInputStream());
            } else if ("csv".equalsIgnoreCase(format)) {
                importData = attendanceExportService.importFromCsv(file.getInputStream());
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "message", "Format không hỗ trợ. Chấp nhận: excel, csv"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Không thể đọc file: " + e.getMessage()
            ));
        }
        
        // Validate data
        if (importData.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", "File không chứa dữ liệu hợp lệ"
            ));
        }
        
        // Process import
        ImportResultDTO result = attendanceService.importAttendance(importData);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", result.getMessage());
        response.put("success_count", result.getSuccessCount());
        response.put("error_count", result.getErrorCount());
        response.put("total_records", result.getTotalRecords());
        response.put("errors", result.getErrors());
        
        if (result.getErrorCount() > 0) {
            response.put("status", "partial_success");
        } else {
            response.put("status", "success");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== TEMPLATE DOWNLOAD ====================
    
    /**
     * Download template import
     */
    @GetMapping("/attendance/import/template")
    public ResponseEntity<?> downloadImportTemplate(
            @RequestParam(defaultValue = "excel") String format) throws IOException {
        
        byte[] templateBytes = attendanceExportService.generateImportTemplate(format);
        
        String extension = "excel".equalsIgnoreCase(format) ? "xlsx" : "csv";
        String filename = "attendance_import_template." + extension;
        String contentType = "excel".equalsIgnoreCase(format) ? 
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : 
            "text/csv";
        
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=" + filename)
            .header("Content-Type", contentType)
            .body(templateBytes);
    }
}

