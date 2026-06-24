package com.management.student_center.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.management.student_center.entity.StudentPaymentTransaction;

@Repository
public interface StudentPaymentTransactionRepository extends JpaRepository<StudentPaymentTransaction, Long> {

	List<StudentPaymentTransaction> findByStudentTuitionId(Long tuitionId);

	@Query("""
			SELECT COALESCE(SUM(t.amount),0)
			FROM StudentPaymentTransaction t
			WHERE t.studentTuition.id = :tuitionId
			""")
	BigDecimal sumPaidAmount(@Param("tuitionId") Long tuitionId);

	List<StudentPaymentTransaction> findByPaymentDateBetween(LocalDateTime from, LocalDateTime to);
}