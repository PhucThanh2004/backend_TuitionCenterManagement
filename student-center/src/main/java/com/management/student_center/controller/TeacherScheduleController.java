package com.management.student_center.controller;

import com.management.student_center.dto.TeacherScheduleDTO;
import com.management.student_center.service.TeacherScheduleService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/api/teachers")
public class TeacherScheduleController {

    private final TeacherScheduleService scheduleService;

    public TeacherScheduleController(TeacherScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/{teacherId}/schedule")
    public List<TeacherScheduleDTO> getTeacherSchedule(
            @PathVariable Long teacherId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return scheduleService.getTeacherSchedule(teacherId, startDate, endDate);
    }

}
