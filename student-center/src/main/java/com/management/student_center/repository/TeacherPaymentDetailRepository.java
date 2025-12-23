package com.management.student_center.repository;

import com.management.student_center.entity.TeacherPaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherPaymentDetailRepository extends JpaRepository<TeacherPaymentDetail, Long> {
	  @Query("""
		        SELECT COUNT(tpd)
		        FROM TeacherPaymentDetail tpd
		        JOIN tpd.payment tp
		        WHERE tpd.subject.id = :subjectId
		          AND (tp.status = 'unpaid' OR tp.status = 'partial')
		    """)
		    long countUnpaidBySubject(@Param("subjectId") Long subjectId);
}