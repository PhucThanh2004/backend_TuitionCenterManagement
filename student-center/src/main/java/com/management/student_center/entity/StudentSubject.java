package com.management.student_center.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.management.student_center.enums.BillingType;
import com.management.student_center.enums.PaymentPlanType;

@Entity
@Table(name = "studentsubjects")
public class StudentSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ với Student
    @ManyToOne
    @JoinColumn(name = "studentId")
    @JsonIgnoreProperties({"studentSubjects"})
    private Student student;

    // Quan hệ với Subject
    @ManyToOne
    @JoinColumn(name = "subjectId")
    @JsonIgnoreProperties({"studentSubjects"})
    private Subject subject;

    // Ngày đăng ký
    private LocalDate enrollmentDate;
    
    // Thêm cột deleted_at: nếu có giá trị là đã xóa, null là chưa xóa
    private LocalDate deletedAt;

    // Constructor mặc định
    public StudentSubject() {}

    // Constructor đầy đủ (không bao gồm deletedAt)
    public StudentSubject(Long id, Student student, Subject subject, LocalDate enrollmentDate) {
        this.id = id;
        this.student = student;
        this.subject = subject;
        this.enrollmentDate = enrollmentDate;
        this.deletedAt = null; // Mặc định chưa xóa
    }
    @Column(precision = 19, scale = 2)
    private BigDecimal feeAmountSnapshot;

    @Enumerated(EnumType.STRING)
    private BillingType billingTypeSnapshot;


	@Enumerated(EnumType.STRING)
    private PaymentPlanType paymentPlanTypeSnapshot;

    private Integer installmentCountSnapshot;
    // Constructor đầy đủ (có deletedAt)
    public StudentSubject(Long id, Student student, Subject subject, LocalDate enrollmentDate, LocalDate deletedAt) {
        this.id = id;
        this.student = student;
        this.subject = subject;
        this.enrollmentDate = enrollmentDate;
        this.deletedAt = deletedAt;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    
    public LocalDate getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDate deletedAt) { this.deletedAt = deletedAt; }
    
    // Helper method: Kiểm tra bản ghi đã bị xóa chưa
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
    
    public BigDecimal getFeeAmountSnapshot() {
		return feeAmountSnapshot;
	}

	public void setFeeAmountSnapshot(BigDecimal feeAmountSnapshot) {
		this.feeAmountSnapshot = feeAmountSnapshot;
	}

	public BillingType getBillingTypeSnapshot() {
		return billingTypeSnapshot;
	}

	public void setBillingTypeSnapshot(BillingType billingTypeSnapshot) {
		this.billingTypeSnapshot = billingTypeSnapshot;
	}

	public PaymentPlanType getPaymentPlanTypeSnapshot() {
		return paymentPlanTypeSnapshot;
	}

	public void setPaymentPlanTypeSnapshot(PaymentPlanType paymentPlanTypeSnapshot) {
		this.paymentPlanTypeSnapshot = paymentPlanTypeSnapshot;
	}

	public Integer getInstallmentCountSnapshot() {
		return installmentCountSnapshot;
	}

	public void setInstallmentCountSnapshot(Integer installmentCountSnapshot) {
		this.installmentCountSnapshot = installmentCountSnapshot;
	}
}