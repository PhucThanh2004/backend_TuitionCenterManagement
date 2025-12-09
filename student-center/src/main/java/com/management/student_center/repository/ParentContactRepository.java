package com.management.student_center.repository;

import com.management.student_center.entity.ParentContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParentContactRepository extends JpaRepository<ParentContact, Long> {
    void deleteByStudentId(Long studentId);
    List<ParentContact> findByStudentId(Long studentId);
}