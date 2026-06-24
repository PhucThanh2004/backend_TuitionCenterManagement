package com.management.student_center.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SessionDetailDTO {
    
    private Long id;
    private String status;
    private String className;
    private String subjectName;
    private String subjectSlug;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long totalMinutes;
    
    private TeacherInfo teacher;
    private RoomInfo room;
    private List<StudentAttendanceInfo> studentAttendances;
    private TeacherAttendanceInfo teacherAttendance;
    
    // ========== THÊM CÁC FIELD CHO NỘI DUNG BUỔI HỌC ==========
    private Boolean isFollowingPlan;           // true: dạy đúng kế hoạch, false: dạy lệch
    private String displayTopic;               // Chủ đề (kế hoạch hoặc thực tế)
    private String displayContent;             // Nội dung (kế hoạch hoặc thực tế)
    private String displayHomework;            // Bài tập (kế hoạch hoặc thực tế)
    private String plannedTopic;               // Chủ đề theo kế hoạch (để so sánh)
    private Long plannedSessionDetailId;       // ID của kế hoạch (nếu có)
    private String deviationReason;            // Lý do thay đổi (nếu dạy lệch)
    private String noteForNextSession;         // Ghi chú cho buổi sau
    
    // Constructors
    public SessionDetailDTO() {}
    
    public SessionDetailDTO(Long id, String status, String className, String subjectName, 
                           String subjectSlug, LocalDate sessionDate, LocalTime startTime, 
                           LocalTime endTime, Long totalMinutes, TeacherInfo teacher, 
                           RoomInfo room, List<StudentAttendanceInfo> studentAttendances, 
                           TeacherAttendanceInfo teacherAttendance) {
        this.id = id;
        this.status = status;
        this.className = className;
        this.subjectName = subjectName;
        this.subjectSlug = subjectSlug;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalMinutes = totalMinutes;
        this.teacher = teacher;
        this.room = room;
        this.studentAttendances = studentAttendances;
        this.teacherAttendance = teacherAttendance;
    }
    
    // ========== GETTERS & SETTERS CHO CÁC FIELD MỚI ==========
    public Boolean getIsFollowingPlan() {
        return isFollowingPlan;
    }
    
    public void setIsFollowingPlan(Boolean isFollowingPlan) {
        this.isFollowingPlan = isFollowingPlan;
    }
    
    public String getDisplayTopic() {
        return displayTopic;
    }
    
    public void setDisplayTopic(String displayTopic) {
        this.displayTopic = displayTopic;
    }
    
    public String getDisplayContent() {
        return displayContent;
    }
    
    public void setDisplayContent(String displayContent) {
        this.displayContent = displayContent;
    }
    
    public String getDisplayHomework() {
        return displayHomework;
    }
    
    public void setDisplayHomework(String displayHomework) {
        this.displayHomework = displayHomework;
    }
    
    public String getPlannedTopic() {
        return plannedTopic;
    }
    
    public void setPlannedTopic(String plannedTopic) {
        this.plannedTopic = plannedTopic;
    }
    
    public Long getPlannedSessionDetailId() {
        return plannedSessionDetailId;
    }
    
    public void setPlannedSessionDetailId(Long plannedSessionDetailId) {
        this.plannedSessionDetailId = plannedSessionDetailId;
    }
    
    public String getDeviationReason() {
        return deviationReason;
    }
    
    public void setDeviationReason(String deviationReason) {
        this.deviationReason = deviationReason;
    }
    
    public String getNoteForNextSession() {
        return noteForNextSession;
    }
    
    public void setNoteForNextSession(String noteForNextSession) {
        this.noteForNextSession = noteForNextSession;
    }
    
    // ========== GETTERS & SETTERS CHO CÁC FIELD CŨ ==========
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getSubjectSlug() { return subjectSlug; }
    public void setSubjectSlug(String subjectSlug) { this.subjectSlug = subjectSlug; }
    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public Long getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(Long totalMinutes) { this.totalMinutes = totalMinutes; }
    public TeacherInfo getTeacher() { return teacher; }
    public void setTeacher(TeacherInfo teacher) { this.teacher = teacher; }
    public RoomInfo getRoom() { return room; }
    public void setRoom(RoomInfo room) { this.room = room; }
    public List<StudentAttendanceInfo> getStudentAttendances() { return studentAttendances; }
    public void setStudentAttendances(List<StudentAttendanceInfo> studentAttendances) { 
        this.studentAttendances = studentAttendances; 
    }
    public TeacherAttendanceInfo getTeacherAttendance() { return teacherAttendance; }
    public void setTeacherAttendance(TeacherAttendanceInfo teacherAttendance) { 
        this.teacherAttendance = teacherAttendance; 
    }
    
    // ========== INNER CLASSES (giữ nguyên) ==========
    public static class TeacherInfo {
        // ... giữ nguyên code cũ
        private Long id;
        private String fullName;
        private String specialty;
        private String email;
        private String phoneNumber;
        private String image;
        
        public TeacherInfo() {}
        
        public TeacherInfo(Long id, String fullName, String specialty, String email, String phoneNumber, String image) {
            this.id = id;
            this.fullName = fullName;
            this.specialty = specialty;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.image = image;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getSpecialty() { return specialty; }
        public void setSpecialty(String specialty) { this.specialty = specialty; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
    }
    
    public static class RoomInfo {
        // ... giữ nguyên code cũ
        private Long id;
        private String name;
        private Integer seatCapacity;
        
        public RoomInfo() {}
        
        public RoomInfo(Long id, String name, Integer seatCapacity) {
            this.id = id;
            this.name = name;
            this.seatCapacity = seatCapacity;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getSeatCapacity() { return seatCapacity; }
        public void setSeatCapacity(Integer seatCapacity) { this.seatCapacity = seatCapacity; }
    }
    
    public static class StudentAttendanceInfo {
        // ... giữ nguyên code cũ
        private Long studentId;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String attendanceStatus;
        private String attendanceNote;
        
        public StudentAttendanceInfo() {}
        
        public StudentAttendanceInfo(Long studentId, String fullName, String email, 
                                    String phoneNumber, String attendanceStatus, 
                                    String attendanceNote) {
            this.studentId = studentId;
            this.fullName = fullName;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.attendanceStatus = attendanceStatus;
            this.attendanceNote = attendanceNote;
        }
        
        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getAttendanceStatus() { return attendanceStatus; }
        public void setAttendanceStatus(String attendanceStatus) { this.attendanceStatus = attendanceStatus; }
        public String getAttendanceNote() { return attendanceNote; }
        public void setAttendanceNote(String attendanceNote) { this.attendanceNote = attendanceNote; }
    }
    
    public static class TeacherAttendanceInfo {
        // ... giữ nguyên code cũ
        private Long teacherId;
        private String teacherName;
        private String status;
        private String note;
        
        public TeacherAttendanceInfo() {}
        
        public TeacherAttendanceInfo(Long teacherId, String teacherName, String status, String note) {
            this.teacherId = teacherId;
            this.teacherName = teacherName;
            this.status = status;
            this.note = note;
        }
        
        public Long getTeacherId() { return teacherId; }
        public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
        public String getTeacherName() { return teacherName; }
        public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }
}