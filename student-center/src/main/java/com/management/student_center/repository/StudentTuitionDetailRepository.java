package com.management.student_center.repository;

import com.management.student_center.entity.StudentTuitionDetail;
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
          AND (st.status = 'unpaid' OR st.status = 'partial')
    """)
    long countUnpaidBySubject(@Param("subjectId") Long subjectId);
}
