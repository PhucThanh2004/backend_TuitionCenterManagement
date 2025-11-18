package com.management.student_center.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "teacherpaymentdetails")
public class TeacherPaymentDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float totalHours;

    private Integer totalSessions;

    private Float salaryRate;

    private Float totalMoney;

    // Quan hệ với TeacherPayment
    @ManyToOne
    @JoinColumn(name = "paymentId")
    private TeacherPayment payment;

    // Quan hệ với Subject
    @ManyToOne
    @JoinColumn(name = "subjectId")
    private Subject subject;

    public TeacherPaymentDetail() {}

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Float getTotalHours() { return totalHours; }
    public void setTotalHours(Float totalHours) { this.totalHours = totalHours; }

    public Integer getTotalSessions() { return totalSessions; }
    public void setTotalSessions(Integer totalSessions) { this.totalSessions = totalSessions; }

    public Float getSalaryRate() { return salaryRate; }
    public void setSalaryRate(Float salaryRate) { this.salaryRate = salaryRate; }

    public Float getTotalMoney() { return totalMoney; }
    public void setTotalMoney(Float totalMoney) { this.totalMoney = totalMoney; }

    public TeacherPayment getPayment() { return payment; }
    public void setPayment(TeacherPayment payment) { this.payment = payment; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
}
