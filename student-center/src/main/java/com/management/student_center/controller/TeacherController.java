package com.management.student_center.controller;

import com.management.student_center.dto.TeacherBasicDTO;
import com.management.student_center.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/teachers/basic")
    public ResponseEntity<Map<String, Object>> getTeacherBasicList() {
        try {
            List<TeacherBasicDTO> data = teacherService.getTeacherBasicList();
            Map<String, Object> response = new HashMap<>();
            response.put("errCode", 0);
            response.put("message", "OK");
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("errCode", 500);
            error.put("message", "Có lỗi xảy ra từ phía máy chủ!");
            return ResponseEntity.status(500).body(error);
        }
    }
}
