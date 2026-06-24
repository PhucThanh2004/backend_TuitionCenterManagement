// 3. StudentCurriculumEvaluationRepository.java
package com.management.student_center.repository;

import com.management.student_center.entity.StudentCurriculumEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StudentCurriculumEvaluationRepository extends JpaRepository<StudentCurriculumEvaluation, Long> {
    Optional<StudentCurriculumEvaluation> findByStudentIdAndCurriculumId(Long studentId, Long curriculumId);
}