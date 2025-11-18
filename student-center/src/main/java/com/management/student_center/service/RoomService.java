package com.management.student_center.service;


import com.management.student_center.dto.RoomListDTO;
import com.management.student_center.entity.Room;
import com.management.student_center.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
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

}
