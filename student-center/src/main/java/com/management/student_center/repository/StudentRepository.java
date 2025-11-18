package com.management.student_center.repository;

import com.management.student_center.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByGrade(String grade);
}
