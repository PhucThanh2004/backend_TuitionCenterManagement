package com.management.student_center.repository;

import com.management.student_center.entity.TeacherPaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherPaymentDetailRepository extends JpaRepository<TeacherPaymentDetail, Long> {
}