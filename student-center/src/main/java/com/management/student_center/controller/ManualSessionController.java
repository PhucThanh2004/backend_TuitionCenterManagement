package com.management.student_center.controller;

import com.management.student_center.dto.ManualSessionRequest;
import com.management.student_center.dto.SessionDTO;
import com.management.student_center.service.SubjectScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/v1/api")
public class ManualSessionController {

    private final SubjectScheduleService subjectScheduleService;

    public ManualSessionController(SubjectScheduleService subjectScheduleService) {
        this.subjectScheduleService = subjectScheduleService;
    }

    @PostMapping("/manual-session")
    public ResponseEntity<?> addManualSession(@RequestBody ManualSessionRequest req) {
        try {
            SessionDTO session = subjectScheduleService.addManualSession(req);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("Tạo session thành công", session));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Có lỗi xảy ra khi tạo session", e.getMessage()));
        }
    }

    @PutMapping("/session/{sessionId}")
    public ResponseEntity<?> updateSession(@PathVariable Long sessionId,
                                           @RequestBody ManualSessionRequest req) {
        try {
            SessionDTO session = subjectScheduleService.updateSession(sessionId, req);
            return ResponseEntity.ok(new ApiResponse("Cập nhật buổi học thành công", session));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Có lỗi xảy ra khi cập nhật buổi học", e.getMessage()));
        }
    }

    // Helper class trả về JSON
    static class ApiResponse {
        private String message;
        private Object data;
        public ApiResponse(String message) { this.message = message; }
        public ApiResponse(String message, Object data) { this.message = message; this.data = data; }
        public String getMessage() { return message; }
        public Object getData() { return data; }
    }
}
