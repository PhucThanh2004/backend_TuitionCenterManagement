package com.management.student_center.dto.studentSubjectDto;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AttendanceStatisticsDTO {
    private Long studentId;
    private String studentName;
    private Long subjectId;
    private String subjectName;
    
    // Thống kê tổng hợp
    private Integer totalSessions;      // Tổng số buổi tham gia môn học
    private Integer presentCount;       // Số buổi có mặt
    private Integer absentCount;        // Số buổi vắng
    private Integer lateCount;          // Số buổi trễ
    private Double attendanceRate;      // Tỉ lệ chuyên cần (có mặt + trễ)/tổng số buổi
    
    // Lịch sử điểm danh chi tiết
    private List<AttendanceHistoryDTO> history;
    
    // Getters and Setters
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    
    public Integer getTotalSessions() { return totalSessions; }
    public void setTotalSessions(Integer totalSessions) { this.totalSessions = totalSessions; }
    
    public Integer getPresentCount() { return presentCount; }
    public void setPresentCount(Integer presentCount) { this.presentCount = presentCount; }
    
    public Integer getAbsentCount() { return absentCount; }
    public void setAbsentCount(Integer absentCount) { this.absentCount = absentCount; }
    
    public Integer getLateCount() { return lateCount; }
    public void setLateCount(Integer lateCount) { this.lateCount = lateCount; }
    
    public Double getAttendanceRate() { return attendanceRate; }
    public void setAttendanceRate(Double attendanceRate) { this.attendanceRate = attendanceRate; }
    
    public List<AttendanceHistoryDTO> getHistory() { return history; }
    public void setHistory(List<AttendanceHistoryDTO> history) { this.history = history; }
}