package com.management.student_center.controller;

import com.management.student_center.service.StatisticsService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/statistics")
public class StatisticsController {
    
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueChart(@RequestParam int year) {
        Map<String, Object> response = new HashMap<>();
        response.put("errCode", 0);
        response.put("message", "OK");
        response.put("data", statisticsService.getYearlyStatistics(year));
        
        return ResponseEntity.ok(response);
    }
    @GetMapping("/export/revenue")
    public ResponseEntity<byte[]> exportRevenue(@RequestParam int year) throws IOException {
        byte[] excelContent = statisticsService.exportRevenueToExcel(year);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=bao-cao-doanh-thu-" + year + ".xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelContent);
    }
    
    @GetMapping("/subject-revenue")
    public ResponseEntity<?> getSubjectRevenue(@RequestParam int year) {
        return ResponseEntity.ok(statisticsService.getSubjectStatistics(year));
    }

    @GetMapping("/export/subject-revenue")
    public ResponseEntity<byte[]> exportSubjectRevenue(@RequestParam int year) throws IOException {
        byte[] excelContent = statisticsService.exportSubjectRevenueToExcel(year);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=doanh-thu-mon-hoc-" + year + ".xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelContent);
    }
}