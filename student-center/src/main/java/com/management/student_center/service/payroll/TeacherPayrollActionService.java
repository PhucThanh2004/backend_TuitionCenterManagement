package com.management.student_center.service.payroll;

import com.management.student_center.dto.payroll.TeacherPayrollConfirmDTO;
import com.management.student_center.dto.payroll.TeacherPayrollRejectDTO;
import com.management.student_center.dto.payroll.TeacherPayrollAdjustmentDTO;
import com.management.student_center.entity.TeacherPayment;
import com.management.student_center.enums.TeacherPaymentStatus;
import com.management.student_center.repository.TeacherPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TeacherPayrollActionService {

	private final TeacherPaymentRepository paymentRepository;

	private final PayrollHistoryService historyService;

	public TeacherPayrollActionService(TeacherPaymentRepository paymentRepository,
			PayrollHistoryService historyService) {
		this.paymentRepository = paymentRepository;
		this.historyService = historyService;
	}

	// =========================
	// 1. CONFIRM
	// =========================
	@Transactional
	public void confirmPayroll(TeacherPayrollConfirmDTO request) {

		TeacherPayment payment = paymentRepository.findById(request.getPaymentId())
				.orElseThrow(() -> new RuntimeException("Payroll not found"));

		if (payment.getStatus() != TeacherPaymentStatus.WAITING_TEACHER_CONFIRMATION) {
			throw new RuntimeException("Invalid status for confirm");
		}

		payment.setTeacherFeedback(request.getTeacherFeedback());
		payment.setTeacherConfirmedAt(LocalDateTime.now());
		payment.setStatus(TeacherPaymentStatus.TEACHER_CONFIRMED);

		historyService.saveHistory(payment, TeacherPaymentStatus.WAITING_TEACHER_CONFIRMATION,
				TeacherPaymentStatus.TEACHER_CONFIRMED, "Teacher confirmed payroll");

		paymentRepository.save(payment);
	}

	// =========================
	// 2. REJECT
	// =========================
	@Transactional
	public void rejectPayroll(TeacherPayrollRejectDTO request) {

		TeacherPayment payment = paymentRepository.findById(request.getPaymentId())
				.orElseThrow(() -> new RuntimeException("Payroll not found"));

		if (payment.getStatus() != TeacherPaymentStatus.WAITING_TEACHER_CONFIRMATION) {
			throw new RuntimeException("Only waiting payroll can be rejected");
		}

		payment.setTeacherFeedback(request.getReason());
		payment.setStatus(TeacherPaymentStatus.REJECTED);

		historyService.saveHistory(payment, TeacherPaymentStatus.WAITING_TEACHER_CONFIRMATION,
				TeacherPaymentStatus.REJECTED, "Teacher rejected payroll");

		paymentRepository.save(payment);
	}

}