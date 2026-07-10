package com.management.student_center.repository;

import com.management.student_center.entity.TeacherSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, Long> {

	// Tìm TeacherSubject theo subjectId
	Optional<TeacherSubject> findBySubjectId(Long subjectId);

	// Xoá TeacherSubject theo subjectId
	void deleteBySubjectId(Long subjectId);

	// Tìm theo cặp teacherId và subjectId để check trùng
	Optional<TeacherSubject> findByTeacherIdAndSubjectId(Long teacherId, Long subjectId);

	// Kiểm tra tồn tại
	boolean existsByTeacherIdAndSubjectId(Long teacherId, Long subjectId);

	@Query("SELECT ts FROM TeacherSubject ts " + "WHERE (:grade IS NULL OR ts.subject.grade = :grade) "
			+ "AND (:teacherName IS NULL OR LOWER(ts.teacher.userInfo.fullName) LIKE LOWER(CONCAT('%', :teacherName, '%'))) "
			+ "AND (:subjectName IS NULL OR LOWER(ts.subject.name) LIKE LOWER(CONCAT('%', :subjectName, '%')))")
	List<TeacherSubject> findByCriteria(@Param("grade") Integer grade, @Param("teacherName") String teacherName,
			@Param("subjectName") String subjectName);

	List<TeacherSubject> findByTeacherId(Long teacherId);

	List<TeacherSubject> findAllBySubjectId(Long subjectId);

	//Mới thêm
	@Query("""
			    SELECT ts
			    FROM TeacherSubject ts
			    JOIN FETCH ts.teacher t
			    JOIN FETCH t.userInfo u
			    JOIN FETCH ts.subject s
			""")
	List<TeacherSubject> findAllWithTeacherAndSubject();
}