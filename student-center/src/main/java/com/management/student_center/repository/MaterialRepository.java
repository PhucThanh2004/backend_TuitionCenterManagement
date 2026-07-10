package com.management.student_center.repository;

import com.management.student_center.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
	List<Material> findBySubjectIdOrderByUploadedAtDesc(Long subjectId);

	
	//Mới thêm
	@Query("""
			    SELECT m
			    FROM Material m
			    JOIN FETCH m.subject s
			""")
	List<Material> findAllWithSubject();
}
