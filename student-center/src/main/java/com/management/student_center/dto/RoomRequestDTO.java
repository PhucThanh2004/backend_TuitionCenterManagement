package com.management.student_center.dto;

import java.util.List;

import com.management.student_center.enums.RoomManualStatus;

public class RoomRequestDTO {
    private String name;
    private Integer seatCapacity;
    private RoomManualStatus manualStatus;
    private List<DeviceUpdateDTO> devices;

    public RoomRequestDTO() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getSeatCapacity() { return seatCapacity; }
    public void setSeatCapacity(Integer seatCapacity) { this.seatCapacity = seatCapacity; }
    
    public RoomManualStatus getManualStatus() { return manualStatus; }
    public void setManualStatus(RoomManualStatus manualStatus) { this.manualStatus = manualStatus; }
    
    public List<DeviceUpdateDTO> getDevices() { return devices; } 
    public void setDevices(List<DeviceUpdateDTO> devices) { this.devices = devices; } 
}
