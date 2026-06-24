package com.management.student_center.repository;

import com.management.student_center.entity.StudentTuitionDetail;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentTuitionDetailRepository extends JpaRepository<StudentTuitionDetail, Long> {

	@Query("""
			SELECT COUNT(std)
			FROM StudentTuitionDetail std
			JOIN std.studentTuition st
			WHERE std.subject.id = :subjectId
			AND st.status IN (
			    com.management.student_center.enums.StudentTuitionStatus.WAITING_PAYMENT,
			    com.management.student_center.enums.StudentTuitionStatus.PARTIAL_PAID,
			    com.management.student_center.enums.StudentTuitionStatus.OVERDUE
			)
			""")
	long countUnpaidBySubject(@Param("subjectId") Long subjectId);

	@Query("""
			    SELECT COUNT(std)
			    FROM StudentTuitionDetail std
			    JOIN std.studentTuition st
			    WHERE std.subject.id = :subjectId
			      AND st.student.id = :studentId
			      AND st.status IN (
			    com.management.student_center.enums.StudentTuitionStatus.WAITING_PAYMENT,
			    com.management.student_center.enums.StudentTuitionStatus.PARTIAL_PAID,
			    com.management.student_center.enums.StudentTuitionStatus.OVERDUE
			)
			""")
	long countUnpaidByStudentAndSubject(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

	@Query("""
		    SELECT COUNT(std)
		    FROM StudentTuitionDetail std
		    JOIN std.studentTuition st
		    WHERE st.student.id = :studentId
		    AND st.status IN (
		        com.management.student_center.enums.StudentTuitionStatus.WAITING_PAYMENT,
		        com.management.student_center.enums.StudentTuitionStatus.PARTIAL_PAID,
		        com.management.student_center.enums.StudentTuitionStatus.OVERDUE
		    )
		""")
		long countUnpaidByStudent(
		        @Param("studentId") Long studentId
		);
	
	
	List<StudentTuitionDetail> findBySubjectId(Long subjectId);

	@Query("""
			SELECT d
			FROM StudentTuitionDetail d
			JOIN d.studentTuition st
			WHERE st.student.id = :studentId
			AND d.subject.id = :subjectId
			""")
	List<StudentTuitionDetail> findByStudentAndSubject(@Param("studentId") Long studentId,
			@Param("subjectId") Long subjectId);

	@Query("""
		    SELECT COUNT(d)
		    FROM StudentTuitionDetail d
		    JOIN d.studentTuition st
		    WHERE st.student.id = :studentId
		    AND d.subject.id = :subjectId
		    AND d.paymentPlanType =
		        com.management.student_center.enums.PaymentPlanType.INSTALLMENT
		""")
		long countGeneratedInstallments(
		        @Param("studentId") Long studentId,
		        @Param("subjectId") Long subjectId
		);

	@Query("""
		    SELECT MAX(d.installmentNo)
		    FROM StudentTuitionDetail d
		    JOIN d.studentTuition st
		    WHERE st.student.id = :studentId
		    AND d.subject.id = :subjectId
AND d.paymentPlanType =
		        com.management.student_center.enums.PaymentPlanType.INSTALLMENT
		""")
		Integer findLatestInstallment(
		        @Param("studentId") Long studentId,
		        @Param("subjectId") Long subjectId
		);
	
	@Query("""
		    SELECT COUNT(d)
		    FROM StudentTuitionDetail d
		    JOIN d.studentTuition st
		    WHERE st.student.id = :studentId
		    AND d.subject.id = :subjectId
		    AND d.paymentPlanType =
		        com.management.student_center.enums.PaymentPlanType.ONE_TIME
		""")
		long countOneTimeInvoices(
		        @Param("studentId") Long studentId,
		        @Param("subjectId") Long subjectId
		);
}
