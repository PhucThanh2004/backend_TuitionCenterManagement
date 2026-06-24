package com.management.student_center.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.management.student_center.enums.StudentTuitionStatus;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "student_tuitions")
public class StudentTuition {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "student_id")
	private Student student;

	private int month;
	private int year;

	private BigDecimal totalAmount; // Tổng tiền phải đóng

	private BigDecimal paidAmount = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	private StudentTuitionStatus status;

	@Column(columnDefinition = "TEXT")
	private String notes;

	private LocalDateTime createdAt;

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

	@OneToMany(mappedBy = "studentTuition", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<StudentTuitionDetail> details;

	private String invoiceCode;

	private LocalDate dueDate;

	private Boolean tuitionLocked = false;

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public Boolean getTuitionLocked() {
		return tuitionLocked;
	}

	public void setTuitionLocked(Boolean tuitionLocked) {
		this.tuitionLocked = tuitionLocked;
	}

	// --- Getters & Setters ---
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public StudentTuitionStatus getStatus() {
		return status;
	}

	public void setStatus(StudentTuitionStatus status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<StudentTuitionDetail> getDetails() {
		return details;
	}

	public void setDetails(List<StudentTuitionDetail> details) {
		this.details = details;
	}

	public BigDecimal getRemainingAmount() {
		BigDecimal total = this.totalAmount != null ? this.totalAmount : BigDecimal.ZERO;
		BigDecimal paid = this.paidAmount != null ? this.paidAmount : BigDecimal.ZERO;
		BigDecimal remaining = total.subtract(paid);
		// Đảm bảo không trả về số âm
		return remaining.compareTo(BigDecimal.ZERO) > 0 ? remaining : BigDecimal.ZERO;
	}

}