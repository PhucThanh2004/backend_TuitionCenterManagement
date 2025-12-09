package com.management.student_center.repository;

import com.management.student_center.entity.TeacherPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherPaymentRepository extends JpaRepository<TeacherPayment, Long> {
    // Thay thế cho [Op.like] %notes%
    List<TeacherPayment> findByNotesContaining(String notePart);

    // Tìm bảng lương cụ thể của giáo viên theo tháng (dựa vào notes)
    Optional<TeacherPayment> findByTeacherIdAndNotesContaining(Long teacherId, String notePart);
}