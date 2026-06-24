package com.management.student_center.service.tuition;

import com.management.student_center.entity.StudentPaymentTransaction;
import com.management.student_center.entity.StudentTuition;
import com.management.student_center.repository.StudentPaymentTransactionRepository;
import com.management.student_center.repository.StudentTuitionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TuitionPaymentService {

	private final StudentTuitionRepository tuitionRepository;
	private final StudentPaymentTransactionRepository paymentRepository;
	private final TuitionStatusService statusService;

	public TuitionPaymentService(StudentTuitionRepository tuitionRepository,
			StudentPaymentTransactionRepository paymentRepository, TuitionStatusService statusService) {
		this.tuitionRepository = tuitionRepository;
		this.paymentRepository = paymentRepository;
		this.statusService = statusService;
	}

	@Transactional
	public StudentTuition pay(Long tuitionId, BigDecimal amount, String paymentMethod, String note) {

		StudentTuition tuition = tuitionRepository.findById(tuitionId)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

		StudentPaymentTransaction tx = new StudentPaymentTransaction();

		tx.setStudentTuition(tuition);
		tx.setAmount(amount);
		tx.setPaymentMethod(paymentMethod);
		tx.setNote(note);
		tx.setPaymentDate(LocalDateTime.now());

		paymentRepository.save(tx);

		BigDecimal paid = tuition.getPaidAmount() == null ? BigDecimal.ZERO : tuition.getPaidAmount();

		tuition.setPaidAmount(paid.add(amount));

		statusService.refreshStatus(tuition);

		return tuitionRepository.save(tuition);
	}
}