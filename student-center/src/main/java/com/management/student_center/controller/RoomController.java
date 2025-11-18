package com.management.student_center.controller;

import com.management.student_center.dto.RoomListDTO;
import com.management.student_center.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/v1/api")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/rooms")
    public ResponseEntity<?> getAllRooms() {
        try {
            List<RoomListDTO> rooms = roomService.getAllRooms();

            Map<String, Object> body = new HashMap<>();
            body.put("message", "Lấy danh sách phòng học thành công");
            body.put("data", rooms);

            return ResponseEntity.ok(body);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of(
                            "message", "Có lỗi xảy ra khi lấy danh sách phòng học",
                            "error", e.getMessage()
                    )
            );
        }
    }
}
