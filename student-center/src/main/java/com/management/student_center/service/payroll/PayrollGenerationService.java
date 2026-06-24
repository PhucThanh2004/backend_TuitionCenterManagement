package com.management.student_center.service.payroll;

import com.management.student_center.dto.payroll.*;
import com.management.student_center.entity.*;
import com.management.student_center.enums.TeacherPaymentStatus;
import com.management.student_center.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class PayrollGenerationService {

	private final PayrollPreviewService payrollPreviewService;
	private final TeacherRepository teacherRepository;
	private final TeacherPaymentRepository teacherPaymentRepository;
	private final TeacherPaymentDetailRepository teacherPaymentDetailRepository;
	private final SessionTeacherRepository sessionTeacherRepository;
	private final PayrollHistoryService historyService;

	public PayrollGenerationService(PayrollPreviewService payrollPreviewService, TeacherRepository teacherRepository,
			TeacherPaymentRepository teacherPaymentRepository,
			TeacherPaymentDetailRepository teacherPaymentDetailRepository,
			SessionTeacherRepository sessionTeacherRepository, PayrollHistoryService historyService) {
		this.payrollPreviewService = payrollPreviewService;
		this.teacherRepository = teacherRepository;
		this.teacherPaymentRepository = teacherPaymentRepository;
		this.teacherPaymentDetailRepository = teacherPaymentDetailRepository;
		this.sessionTeacherRepository = sessionTeacherRepository;
		this.historyService = historyService;

	}

	// =====================================================
	// 1. GENERATE PAYROLL
	// =====================================================
	@Transactional
	public TeacherPayment generatePayroll(PayrollPreviewRequestDTO request, boolean saveHistory) {

		PayrollPreviewResponseDTO preview = payrollPreviewService.previewPayroll(request);

		TeacherPayment payment = teacherPaymentRepository
				.findByTeacherInfoIdAndMonthAndYear(request.getTeacherId(), request.getMonth(), request.getYear())
				.orElse(null);

		// CREATE NEW IF NOT EXISTS
		if (payment == null) {
			payment = new TeacherPayment();
			payment.setTeacherInfo(teacherRepository.findById(request.getTeacherId()).orElseThrow());
			payment.setMonth(request.getMonth());
			payment.setYear(request.getYear());
			if (payment.getRevisionNo() == null) {
				payment.setRevisionNo(1);
			}
		}

		if (payment.getStatus() == TeacherPaymentStatus.FINALIZED) {
			throw new RuntimeException("Cannot modify finalized payroll");
		}

		if (payment.getId() != null) {

			teacherPaymentDetailRepository.deleteByPaymentInfoId(payment.getId());

			teacherPaymentDetailRepository.flush();
		}

		payment.setAmount(preview.getTotalAmount());
		payment.setTotalSessions(preview.getTotalSessions());
		payment.setPaymentDate(LocalDate.now());
		payment.setStatus(TeacherPaymentStatus.WAITING_TEACHER_CONFIRMATION);

		TeacherPayment savedPayment = teacherPaymentRepository.save(payment);

		if (saveHistory) {

			historyService.saveHistory(savedPayment, null, TeacherPaymentStatus.WAITING_TEACHER_CONFIRMATION,
					"Payroll generated");
		}
		// CREATE DETAILS
		for (PayrollSessionDetailDTO dto : preview.getSessions()) {

			SessionTeacher st = sessionTeacherRepository.findById(dto.getSessionTeacherId()).orElseThrow();

			TeacherPaymentDetail detail = new TeacherPaymentDetail();

			detail.setPaymentInfo(savedPayment);
			detail.setSessionTeacherInfo(st);
			detail.setSalaryType(dto.getSalaryType());
			detail.setSalaryRate(dto.getSalaryRate());
			detail.setWorkedHours(dto.getWorkedHours());
			detail.setBaseAmount(dto.getAmount());
			detail.setFinalAmount(dto.getAmount());
			detail.setPayrollDate(dto.getSessionDate());
			detail.setNote(dto.getNote());

			teacherPaymentDetailRepository.save(detail);
		}

		return savedPayment;
	}

	@Transactional
	public TeacherPayment generatePayroll(PayrollPreviewRequestDTO request) {

		return generatePayroll(request, true);
	}

	// =====================================================
	// 2. CONFIRM PAYROLL
	// =====================================================
	@Transactional
	public void confirmPayroll(TeacherPayrollConfirmDTO request) {

		TeacherPayment payment = teacherPaymentRepository.findById(request.getPaymentId())
				.orElseThrow(() -> new RuntimeException("Payroll not found"));

		if (payment.getStatus() != TeacherPaymentStatus.WAITING_TEACHER_CONFIRMATION) {
			throw new RuntimeException("Invalid status for confirm");
		}

		payment.setTeacherFeedback(request.getTeacherFeedback());
		payment.setTeacherConfirmedAt(LocalDateTime.now());
		payment.setStatus(TeacherPaymentStatus.TEACHER_CONFIRMED);
		historyService.saveHistory(payment, TeacherPaymentStatus.WAITING_TEACHER_CONFIRMATION,
				TeacherPaymentStatus.TEACHER_CONFIRMED, "Teacher confirmed payroll");
		teacherPaymentRepository.save(payment);
	}

	// =====================================================
	// 3. FINALIZE PAYROLL (LOCK DATA HERE)
	// =====================================================
	@Transactional
	public void finalizePayroll(PayrollFinalizeDTO request, Integer adminId) {

		TeacherPayment payment = teacherPaymentRepository.findById(request.getPaymentId())
				.orElseThrow(() -> new RuntimeException("Payroll not found"));

		if (payment.getStatus() != TeacherPaymentStatus.TEACHER_CONFIRMED) {
			throw new RuntimeException("Payroll is not confirmed by teacher");
		}

		payment.setPayrollNote(request.getPayrollNote());
		payment.setFinalizedAt(LocalDateTime.now());
		payment.setFinalizedBy(adminId);
		payment.setStatus(TeacherPaymentStatus.FINALIZED);
		historyService.saveHistory(payment, TeacherPaymentStatus.TEACHER_CONFIRMED, TeacherPaymentStatus.FINALIZED,
				"Admin finalized payroll");
		teacherPaymentRepository.save(payment);

		// LOCK SESSION TEACHER HERE (IMPORTANT FIX)
		for (TeacherPaymentDetail detail : teacherPaymentDetailRepository.findByPaymentInfoId(payment.getId())) {

			SessionTeacher st = detail.getSessionTeacherInfo();
			st.setPayrollLocked(true);

			sessionTeacherRepository.save(st);
		}
	}

	// =====================================================
	// 4. REGENERATE PAYROLL (FIXED LOGIC)
	// =====================================================
	@Transactional
	public TeacherPaymentResponseDTO regeneratePayroll(PayrollPreviewRequestDTO request) {

		TeacherPayment payment = teacherPaymentRepository
				.findByTeacherInfoIdAndMonthAndYear(request.getTeacherId(), request.getMonth(), request.getYear())
				.orElseThrow(() -> new RuntimeException("Payroll not found"));

		if (payment.getStatus() != TeacherPaymentStatus.REJECTED
				&& payment.getStatus() != TeacherPaymentStatus.REQUEST_ADJUSTMENT) {
			throw new RuntimeException("Only REJECTED or REQUEST_ADJUSTMENT can be regenerated");
		}

		TeacherPaymentStatus oldStatus = payment.getStatus();

		payment.setPreviousAmount(payment.getAmount());

		payment.setRevisionNo((payment.getRevisionNo() == null ? 1 : payment.getRevisionNo()) + 1);

		payment.setAdjustedAt(LocalDateTime.now());

		payment.setStatus(TeacherPaymentStatus.WAITING_TEACHER_CONFIRMATION);

		teacherPaymentRepository.save(payment);

		historyService.saveHistory(payment, oldStatus, TeacherPaymentStatus.WAITING_TEACHER_CONFIRMATION,
				"Admin regenerated payroll after " + oldStatus);

		TeacherPayment generatedPayment = generatePayroll(request, false);

		TeacherPaymentResponseDTO dto = new TeacherPaymentResponseDTO();

		dto.setId(generatedPayment.getId());

		dto.setTeacherId(generatedPayment.getTeacherInfo().getId());

		dto.setTeacherName(generatedPayment.getTeacherInfo().getUserInfo().getFullName());

		dto.setMonth(generatedPayment.getMonth());

		dto.setYear(generatedPayment.getYear());

		dto.setAmount(generatedPayment.getAmount());

		dto.setStatus(generatedPayment.getStatus().name());

		dto.setRevisionNo(generatedPayment.getRevisionNo());

		return dto;
	}

}