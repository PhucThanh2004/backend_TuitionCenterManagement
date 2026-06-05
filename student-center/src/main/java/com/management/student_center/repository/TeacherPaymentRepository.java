package com.management.student_center.repository;

import com.management.student_center.entity.TeacherPayment;
import com.management.student_center.enums.TeacherPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeacherPaymentRepository extends JpaRepository<TeacherPayment, Integer> {

	Optional<TeacherPayment> findByTeacherInfoIdAndMonthAndYear(Long teacherId, Integer month, Integer year);

	List<TeacherPayment> findByMonthAndYear(Integer month, Integer year);

	List<TeacherPayment> findByStatus(TeacherPaymentStatus status);

	List<TeacherPayment> findAllByOrderByCreatedAtDesc();

	List<TeacherPayment> findByTeacherInfoIdOrderByYearDescMonthDesc(Integer teacherId);

	@Query("SELECT p FROM TeacherPayment p " + "JOIN FETCH p.teacherInfo t " + // p.teacherInfo (tên field trong Entity)
			"JOIN FETCH t.userInfo " + // t.userInfo (field trong Teacher entity)
			"WHERE p.id = :paymentId")
	Optional<TeacherPayment> findByIdWithTeacher(@Param("paymentId") Integer paymentId);
}
