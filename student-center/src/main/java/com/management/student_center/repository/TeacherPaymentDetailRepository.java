package com.management.student_center.repository;

import com.management.student_center.entity.TeacherPaymentDetail;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherPaymentDetailRepository extends JpaRepository<TeacherPaymentDetail, Long> {

	List<TeacherPaymentDetail> findByPaymentInfoId(Integer paymentId);

	boolean existsBySessionTeacherInfoId(Long sessionTeacherId);

	@Modifying
	@Transactional
	long deleteBySessionTeacherInfoId(Long sessionTeacherId);


	@Modifying
	@Transactional
	@Query("DELETE FROM TeacherPaymentDetail d WHERE d.paymentInfo.id = :paymentId")
	void deleteByPaymentInfoId(@Param("paymentId") Integer paymentId);

}