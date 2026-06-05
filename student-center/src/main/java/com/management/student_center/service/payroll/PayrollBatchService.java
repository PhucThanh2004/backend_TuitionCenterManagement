package com.management.student_center.service.payroll;

import com.management.student_center.dto.payroll.*;
import com.management.student_center.entity.Teacher;
import com.management.student_center.entity.TeacherPayment;
import com.management.student_center.repository.TeacherPaymentRepository;
import com.management.student_center.repository.TeacherRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayrollBatchService {

	private final TeacherRepository teacherRepository;

	private final PayrollPreviewService payrollPreviewService;

	private final PayrollGenerationService payrollGenerationService;

	private final TeacherPaymentRepository paymentRepository;

	public PayrollBatchService(TeacherRepository teacherRepository, PayrollPreviewService payrollPreviewService,
			PayrollGenerationService payrollGenerationService, TeacherPaymentRepository paymentRepository) {
		this.teacherRepository = teacherRepository;
		this.payrollPreviewService = payrollPreviewService;
		this.payrollGenerationService = payrollGenerationService;
		this.paymentRepository = paymentRepository;
	}

	private static final Logger log =
	        LoggerFactory.getLogger(PayrollBatchService.class);
	
	public MonthlyPayrollPreviewDTO previewMonthlyPayroll(Integer month, Integer year) {

		List<Teacher> teachers = teacherRepository.findAll();

		List<MonthlyPayrollTeacherDTO> teacherDtos = new ArrayList<>();

		BigDecimal totalAmount = BigDecimal.ZERO;

		for (Teacher teacher : teachers) {

			PayrollPreviewRequestDTO request = new PayrollPreviewRequestDTO();

			request.setTeacherId(teacher.getId());

			request.setMonth(month);

			request.setYear(year);

			PayrollPreviewResponseDTO preview;

			try {

				preview = payrollPreviewService.previewPayroll(request);

			} catch (Exception ex) {

				continue;
			}

			if (preview.getTotalSessions() == 0) {
				continue;
			}

			MonthlyPayrollTeacherDTO dto = new MonthlyPayrollTeacherDTO();

			dto.setTeacherId(teacher.getId());

			dto.setTeacherName(teacher.getUserInfo().getFullName());

			dto.setTotalSessions(preview.getTotalSessions());

			dto.setAmount(preview.getTotalAmount());

			teacherDtos.add(dto);

			totalAmount = totalAmount.add(preview.getTotalAmount());
		}

		MonthlyPayrollPreviewDTO response = new MonthlyPayrollPreviewDTO();

		response.setMonth(month);

		response.setYear(year);

		response.setTeachers(teacherDtos);

		response.setTotalTeachers(teacherDtos.size());

		response.setTotalPayrollAmount(totalAmount);

		return response;
	}

	public List<TeacherPayment> generateMonthlyPayroll(Integer month, Integer year) {

		List<Teacher> teachers = teacherRepository.findAll();

		List<TeacherPayment> result = new ArrayList<>();

		for (Teacher teacher : teachers) {

			try {

				PayrollPreviewRequestDTO request = new PayrollPreviewRequestDTO();

				request.setTeacherId(teacher.getId());

				request.setMonth(month);

				request.setYear(year);

				request.setOverwriteExisting(false);

				PayrollPreviewResponseDTO preview = payrollPreviewService.previewPayroll(request);

				if (preview.getTotalSessions() == 0) {
					continue;
				}

				TeacherPayment payment = payrollGenerationService.generatePayroll(request);

				result.add(payment);

			} catch (Exception ex) {

				log.error("Generate payroll failed for teacher {}", teacher.getId(), ex);
			}
		}

		return result;
	}

	public MonthlyPayrollStatsDTO getMonthlyStats(Integer month, Integer year) {

		List<TeacherPayment> payments = paymentRepository.findByMonthAndYear(month, year);

		MonthlyPayrollStatsDTO dto = new MonthlyPayrollStatsDTO();

		dto.setMonth(month);

		dto.setYear(year);

		dto.setTeacherCount(payments.size());

		int totalSessions = 0;

		BigDecimal totalAmount = BigDecimal.ZERO;

		for (TeacherPayment payment : payments) {

			totalSessions += payment.getTotalSessions();

			totalAmount = totalAmount.add(payment.getAmount());
		}

		dto.setSessionCount(totalSessions);

		dto.setTotalAmount(totalAmount);

		return dto;
	}
}