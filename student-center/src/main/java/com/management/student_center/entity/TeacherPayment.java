package com.management.student_center.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.management.student_center.enums.TeacherPaymentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "teacherpayments", uniqueConstraints = {
		@UniqueConstraint(name = "uk_teacher_month_year", columnNames = { "teacherId", "month", "year" }) })
public class TeacherPayment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teacherId", nullable = false)
	private Teacher teacherInfo;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount = BigDecimal.ZERO;

	@Column(precision = 19, scale = 2)
	private BigDecimal paidAmount = BigDecimal.ZERO;

	private Integer totalSessions = 0;

	private LocalDate paymentDate;

	private Integer month;

	private Integer year;

	@Enumerated(EnumType.STRING)
	private TeacherPaymentStatus status = TeacherPaymentStatus.DRAFT;

	@Column(columnDefinition = "TEXT")
	private String notes;

	@Column(columnDefinition = "TEXT")
	private String payrollNote;

	@Column(columnDefinition = "TEXT")
	private String teacherFeedback;

	private LocalDateTime teacherConfirmedAt;

	private Integer teacherConfirmedBy;

	private LocalDateTime finalizedAt;

	private Integer finalizedBy;

	@OneToMany(mappedBy = "paymentInfo", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<TeacherPaymentDetail> paymentDetails;

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public TeacherPayment() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Teacher getTeacherInfo() {
		return teacherInfo;
	}

	public void setTeacherInfo(Teacher teacherInfo) {
		this.teacherInfo = teacherInfo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public Integer getTotalSessions() {
		return totalSessions;
	}

	public void setTotalSessions(Integer totalSessions) {
		this.totalSessions = totalSessions;
	}

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public TeacherPaymentStatus getStatus() {
		return status;
	}

	public void setStatus(TeacherPaymentStatus status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getPayrollNote() {
		return payrollNote;
	}

	public void setPayrollNote(String payrollNote) {
		this.payrollNote = payrollNote;
	}

	public String getTeacherFeedback() {
		return teacherFeedback;
	}

	public void setTeacherFeedback(String teacherFeedback) {
		this.teacherFeedback = teacherFeedback;
	}

	public LocalDateTime getTeacherConfirmedAt() {
		return teacherConfirmedAt;
	}

	public void setTeacherConfirmedAt(LocalDateTime teacherConfirmedAt) {
		this.teacherConfirmedAt = teacherConfirmedAt;
	}

	public Integer getTeacherConfirmedBy() {
		return teacherConfirmedBy;
	}

	public void setTeacherConfirmedBy(Integer teacherConfirmedBy) {
		this.teacherConfirmedBy = teacherConfirmedBy;
	}

	public LocalDateTime getFinalizedAt() {
		return finalizedAt;
	}

	public void setFinalizedAt(LocalDateTime finalizedAt) {
		this.finalizedAt = finalizedAt;
	}

	public Integer getFinalizedBy() {
		return finalizedBy;
	}

	public void setFinalizedBy(Integer finalizedBy) {
		this.finalizedBy = finalizedBy;
	}

	public List<TeacherPaymentDetail> getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(List<TeacherPaymentDetail> paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
