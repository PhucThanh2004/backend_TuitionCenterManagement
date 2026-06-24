package com.management.student_center.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.management.student_center.entity.TeacherPaymentHistory;

@Repository
public interface TeacherPaymentHistoryRepository
        extends JpaRepository<TeacherPaymentHistory, Integer> {

    List<TeacherPaymentHistory>
    findByPaymentInfoIdOrderByCreatedAtAsc(Integer paymentId);

}