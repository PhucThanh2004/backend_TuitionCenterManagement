package com.management.student_center.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "teacherpayments")
public class TeacherPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer month; // Thêm cột này để lọc
    private Integer year;  // Thêm cột này để lọc

    private BigDecimal amount;      // Tổng lương phải trả
    private BigDecimal paidAmount;  // Tổng tiền đã tạm ứng/trả

    private LocalDate paymentDate;
    private String status; // "unpaid", "partial", "paid"

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "teacherId")
    private Teacher teacher;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference 
    private List<TeacherPaymentDetail> paymentDetails;

    // --- Getter ảo để Frontend tiện hiển thị ---
    public BigDecimal getRemainingAmount() {
        BigDecimal total = amount != null ? amount : BigDecimal.ZERO;
        BigDecimal paid = paidAmount != null ? paidAmount : BigDecimal.ZERO;
        return total.subtract(paid);
    }

    public TeacherPayment() {}

    // --- Getter & Setter (Bổ sung các trường mới) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
    public List<TeacherPaymentDetail> getPaymentDetails() { return paymentDetails; }
    public void setPaymentDetails(List<TeacherPaymentDetail> paymentDetails) { this.paymentDetails = paymentDetails; }
}