package com.management.student_center.service.payroll;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.management.student_center.entity.TeacherPayment;
import com.management.student_center.entity.TeacherPaymentHistory;
import com.management.student_center.enums.TeacherPaymentStatus;
import com.management.student_center.repository.TeacherPaymentHistoryRepository;

@Service
public class PayrollHistoryService {

    private final TeacherPaymentHistoryRepository repository;

    public PayrollHistoryService(
            TeacherPaymentHistoryRepository repository) {
        this.repository = repository;
    }

    public void saveHistory(
            TeacherPayment payment,
            TeacherPaymentStatus oldStatus,
            TeacherPaymentStatus newStatus,
            String note) {

        TeacherPaymentHistory history =
                new TeacherPaymentHistory();

        history.setPaymentInfo(payment);

        history.setRevisionNo(
                payment.getRevisionNo());

        history.setOldStatus(
                oldStatus == null ? null : oldStatus.name());

        history.setNewStatus(
                newStatus.name());

        history.setAmount(
                payment.getAmount());

        history.setTeacherFeedback(
                payment.getTeacherFeedback());

        history.setActionNote(note);

        history.setCreatedAt(
                LocalDateTime.now());

        repository.save(history);
    }
}