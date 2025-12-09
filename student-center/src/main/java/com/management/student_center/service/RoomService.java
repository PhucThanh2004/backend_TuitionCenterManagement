package com.management.student_center.service;


import com.management.student_center.dto.RoomListDTO;
import com.management.student_center.dto.RoomScheduleDTO;
import com.management.student_center.entity.Room;
import com.management.student_center.repository.RoomRepository;
import com.management.student_center.repository.SessionRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final SessionRepository sessionRepository;

    public RoomService(RoomRepository roomRepository, SessionRepository sessionRepository) {
        this.roomRepository = roomRepository;
        this.sessionRepository = sessionRepository;
    }
    public List<RoomListDTO> getAllRooms() {
        return roomRepository.findAll()
            .stream()
            .map(r -> new RoomListDTO(
                    r.getId(),
                    r.getName(),
                    r.getSeatCapacity()
            ))
            .collect(Collectors.toList());
    }
    
 // lấy lịch của 1 phòng theo khoảng ngày
    public List<RoomScheduleDTO> getRoomSchedule(Long roomId, LocalDate startDate, LocalDate endDate) {
        return sessionRepository.findRoomSchedule(roomId, startDate, endDate);
    }
    
 // CREATE ROOM
    // =====================
    public Room createRoom(String name, Integer seatCapacity) {
        Room room = new Room();
        room.setName(name);
        room.setSeatCapacity(seatCapacity);
        return roomRepository.save(room);
    }

    // =====================
    // UPDATE ROOM
    // =====================
    public Room updateRoom(Long id, String name, Integer seatCapacity) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));

        room.setName(name);
        room.setSeatCapacity(seatCapacity);

        return roomRepository.save(room);
    }

    // =====================
    // DELETE ROOM
    // =====================
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Phòng không tồn tại");
        }
        roomRepository.deleteById(id);
    }

}