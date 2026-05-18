	package com.management.student_center.service;
	
	import com.management.student_center.dto.ActiveSubjectDTO;
import com.management.student_center.dto.DeviceDTO;
import com.management.student_center.dto.DeviceUpdateDTO;
import com.management.student_center.dto.RoomListDTO;
	import com.management.student_center.dto.RoomScheduleDTO;
	import com.management.student_center.entity.Device;
	import com.management.student_center.entity.Room;
import com.management.student_center.entity.Session;
import com.management.student_center.enums.RoomManualStatus;
	import com.management.student_center.repository.DeviceRepository;
	import com.management.student_center.repository.RoomRepository;
	import com.management.student_center.repository.SessionRepository;
import com.management.student_center.repository.StudentSubjectRepository;

import org.springframework.stereotype.Service;
	import org.springframework.transaction.annotation.Transactional;
	
	import java.time.LocalDate;
	import java.util.List;
	import java.util.stream.Collectors;
	
	@Service
	public class RoomService {
	
	    private final RoomRepository roomRepository;
	    private final SessionRepository sessionRepository;
	    private final DeviceRepository deviceRepository;
	    private final StudentSubjectRepository studentSubjectRepository;
	
	    public RoomService(RoomRepository roomRepository,
                SessionRepository sessionRepository,
                DeviceRepository deviceRepository,
                StudentSubjectRepository studentSubjectRepository) {
 this.roomRepository = roomRepository;
 this.sessionRepository = sessionRepository;
 this.deviceRepository = deviceRepository;
 this.studentSubjectRepository = studentSubjectRepository;
}
	    
	    // Helper method to determine room status
	    private String determineRoomStatus(Room room) {
	        // Nếu manual status là MAINTENANCE, luôn trả về MAINTENANCE
	        if (room.getManualStatus() == RoomManualStatus.MAINTENANCE) {
	            return "MAINTENANCE";
	        }
	        
	        // Nếu manual status là DISABLED, trả về DISABLED
	        if (room.getManualStatus() == RoomManualStatus.DISABLED) {
	            return "DISABLED";
	        }
	        
	        // CHỈ KIỂM TRA SESSION ĐANG DIỄN RA TẠI THỜI ĐIỂM HIỆN TẠI
	        Long activeSessions = sessionRepository.countActiveSessionsNow(room.getId());
	        
	        // Nếu đang có session hoạt động -> ACTIVE
	        if (activeSessions > 0) {
	            return "ACTIVE";
	        }
	        
	        // Không có session nào đang diễn ra -> DISABLED
	        return "DISABLED";
	    }
	    // Helper method to convert Device to DeviceDTO
	    private List<DeviceDTO> getDeviceDTOs(Long roomId) {
	        return deviceRepository.findByRoomId(roomId)
	            .stream()
	            .map(d -> new DeviceDTO(
	                d.getId(),
	                d.getType() != null ? d.getType().name() : null
	            ))
	            .collect(Collectors.toList());
	    }
	    
	    public List<RoomListDTO> getAllRooms() {
	        return roomRepository.findAll()
	            .stream()
	            .map(r -> new RoomListDTO(
	            	    r.getId(),
	            	    r.getName(),
	            	    r.getSeatCapacity(),
	            	    r.getManualStatus(),
	            	    determineRoomStatus(r),
	            	    getDeviceDTOs(r.getId()),
	            	    getActiveSubjectByRoom(r)   
	            	))
	            .collect(Collectors.toList());
	    }
	    
	    // Get single room details
	    public RoomListDTO getRoomById(Long id) {
	        Room room = roomRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));

	        return new RoomListDTO(
	            room.getId(),
	            room.getName(),
	            room.getSeatCapacity(),
	            room.getManualStatus(),
	            determineRoomStatus(room),
	            getDeviceDTOs(room.getId()),
	            getActiveSubjectByRoom(room)
	        );
	    }
	    
	    // Lấy lịch của 1 phòng theo khoảng ngày
	    public List<RoomScheduleDTO> getRoomSchedule(Long roomId, LocalDate startDate, LocalDate endDate) {
	        // Kiểm tra phòng tồn tại
	        if (!roomRepository.existsById(roomId)) {
	            throw new RuntimeException("Không tìm thấy phòng");
	        }
	        return sessionRepository.findRoomSchedule(roomId, startDate, endDate);
	    }
	    
	    // CREATE ROOM
	    @Transactional
	    public Room createRoom(String name, Integer seatCapacity, RoomManualStatus manualStatus) {
	        Room room = new Room();
	        room.setName(name);
	        room.setSeatCapacity(seatCapacity);
	        if (manualStatus != null) {
	            room.setManualStatus(manualStatus);
	        }
	        return roomRepository.save(room);
	    }
	    
	    // UPDATE ROOM
	    @Transactional
	    public Room updateRoom(Long id, String name, Integer seatCapacity, 
	                           RoomManualStatus manualStatus, List<DeviceUpdateDTO> devices) {
	        // 1. Update thông tin cơ bản
	        Room room = roomRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));
	        
	        if (name != null) room.setName(name);
	        if (seatCapacity != null) room.setSeatCapacity(seatCapacity);
	        if (manualStatus != null) {
	            if (manualStatus == RoomManualStatus.MAINTENANCE || manualStatus == RoomManualStatus.DISABLED) {
	                room.setManualStatus(manualStatus);
	            } else if (manualStatus == RoomManualStatus.ACTIVE) {
	                room.setManualStatus(RoomManualStatus.ACTIVE);
	            }
	        }
	        
	        // 2. Xử lý devices
	        if (devices != null) {
	            // Lấy danh sách ID device hiện tại
	            List<Device> currentDevices = deviceRepository.findByRoomId(id);
	            List<Long> currentDeviceIds = currentDevices.stream()
	                .map(Device::getId)
	                .collect(java.util.stream.Collectors.toList());
	            
	            // Xử lý từng device trong request
	            for (DeviceUpdateDTO deviceDTO : devices) {
	                if ("ADD".equalsIgnoreCase(deviceDTO.getAction())) {
	                    // Thêm device mới
	                    Device newDevice = new Device();
	                    try {
	                        newDevice.setType(
	                            com.management.student_center.enums.DeviceType.valueOf(deviceDTO.getType().toUpperCase())
	                        );
	                    } catch (Exception e) {
	                        throw new RuntimeException("Loại thiết bị không hợp lệ: " + deviceDTO.getType());
	                    }
	                    newDevice.setRoom(room);
	                    deviceRepository.save(newDevice);
	                    
	                } else if ("DELETE".equalsIgnoreCase(deviceDTO.getAction()) && deviceDTO.getId() != null) {
	                    // Xóa device nếu tồn tại
	                    if (currentDeviceIds.contains(deviceDTO.getId())) {
	                        deviceRepository.deleteById(deviceDTO.getId());
	                    }
	                }
	            }
	        }
	        
	        return roomRepository.save(room);
	    }
	    
	    // UPDATE MANUAL STATUS ONLY
	    @Transactional
	    public Room updateManualStatus(Long id, RoomManualStatus manualStatus) {
	        Room room = roomRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));
	        
	        // Chỉ cho phép set MAINTENANCE hoặc ACTIVE (để hệ thống tự động)
	        if (manualStatus == RoomManualStatus.MAINTENANCE) {
	            room.setManualStatus(RoomManualStatus.MAINTENANCE);
	        } else if (manualStatus == RoomManualStatus.ACTIVE) {
	            room.setManualStatus(RoomManualStatus.ACTIVE);
	        } else {
	            throw new RuntimeException("Chỉ được phép cập nhật trạng thái MAINTENANCE hoặc ACTIVE");
	        }
	        
	        return roomRepository.save(room);
	    }
	    
	    // DELETE ROOM
	    @Transactional
	    public void deleteRoom(Long id) {
	        if (!roomRepository.existsById(id)) {
	            throw new RuntimeException("Phòng không tồn tại");
	        }
	        // Xóa tất cả devices trước
	        deviceRepository.deleteByRoomId(id);
	        // Xóa room
	        roomRepository.deleteById(id);
	    }
	    
	    // DEVICE MANAGEMENT
	    @Transactional
	    public Device addDevice(Long roomId, String type) {
	
	        Room room = roomRepository.findById(roomId)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));
	
	        Device device = new Device();
	
	        try {
	            device.setType(
	                com.management.student_center.enums.DeviceType
	                    .valueOf(type.toUpperCase())
	            );
	        } catch (Exception e) {
	            throw new RuntimeException("Loại thiết bị không hợp lệ");
	        }
	
	        device.setRoom(room);
	
	        return deviceRepository.save(device);
	    }
	    
	    @Transactional
	    public void removeDevice(Long deviceId) {
	        if (!deviceRepository.existsById(deviceId)) {
	            throw new RuntimeException("Không tìm thấy thiết bị");
	        }
	        deviceRepository.deleteById(deviceId);
	    }
	    
	    public List<DeviceDTO> getRoomDevices(Long roomId) {
	        return getDeviceDTOs(roomId);
	    }
	    
	    private ActiveSubjectDTO getActiveSubjectByRoom(Room room) {

	        Session session = sessionRepository.findActiveSessionByRoomId(room.getId());

	        if (session == null) return null;

	        Long studentCount = studentSubjectRepository.countBySubjectId(
	            session.getSubject().getId()
	        );

	        return new ActiveSubjectDTO(
	            session.getSubject().getId(),
	            session.getSubject().getName(),
	            session.getStartTime().toString(),
	            session.getEndTime().toString(),
	            studentCount
	        );
	    }
	}