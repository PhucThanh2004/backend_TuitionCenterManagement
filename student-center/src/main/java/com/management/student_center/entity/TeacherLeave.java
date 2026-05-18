package com.management.student_center.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "teacher_leaves")
@Getter
@Setter
@NoArgsConstructor
public class TeacherLeave {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Giáo viên xin nghỉ
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher_id", nullable = false)
	private Teacher teacher;

	/**
	 * Ngày bắt đầu nghỉ
	 */
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	/**
	 * Ngày kết thúc nghỉ
	 */
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate;

	/**
	 * Nghỉ theo giờ (optional) Ví dụ: 07:00 -> 11:00
	 */
	@Column(name = "start_time")
	private LocalTime startTime;

	@Column(name = "end_time")
	private LocalTime endTime;

	/**
	 * Lý do nghỉ
	 */
	@Column(columnDefinition = "TEXT")
	private String reason;

	/**
	 * SICK, ANNUAL,...
	 */
	@Column(name = "leave_type", nullable = false, length = 50)
	private String leaveType;

	/**
	 * PENDING, APPROVED, REJECTED, CANCELLED
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private LeaveStatus status = LeaveStatus.PENDING;

	/**
	 * Admin duyệt
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "approver_id")
	private User approver;

	/**
	 * Thời gian duyệt
	 */
	@Column(name = "approved_at")
	private LocalDateTime approvedAt;

	/**
	 * CANCEL hoặc REPLACE
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "affect_type")
	private AffectType affectType = AffectType.CANCEL;

	/**
	 * Đã xử lý toàn bộ session chưa
	 */
	@Column(name = "processed")
	private Boolean processed = false;

	/**
	 * Danh sách session bị ảnh hưởng
	 */
	@OneToMany(mappedBy = "leave", cascade = CascadeType.ALL)
	private List<LeaveAffectedSession> affectedSessions;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public LeaveStatus getStatus() {
		return status;
	}

	public void setStatus(LeaveStatus status) {
		this.status = status;
	}

	public User getApprover() {
		return approver;
	}

	public void setApprover(User approver) {
		this.approver = approver;
	}

	public LocalDateTime getApprovedAt() {
		return approvedAt;
	}

	public void setApprovedAt(LocalDateTime approvedAt) {
		this.approvedAt = approvedAt;
	}

	public AffectType getAffectType() {
		return affectType;
	}

	public void setAffectType(AffectType affectType) {
		this.affectType = affectType;
	}

	public Boolean getProcessed() {
		return processed;
	}

	public void setProcessed(Boolean processed) {
		this.processed = processed;
	}

	public List<LeaveAffectedSession> getAffectedSessions() {
		return affectedSessions;
	}

	public void setAffectedSessions(List<LeaveAffectedSession> affectedSessions) {
		this.affectedSessions = affectedSessions;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// ================= ENUM =================

	public enum AffectType {
		CANCEL, REPLACE
	}

	public enum LeaveStatus {
		PENDING, APPROVED, REJECTED, CANCELLED
	}
}