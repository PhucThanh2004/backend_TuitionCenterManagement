package com.management.student_center.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.management.student_center.enums.AssignmentStatus;
import com.management.student_center.enums.AssignmentType;
import com.management.student_center.enums.SalaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "session_teachers", uniqueConstraints = {
		@UniqueConstraint(name = "uk_session_teacher_unique", columnNames = { "sessionId", "teacherId",
				"assignmentType" }) })
public class SessionTeacher {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sessionId", nullable = false)
	private Session sessionInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teacherId", nullable = false)
	private Teacher teacherInfo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AssignmentType assignmentType = AssignmentType.MAIN;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AssignmentStatus assignmentStatus = AssignmentStatus.ASSIGNED;

	private LocalTime actualStartTime;

	private LocalTime actualEndTime;

	@Column(precision = 5, scale = 2)
	private BigDecimal workedHours = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SalaryType salaryType = SalaryType.PER_SESSION;

	@Column(precision = 19, scale = 2)
	private BigDecimal salaryRate = BigDecimal.ZERO;

	@Column(precision = 19, scale = 2)
	private BigDecimal baseAmount = BigDecimal.ZERO;

	@Column(precision = 19, scale = 2)
	private BigDecimal bonusAmount = BigDecimal.ZERO;

	@Column(precision = 19, scale = 2)
	private BigDecimal penaltyAmount = BigDecimal.ZERO;

	@Column(precision = 19, scale = 2)
	private BigDecimal finalAmount = BigDecimal.ZERO;

	private Boolean payrollLocked = false;

	@Column(columnDefinition = "TEXT")
	private String note;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "sessionTeacherInfo", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnore
	private List<TeacherPaymentDetail> paymentDetails;

	public SessionTeacher() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Session getSessionInfo() {
		return sessionInfo;
	}

	public void setSessionInfo(Session sessionInfo) {
		this.sessionInfo = sessionInfo;
	}

	public Teacher getTeacherInfo() {
		return teacherInfo;
	}

	public void setTeacherInfo(Teacher teacherInfo) {
		this.teacherInfo = teacherInfo;
	}

	public AssignmentType getAssignmentType() {
		return assignmentType;
	}

	public void setAssignmentType(AssignmentType assignmentType) {
		this.assignmentType = assignmentType;
	}

	public AssignmentStatus getAssignmentStatus() {
		return assignmentStatus;
	}

	public void setAssignmentStatus(AssignmentStatus assignmentStatus) {
		this.assignmentStatus = assignmentStatus;
	}

	public LocalTime getActualStartTime() {
		return actualStartTime;
	}

	public void setActualStartTime(LocalTime actualStartTime) {
		this.actualStartTime = actualStartTime;
	}

	public LocalTime getActualEndTime() {
		return actualEndTime;
	}

	public void setActualEndTime(LocalTime actualEndTime) {
		this.actualEndTime = actualEndTime;
	}

	public BigDecimal getWorkedHours() {
		return workedHours;
	}

	public void setWorkedHours(BigDecimal workedHours) {
		this.workedHours = workedHours;
	}

	public SalaryType getSalaryType() {
		return salaryType;
	}

	public void setSalaryType(SalaryType salaryType) {
		this.salaryType = salaryType;
	}

	public BigDecimal getSalaryRate() {
		return salaryRate;
	}

	public void setSalaryRate(BigDecimal salaryRate) {
		this.salaryRate = salaryRate;
	}

	public BigDecimal getBaseAmount() {
		return baseAmount;
	}

	public void setBaseAmount(BigDecimal baseAmount) {
		this.baseAmount = baseAmount;
	}

	public BigDecimal getBonusAmount() {
		return bonusAmount;
	}

	public void setBonusAmount(BigDecimal bonusAmount) {
		this.bonusAmount = bonusAmount;
	}

	public BigDecimal getPenaltyAmount() {
		return penaltyAmount;
	}

	public void setPenaltyAmount(BigDecimal penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}

	public BigDecimal getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(BigDecimal finalAmount) {
		this.finalAmount = finalAmount;
	}

	public Boolean getPayrollLocked() {
		return payrollLocked;
	}

	public void setPayrollLocked(Boolean payrollLocked) {
		this.payrollLocked = payrollLocked;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}
