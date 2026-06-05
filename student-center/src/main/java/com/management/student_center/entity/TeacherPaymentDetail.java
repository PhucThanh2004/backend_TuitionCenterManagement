package com.management.student_center.entity;

import com.management.student_center.enums.SalaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "teacherpaymentdetails", uniqueConstraints = {
		@UniqueConstraint(name = "uk_payment_session_teacher", columnNames = { "paymentId", "sessionTeacherId" }) })
public class TeacherPaymentDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "paymentId", nullable = false)
	private TeacherPayment paymentInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sessionTeacherId", nullable = false)
	private SessionTeacher sessionTeacherInfo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SalaryType salaryType = SalaryType.PER_SESSION;

	@Column(nullable = false,precision = 19, scale = 2)
	private BigDecimal salaryRate = BigDecimal.ZERO;

	@Column(nullable = false,precision = 5, scale = 2)
	private BigDecimal workedHours = BigDecimal.ZERO;

	@Column(nullable = false,precision = 19, scale = 2)
	private BigDecimal baseAmount = BigDecimal.ZERO;

	@Column(nullable = false,precision = 19, scale = 2)
	private BigDecimal bonusAmount = BigDecimal.ZERO;

	@Column(nullable = false,precision = 19, scale = 2)
	private BigDecimal penaltyAmount = BigDecimal.ZERO;

	@Column(nullable = false,precision = 19, scale = 2)
	private BigDecimal finalAmount = BigDecimal.ZERO;

	private LocalDate payrollDate;

	@Column(columnDefinition = "TEXT")
	private String note;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public TeacherPaymentDetail() {
	}

	public Long getId() {
		return id;
	}

	public TeacherPayment getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(TeacherPayment paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	public SessionTeacher getSessionTeacherInfo() {
		return sessionTeacherInfo;
	}

	public void setSessionTeacherInfo(SessionTeacher sessionTeacherInfo) {
		this.sessionTeacherInfo = sessionTeacherInfo;
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

	public BigDecimal getWorkedHours() {
		return workedHours;
	}

	public void setWorkedHours(BigDecimal workedHours) {
		this.workedHours = workedHours;
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

	public LocalDate getPayrollDate() {
		return payrollDate;
	}

	public void setPayrollDate(LocalDate payrollDate) {
		this.payrollDate = payrollDate;
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
