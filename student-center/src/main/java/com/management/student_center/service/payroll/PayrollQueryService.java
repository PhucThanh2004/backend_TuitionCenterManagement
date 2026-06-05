package com.management.student_center.service.payroll;

import com.management.student_center.dto.payroll.PayrollDetailResponseDTO;
import com.management.student_center.dto.payroll.PayrollListItemDTO;
import com.management.student_center.dto.payroll.PayrollSessionDetailDTO;
import com.management.student_center.entity.TeacherPayment;
import com.management.student_center.entity.TeacherPaymentDetail;
import com.management.student_center.repository.TeacherPaymentDetailRepository;
import com.management.student_center.repository.TeacherPaymentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PayrollQueryService {

	private final TeacherPaymentRepository paymentRepository;

	private final TeacherPaymentDetailRepository detailRepository;

	public PayrollQueryService(TeacherPaymentRepository paymentRepository,
			TeacherPaymentDetailRepository detailRepository) {
		this.paymentRepository = paymentRepository;
		this.detailRepository = detailRepository;
	}

	public PayrollDetailResponseDTO getPayrollById(Integer paymentId) {

		TeacherPayment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new RuntimeException("Payroll not found"));

		List<TeacherPaymentDetail> details = detailRepository.findByPaymentInfoId(paymentId);

		PayrollDetailResponseDTO response = new PayrollDetailResponseDTO();

		response.setPaymentId(payment.getId());

		response.setTeacherId(payment.getTeacherInfo().getId());

		response.setTeacherName(payment.getTeacherInfo().getUserInfo().getFullName());

		response.setMonth(payment.getMonth());

		response.setYear(payment.getYear());

		response.setAmount(payment.getAmount());

		response.setTotalSessions(payment.getTotalSessions());

		response.setStatus(payment.getStatus());

		response.setPaymentDate(payment.getPaymentDate());

		List<PayrollSessionDetailDTO> sessionDtos = new ArrayList<>();

		for (TeacherPaymentDetail detail : details) {

			PayrollSessionDetailDTO dto = new PayrollSessionDetailDTO();

			dto.setSessionTeacherId(detail.getSessionTeacherInfo().getId());

			dto.setSessionId(detail.getSessionTeacherInfo().getSessionInfo().getId());

			dto.setSubjectName(detail.getSessionTeacherInfo().getSessionInfo().getSubject().getName());

			dto.setSessionDate(detail.getPayrollDate());

			dto.setSalaryType(detail.getSalaryType());

			dto.setSalaryRate(detail.getSalaryRate());

			dto.setWorkedHours(detail.getWorkedHours());

			dto.setAmount(detail.getFinalAmount());

			dto.setNote(detail.getNote());

			sessionDtos.add(dto);
		}

		response.setDetails(sessionDtos);

		return response;
	}

	public PayrollDetailResponseDTO exportPayroll(Integer paymentId) {

		return getPayrollById(paymentId);
	}

	public List<PayrollListItemDTO> getAllPayrolls() {

		List<TeacherPayment> payments = paymentRepository.findAllByOrderByCreatedAtDesc();

		return payments.stream().map(payment -> {

			PayrollListItemDTO dto = new PayrollListItemDTO();

			dto.setPaymentId(payment.getId());

			dto.setTeacherId(payment.getTeacherInfo().getId());

			dto.setTeacherName(payment.getTeacherInfo().getUserInfo().getFullName());

			dto.setMonth(payment.getMonth());

			dto.setYear(payment.getYear());

			dto.setAmount(payment.getAmount());

			dto.setTotalSessions(payment.getTotalSessions());

			dto.setStatus(payment.getStatus());

			dto.setPaymentDate(payment.getPaymentDate());

			return dto;
		}).toList();
	}
}