package com.management.student_center.dto;

import com.management.student_center.enums.RoomManualStatus;
import java.util.List;

public class RoomUpdateDTO {
    private String name;
    private Integer seatCapacity;
    private RoomManualStatus manualStatus;
    private List<DeviceUpdateDTO> devices;  // Danh sách thiết bị cần cập nhật
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getSeatCapacity() { return seatCapacity; }
    public void setSeatCapacity(Integer seatCapacity) { this.seatCapacity = seatCapacity; }
    
    public RoomManualStatus getManualStatus() { return manualStatus; }
    public void setManualStatus(RoomManualStatus manualStatus) { this.manualStatus = manualStatus; }
    
    public List<DeviceUpdateDTO> getDevices() { return devices; }
    public void setDevices(List<DeviceUpdateDTO> devices) { this.devices = devices; }
}