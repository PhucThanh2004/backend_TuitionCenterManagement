package com.management.student_center.repository;

import com.management.student_center.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // THÊM CÁI NÀY
import java.util.Optional; // THÊM CÁI NÀY
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long>, JpaSpecificationExecutor<Teacher> {
    
    Optional<Teacher> findByUserInfoId(Long userId); 
}