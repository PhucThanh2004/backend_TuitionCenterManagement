package com.management.student_center.repository;

import com.management.student_center.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
	List<Student> findByGrade(String grade);

	Optional<Student> findByUserInfoId(Long userId);

	// Tổng số học sinh
	long count();

	// Đếm số học sinh được tạo trong khoảng thời gian
	long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	// Lấy 5 học sinh mới nhất
	List<Student> findTop5ByOrderByCreatedAtDesc();

	@Query("SELECT DISTINCT s.schoolName FROM Student s WHERE s.schoolName IS NOT NULL AND s.schoolName != ''")
	List<String> findDistinctSchoolNames();

	@Query("""
		    SELECT COUNT(s)
		    FROM Student s
		    WHERE s.userInfo.status = true
		""")
		long countActiveStudents();
	
	List<Student> findTop5ByUserInfo_StatusTrueOrderByCreatedAtDesc();
	
	@Query("""
		    SELECT COUNT(s)
		    FROM Student s
		    WHERE s.userInfo.status = true
		      AND s.createdAt BETWEEN :start AND :end
		""")
		long countActiveStudentsCreatedBetween(
		        LocalDateTime start,
		        LocalDateTime end
		);
}