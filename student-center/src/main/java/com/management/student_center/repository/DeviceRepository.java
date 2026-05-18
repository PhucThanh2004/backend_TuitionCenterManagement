package com.management.student_center.repository;

import com.management.student_center.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByRoomId(Long roomId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Device d WHERE d.room.id = :roomId")
    void deleteByRoomId(Long roomId);
}