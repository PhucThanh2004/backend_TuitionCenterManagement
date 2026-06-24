package com.management.student_center.controller;

import com.management.student_center.dto.ActivityLogResponse;
import com.management.student_center.service.ActivityLogService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/activity-logs")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(
            ActivityLogService activityLogService
    ) {
        this.activityLogService = activityLogService;
    }

    // =========================
    // ADMIN FEED
    // =========================

    @GetMapping("/recent/admin")
    public List<ActivityLogResponse> getAdminActivities(
            @RequestParam(defaultValue = "10") int limit
    ) {

        return activityLogService.getAdminActivities(limit);
    }

    // =========================
    // TEACHER FEED
    // =========================

    @GetMapping("/recent/teacher/{userId}")
    public List<ActivityLogResponse> getTeacherActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {

        return activityLogService.getTeacherActivities(
                userId,
                limit
        );
    }

    // =========================
    // MARK AS READ APIs
    // =========================

    /**
     * Đánh dấu một activity log cụ thể đã đọc
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(
            @PathVariable Long id
    ) {
        activityLogService.markAsRead(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Activity log marked as read successfully");
        response.put("id", id.toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Đánh dấu tất cả activity log của user đã đọc
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(
            @PathVariable Long userId
    ) {
        activityLogService.markAllAsRead(userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "All activity logs marked as read successfully");
        response.put("userId", userId.toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy số lượng activity log chưa đọc của user
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @PathVariable Long userId
    ) {
        long unreadCount = activityLogService.getUnreadCount(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("unreadCount", unreadCount);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách activity log chưa đọc của user
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<ActivityLogResponse>> getUnreadActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<ActivityLogResponse> unreadLogs = activityLogService.getUnreadActivities(userId, limit);
        return ResponseEntity.ok(unreadLogs);
    }

    // =========================
    // GET ACTIVITIES BY USER
    // =========================

    /**
     * Lấy tất cả activity log của user (bao gồm cả đã đọc và chưa đọc)
     */
    @GetMapping("/user/{userId}")
    public List<ActivityLogResponse> getActivitiesByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return activityLogService.getTeacherActivities(userId, limit);
    }
}