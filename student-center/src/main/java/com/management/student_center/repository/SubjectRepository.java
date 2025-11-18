package com.management.student_center.repository;

import com.management.student_center.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Page<Subject> findByStatus(String status, Pageable pageable);

    long countByStatus(String status);

    @Query("SELECT COUNT(ss) FROM StudentSubject ss WHERE ss.subject.id = :subjectId")
    long countCurrentStudents(Long subjectId);
}
