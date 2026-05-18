package com.management.student_center.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    /**
     * Đơn nghỉ
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_id", nullable = false)
    private TeacherLeave leave;

    /**
     * Session bị ảnh hưởng
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    /**
     * Giáo viên gốc
     */
    @Column(name = "original_teacher_id")
    private Integer originalTeacherId;

    /**
     * Giáo viên thay thế
     */
    @Column(name = "replacement_teacher_id")
    private Integer replacementTeacherId;

    /**
     * Trạng thái xử lý session
     *
     * PENDING  = chưa xử lý xong
     * RESOLVED = đã có giáo viên thay
     * SKIPPED  = hủy lớp
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.PENDING;

    /**
     * Phản hồi của giáo viên thay thế
     *
     * PENDING
     * ACCEPTED
     * REJECTED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "replacement_response")
    private ReplacementResponse replacementResponse =
            ReplacementResponse.PENDING;

    /**
     * Thời gian phản hồi
     */
    @Column(name = "replacement_response_at")
    private LocalDateTime replacementResponseAt;

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

	/**
     * Thời gian xử lý hoàn tất
     */
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    /**
     * Ghi chú admin
     */
    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    // ================= ENUM =================

    public enum Status {
        PENDING,
        RESOLVED,
        SKIPPED
    }

    public enum ReplacementResponse {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}