package com.management.student_center.dto;

import com.management.student_center.enums.RoomManualStatus;
import java.util.List;

public class RoomListDTO {
    private Long id;
    private String name;
    private Integer seatCapacity;
    private RoomManualStatus manualStatus;
    private String status; // ACTIVE/DISABLED based on sessions
    private List<DeviceDTO> devices;
    private ActiveSubjectDTO activeSession;

    public RoomListDTO(Long id, String name, Integer seatCapacity, 
                       RoomManualStatus manualStatus, String status, List<DeviceDTO> devices, ActiveSubjectDTO activeSession) {
        this.id = id;
        this.name = name;
        this.seatCapacity = seatCapacity;
        this.manualStatus = manualStatus;
        this.status = status;
        this.devices = devices;
        this.activeSession = activeSession;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getSeatCapacity() { return seatCapacity; }
    public void setSeatCapacity(Integer seatCapacity) { this.seatCapacity = seatCapacity; }

    public RoomManualStatus getManualStatus() { return manualStatus; }
    public void setManualStatus(RoomManualStatus manualStatus) { this.manualStatus = manualStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<DeviceDTO> getDevices() { return devices; }
    public void setDevices(List<DeviceDTO> devices) { this.devices = devices; }

	public ActiveSubjectDTO getActiveSession() {
		return activeSession;
	}

	public void setActiveSession(ActiveSubjectDTO activeSession) {
		this.activeSession = activeSession;
	}
    
}