package com.management.student_center.controller;

import com.management.student_center.dto.RoomListDTO;
import com.management.student_center.dto.RoomRequestDTO;
import com.management.student_center.dto.RoomScheduleDTO;
import com.management.student_center.entity.Room;
import com.management.student_center.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    
 // --- Mới: Lấy lịch của 1 phòng theo khoảng ngày ---
    @GetMapping("/rooms/{roomId}/schedule")
    public ResponseEntity<?> getRoomSchedule(
            @PathVariable Long roomId,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            List<RoomScheduleDTO> schedule = roomService.getRoomSchedule(roomId, start, end);

            Map<String, Object> body = new HashMap<>();
            body.put("message", "Lấy lịch phòng thành công");
            body.put("data", schedule);

            return ResponseEntity.ok(body);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of(
                            "message", "Có lỗi xảy ra khi lấy lịch phòng",
                            "error", e.getMessage()
                    )
            );
        }
    }
    
 // CREATE ROOM
    // ==========================================
    @PostMapping("/rooms")
    public ResponseEntity<?> createRoom(@RequestBody RoomRequestDTO req) {
        try {
            Room room = roomService.createRoom(req.getName(), req.getSeatCapacity());
            return ResponseEntity.ok(
                    Map.of("message", "Tạo phòng thành công", "data", room)
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of("message", "Tạo phòng thất bại", "error", e.getMessage())
            ); 	 	
        }
    }

    // ==========================================
    // UPDATE ROOM
    // ==========================================
    @PutMapping("/rooms/{id}")
    public ResponseEntity<?> updateRoom(
            @PathVariable Long id,
            @RequestBody RoomRequestDTO req
    ) {
        try {
            Room updated = roomService.updateRoom(id, req.getName(), req.getSeatCapacity());
            return ResponseEntity.ok(
                    Map.of("message", "Cập nhật phòng thành công", "data", updated)
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of("message", "Cập nhật thất bại", "error", e.getMessage())
            );
        }
    }

    // ==========================================
    // DELETE ROOM
    // ==========================================
    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.ok(
                    Map.of("message", "Xóa phòng thành công", "id", id)
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of("message", "Xóa phòng thất bại", "error", e.getMessage())
            );
        }
    }
}