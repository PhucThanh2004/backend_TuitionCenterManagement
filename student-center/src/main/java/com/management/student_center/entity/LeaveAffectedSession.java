package com.management.student_center.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_affected_sessions")
@Getter
@Setter
@NoArgsConstructor
public class LeaveAffectedSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "leave_id", nullable = false)
	private TeacherLeave leave;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id", nullable = false)
	private Session session;

	@Column(name = "original_teacher_id")
	private Integer originalTeacherId;

	@Column(name = "replacement_teacher_id")
	private Integer replacementTeacherId;

	// ✅ SỬA: Thêm đầy đủ các status
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status = Status.PENDING;

	@Enumerated(EnumType.STRING)
	@Column(name = "replacement_response")
	private ReplacementResponse replacementResponse = ReplacementResponse.PENDING;

	@Column(name = "replacement_response_at")
	private LocalDateTime replacementResponseAt;

	@Column(name = "processed_at")
	private LocalDateTime processedAt;

	@Column(name = "admin_note", columnDefinition = "TEXT")
	private String adminNote;

	// ✅ THÊM: Thời gian admin phân công
	@Column(name = "assigned_at")
	private LocalDateTime assignedAt;

	// ✅ THÊM: Lý do từ chối (nếu có)
	@Column(name = "decline_reason", columnDefinition = "TEXT")
	private String declineReason;
	
	@Column(name = "replacement_salary")
	private BigDecimal replacementSalary;

	// ================= ENUM =================

	public enum Status {
		PENDING, // Chưa có giáo viên thay thế
		ASSIGNED, // ✅ Đã chỉ định, chờ giáo viên phản hồi
		DECLINED, // ✅ Giáo viên từ chối
		RESOLVED, // Đã chấp nhận
		SKIPPED // Hủy buổi học
	}

	public enum ReplacementResponse {
		PENDING, ACCEPTED, REJECTED
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TeacherLeave getLeave() {
		return leave;
	}

	public void setLeave(TeacherLeave leave) {
		this.leave = leave;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Integer getOriginalTeacherId() {
		return originalTeacherId;
	}

	public void setOriginalTeacherId(Integer originalTeacherId) {
		this.originalTeacherId = originalTeacherId;
	}

	public Integer getReplacementTeacherId() {
		return replacementTeacherId;
	}

	public void setReplacementTeacherId(Integer replacementTeacherId) {
		this.replacementTeacherId = replacementTeacherId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public ReplacementResponse getReplacementResponse() {
		return replacementResponse;
	}

	public void setReplacementResponse(ReplacementResponse replacementResponse) {
		this.replacementResponse = replacementResponse;
	}

	public LocalDateTime getReplacementResponseAt() {
		return replacementResponseAt;
	}

	public void setReplacementResponseAt(LocalDateTime replacementResponseAt) {
		this.replacementResponseAt = replacementResponseAt;
	}

	public LocalDateTime getProcessedAt() {
		return processedAt;
	}

	public void setProcessedAt(LocalDateTime processedAt) {
		this.processedAt = processedAt;
	}

	public String getAdminNote() {
		return adminNote;
	}

	public void setAdminNote(String adminNote) {
		this.adminNote = adminNote;
	}

	public LocalDateTime getAssignedAt() {
		return assignedAt;
	}

	public void setAssignedAt(LocalDateTime assignedAt) {
		this.assignedAt = assignedAt;
	}

	public String getDeclineReason() {
		return declineReason;
	}

	public void setDeclineReason(String declineReason) {
		this.declineReason = declineReason;
	}
	
	public BigDecimal getReplacementSalary() {
	    return replacementSalary;
	}

	public void setReplacementSalary(BigDecimal replacementSalary) {
	    this.replacementSalary = replacementSalary;
	}
}