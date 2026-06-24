// 4. StudentSessionEvaluationRepository.java
package com.management.student_center.repository;

import com.management.student_center.entity.StudentSessionEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSessionEvaluationRepository extends JpaRepository<StudentSessionEvaluation, Long> {
   
 // Tìm đánh giá theo studentId và sessionDetailId
    Optional<StudentSessionEvaluation> findByStudentIdAndSessionDetailId(Long studentId, Long sessionDetailId);
    
    // Tìm tất cả đánh giá của student theo danh sách sessionDetailIds
    @Query("SELECT e FROM StudentSessionEvaluation e WHERE e.studentId = :studentId AND e.sessionDetailId IN :sessionDetailIds")
    List<StudentSessionEvaluation> findByStudentIdAndSessionDetailIds(
        @Param("studentId") Long studentId, 
        @Param("sessionDetailIds") List<Long> sessionDetailIds);
    
    // Tìm tất cả đánh giá của student theo curriculum
    @Query("SELECT e FROM StudentSessionEvaluation e WHERE e.studentId = :studentId AND e.sessionDetailId IN " +
           "(SELECT sd.id FROM SessionDetail sd WHERE sd.curriculum.id = :curriculumId)")
    List<StudentSessionEvaluation> findByStudentIdAndCurriculumId(
        @Param("studentId") Long studentId, 
        @Param("curriculumId") Long curriculumId);
    
    List<StudentSessionEvaluation> findByStudentId(Long studentId);
}