package com.management.student_center.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.management.student_center.dto.DailySessionDTO;
import com.management.student_center.dto.UpcomingSessionDTO;
import com.management.student_center.service.SessionService;

@RestController
@RequestMapping("/v1/api/session")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @GetMapping("/daily")
    public List<DailySessionDTO> getSessionsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return sessionService.getSessionsByDate(date);
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<UpcomingSessionDTO>> getUpcomingSessions() {
        return ResponseEntity.ok(sessionService.getTop4UpcomingSessions());
    }
}
