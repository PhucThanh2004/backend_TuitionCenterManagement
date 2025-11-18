package com.management.student_center.entity;

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

    private BigDecimal amount;

    private LocalDate paymentDate;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Quan hệ với Teacher
    @ManyToOne
    @JoinColumn(name = "teacherId")
    private Teacher teacher;

    // Quan hệ với TeacherPaymentDetail
    @OneToMany(mappedBy = "payment")
    private List<TeacherPaymentDetail> paymentDetails;

    public TeacherPayment() {}

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

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
