package com.management.student_center.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.management.student_center.enums.BillingType;
import com.management.student_center.enums.PaymentPlanType;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "student_tuition_details")
public class StudentTuitionDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "student_tuition_id")
	@JsonBackReference
	private StudentTuition studentTuition;

	@ManyToOne
	@JoinColumn(name = "subject_id")
	private Subject subject;

	private int attendedSessions; // Số buổi đi học
	private float totalHours; // Tổng số giờ học
	private BigDecimal totalMoney; // totalHours * hourlyRate

	@Column(name = "note")
	private String note;

	@Enumerated(EnumType.STRING)
	private BillingType billingType;

	@Enumerated(EnumType.STRING)
	private PaymentPlanType paymentPlanType;

	private Integer installmentNo;

	private Integer totalInstallments;

	private BigDecimal unitPrice;

	// --- Getters & Setters ---
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StudentTuition getStudentTuition() {
		return studentTuition;
	}

	public void setStudentTuition(StudentTuition studentTuition) {
		this.studentTuition = studentTuition;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public int getAttendedSessions() {
		return attendedSessions;
	}

	public void setAttendedSessions(int attendedSessions) {
		this.attendedSessions = attendedSessions;
	}

	public float getTotalHours() {
		return totalHours;
	}

	public void setTotalHours(float totalHours) {
		this.totalHours = totalHours;
	}

	public BigDecimal getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public BillingType getBillingType() {
		return billingType;
	}

	public void setBillingType(BillingType billingType) {
		this.billingType = billingType;
	}

	public PaymentPlanType getPaymentPlanType() {
		return paymentPlanType;
	}

	public void setPaymentPlanType(PaymentPlanType paymentPlanType) {
		this.paymentPlanType = paymentPlanType;
	}

	public Integer getInstallmentNo() {
		return installmentNo;
	}

	public void setInstallmentNo(Integer installmentNo) {
		this.installmentNo = installmentNo;
	}

	public Integer getTotalInstallments() {
		return totalInstallments;
	}

	public void setTotalInstallments(Integer totalInstallments) {
		this.totalInstallments = totalInstallments;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
}