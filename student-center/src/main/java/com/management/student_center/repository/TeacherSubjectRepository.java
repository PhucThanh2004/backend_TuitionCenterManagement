package com.management.student_center.repository;

import com.management.student_center.entity.TeacherSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
