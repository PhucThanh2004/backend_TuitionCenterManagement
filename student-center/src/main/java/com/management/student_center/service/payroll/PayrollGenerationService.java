package com.management.student_center.service.payroll;

import com.management.student_center.dto.payroll.PayrollFinalizeDTO;
import com.management.student_center.dto.payroll.PayrollPreviewRequestDTO;
import com.management.student_center.dto.payroll.PayrollPreviewResponseDTO;
import com.management.student_center.dto.payroll.PayrollSessionDetailDTO;
import com.management.student_center.dto.payroll.TeacherPayrollConfirmDTO;
import com.management.student_center.entity.SessionTeacher;
import com.management.student_center.entity.TeacherPayment;
import com.management.student_center.entity.TeacherPaymentDetail;
import com.management.student_center.enums.TeacherPaymentStatus;
import com.management.student_center.repository.SessionTeacherRepository;
import com.management.student_center.repository.TeacherPaymentDetailRepository;
import com.management.student_center.repository.TeacherPaymentRepository;
import com.management.student_center.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class PayrollGenerationService {

	private final PayrollPreviewService payrollPreviewService;

	private final TeacherRepository teacherRepository;

	private final TeacherPaymentRepository teacherPaymentRepository;

	private final TeacherPaymentDetailRepository teacherPaymentDetailRepository;

	private final SessionTeacherRepository sessionTeacherRepository;

	public PayrollGenerationService(PayrollPreviewService payrollPreviewService, TeacherRepository teacherRepository,
			TeacherPaymentRepository teacherPaymentRepository,
			TeacherPaymentDetailRepository teacherPaymentDetailRepository,
			SessionTeacherRepository sessionTeacherRepository) {
		this.payrollPreviewService = payrollPreviewService;
		this.teacherRepository = teacherRepository;
		this.teacherPaymentRepository = teacherPaymentRepository;
		this.teacherPaymentDetailRepository = teacherPaymentDetailRepository;
		this.sessionTeacherRepository = sessionTeacherRepository;
	}

	@Transactional
	public TeacherPayment generatePayroll(PayrollPreviewRequestDTO request) {

		PayrollPreviewResponseDTO preview = payrollPreviewService.previewPayroll(request);

		TeacherPayment payment = teacherPaymentRepository
				.findByTeacherInfoIdAndMonthAndYear(request.getTeacherId(), request.getMonth(), request.getYear())
				.orElse(new TeacherPayment());

		if (payment.getId() != null && payment.getStatus() != null
				&& payment.getStatus() != TeacherPaymentStatus.DRAFT) {

			throw new RuntimeException("Payroll already generated and cannot be regenerated");
		}
		payment.setTeacherInfo(teacherRepository.findById(request.getTeacherId()).orElseThrow());

		payment.setMonth(request.getMonth());

		payment.setYear(request.getYear());

		payment.setAmount(preview.getTotalAmount());

		payment.setTotalSessions(preview.getTotalSessions());
		
		payment.setPaymentDate(LocalDate.now());

		payment.setStatus(TeacherPaymentStatus.WAITING_TEACHER_CONFIRMATION);

		TeacherPayment savedPayment = teacherPaymentRepository.save(payment);

		for (PayrollSessionDetailDTO dto : preview.getSessions()) {

			boolean exists = teacherPaymentDetailRepository.existsBySessionTeacherInfoId(dto.getSessionTeacherId());

			if (exists) {

				if (!request.getOverwriteExisting()) {
					continue;
				}

				teacherPaymentDetailRepository.deleteBySessionTeacherInfoId(dto.getSessionTeacherId());
			}

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

			st.setPayrollLocked(true);

			sessionTeacherRepository.save(st);
		}

		return savedPayment;
	}

	@Transactional
	public void confirmPayroll(TeacherPayrollConfirmDTO request) {

		TeacherPayment payment = teacherPaymentRepository.findById(request.getPaymentId()).orElseThrow();

		if (payment.getStatus() != TeacherPaymentStatus.WAITING_TEACHER_CONFIRMATION) {

			throw new RuntimeException("Payroll is not waiting for teacher confirmation");
		}

		payment.setTeacherFeedback(request.getTeacherFeedback());

		payment.setTeacherConfirmedAt(LocalDateTime.now());

		payment.setStatus(TeacherPaymentStatus.TEACHER_CONFIRMED);

		teacherPaymentRepository.save(payment);
	}

	@Transactional
	public void finalizePayroll(PayrollFinalizeDTO request, Integer adminId) {

		TeacherPayment payment = teacherPaymentRepository.findById(request.getPaymentId()).orElseThrow();

		if (payment.getStatus() != TeacherPaymentStatus.TEACHER_CONFIRMED) {

			throw new RuntimeException("Payroll is not confirmed by teacher");
		}

		payment.setPayrollNote(request.getPayrollNote());

		payment.setFinalizedAt(LocalDateTime.now());

		payment.setFinalizedBy(adminId);

		payment.setStatus(TeacherPaymentStatus.FINALIZED);

		teacherPaymentRepository.save(payment);
	}
}