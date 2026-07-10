package com.management.student_center.repository;

import com.management.student_center.entity.Subject;
import com.management.student_center.enums.BillingType;
import com.management.student_center.enums.PaymentPlanType;

import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
	Optional<Subject> findById(Long id);

	Page<Subject> findByStatus(String status, Pageable pageable);

	List<Subject> findByStatus(String status);

	long countByStatus(String status);

	@Query("SELECT COUNT(ss) FROM StudentSubject ss WHERE ss.subject.id = :subjectId AND ss.deletedAt IS NULL")
	long countCurrentStudents(@Param("subjectId") Long subjectId);

	// Lấy môn học theo teacher
	@Query("SELECT s FROM Subject s JOIN s.teacherSubjects ts WHERE ts.teacher.userInfo.id = :userId")
	Page<Subject> findByUserId(@Param("userId") Long userId, Pageable pageable);

	@Query("SELECT s FROM Subject s JOIN s.teacherSubjects ts WHERE ts.teacher.userInfo.id = :userId AND s.status = :status")
	Page<Subject> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status,
			Pageable pageable);

	long count();

	long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	// Đếm số lớp học theo cấp học
	@Query("""
			    SELECT
			        CASE
			            WHEN CAST(s.grade AS int) BETWEEN 1 AND 5 THEN 'Cấp 1 (Tiểu học)'
			            WHEN CAST(s.grade AS int) BETWEEN 6 AND 9 THEN 'Cấp 2 (THCS)'
			            WHEN CAST(s.grade AS int) BETWEEN 10 AND 12 THEN 'Cấp 3 (THPT)'
			        END,
			        COUNT(s)
			    FROM Subject s
			    GROUP BY
			        CASE
			            WHEN CAST(s.grade AS int) BETWEEN 1 AND 5 THEN 'Cấp 1 (Tiểu học)'
			            WHEN CAST(s.grade AS int) BETWEEN 6 AND 9 THEN 'Cấp 2 (THCS)'
			            WHEN CAST(s.grade AS int) BETWEEN 10 AND 12 THEN 'Cấp 3 (THPT)'
			        END
			""")
	List<Object[]> countSubjectsByLevel();

	List<Subject> findByBillingType(BillingType billingType);

	List<Subject> findByPaymentPlanType(PaymentPlanType paymentPlanType);

	
	//Mới thêm
	@Query("""
			    SELECT DISTINCT s
			    FROM Subject s
			    LEFT JOIN FETCH s.subjectType
			    LEFT JOIN FETCH s.curriculums
			    WHERE s.status = 'active'
			""")
	List<Subject> findAllPublicSubjects();
}
