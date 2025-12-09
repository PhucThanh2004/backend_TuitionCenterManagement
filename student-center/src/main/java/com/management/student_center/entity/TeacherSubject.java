package com.management.student_center.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate; // Import này
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "teachersubjects")
@EntityListeners(AuditingEntityListener.class)
public class TeacherSubject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Quan hệ với Teacher
	@ManyToOne
	@JoinColumn(name = "teacherId")
	private Teacher teacher;

	// Quan hệ với Subject
	@ManyToOne
	@JoinColumn(name = "subjectId")
	private Subject subject;

	// Lương theo giờ
	private BigDecimal salaryRate;

	@CreatedDate
	@Column(name = "createdAt", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// Constructor mặc định
	public TeacherSubject() {
	}

	// Constructor đầy đủ
	public TeacherSubject(Long id, Teacher teacher, Subject subject, BigDecimal salaryRate) {
		this.id = id;
		this.teacher = teacher;
		this.subject = subject;
		this.salaryRate = salaryRate;
	}

	// Getter & Setter
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public BigDecimal getSalaryRate() {
		return salaryRate;
	}

	public void setSalaryRate(BigDecimal salaryRate) {
		this.salaryRate = salaryRate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
