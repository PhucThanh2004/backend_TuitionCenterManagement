package com.management.student_center.repository;

import com.management.student_center.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {

	Optional<Teacher> findByUserInfoId(Long userId);

	List<Teacher> findAllByTeacherSubjects_Subject_Id(Long subjectId);

	long count();

	long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	@Query("""
			    SELECT COUNT(t)
			    FROM Teacher t
			    WHERE t.userInfo.status = true
			""")
	long countActiveTeachers();

	@Query("""
			    SELECT COUNT(t)
			    FROM Teacher t
			    WHERE t.userInfo.status = true
			      AND t.createdAt BETWEEN :start AND :end
			""")
	long countActiveTeachersCreatedBetween(LocalDateTime start, LocalDateTime end);

	// ===== Latest =====

	List<Teacher> findTop5ByUserInfo_StatusTrueOrderByCreatedAtDesc();
}