package com.management.student_center.service.payroll;

import com.management.student_center.dto.payroll.PayrollDetailResponseDTO;
import com.management.student_center.dto.payroll.TeacherPayrollSummaryDTO;
import com.management.student_center.entity.TeacherPayment;
import com.management.student_center.repository.TeacherPaymentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherPayrollService {

	private final TeacherPaymentRepository paymentRepository;
	private final PayrollQueryService payrollQueryService;

	public TeacherPayrollService(TeacherPaymentRepository paymentRepository, PayrollQueryService payrollQueryService) {
		this.paymentRepository = paymentRepository;
		this.payrollQueryService = payrollQueryService;
	}

	public List<TeacherPayrollSummaryDTO> getMyPayrolls(Integer teacherId) {
		List<TeacherPayment> payments = paymentRepository.findByTeacherInfoIdOrderByYearDescMonthDesc(teacherId);

		List<TeacherPayrollSummaryDTO> result = new ArrayList<>();

		for (TeacherPayment payment : payments) {
			TeacherPayrollSummaryDTO dto = new TeacherPayrollSummaryDTO();
			dto.setPaymentId(payment.getId());
			dto.setMonth(payment.getMonth());
			dto.setYear(payment.getYear());
			dto.setAmount(payment.getAmount());
			dto.setTotalSessions(payment.getTotalSessions());
			dto.setStatus(payment.getStatus());
			result.add(dto);
		}

		return result;
	}

	public PayrollDetailResponseDTO getMyPayrollDetail(
	        Integer teacherId,
	        Integer paymentId
	) {
	    TeacherPayment payment = paymentRepository.findById(paymentId)
	            .orElseThrow(() -> new RuntimeException("Payroll not found"));
	    
	    // THÊM ĐOẠN DEBUG NÀY
	    System.out.println("========== DEBUG INFO ==========");
	    System.out.println("1. teacherId from request: " + teacherId);
	    System.out.println("2. payment.getId(): " + payment.getId());
	    System.out.println("3. payment.getTeacherInfo(): " + payment.getTeacherInfo());
	    
	    if (payment.getTeacherInfo() != null) {
	        System.out.println("4. payment.getTeacherInfo().getId(): " + payment.getTeacherInfo().getId());
	        System.out.println("5. Equals comparison: " + payment.getTeacherInfo().getId().equals(teacherId));
	    } else {
	        System.out.println("4. payment.getTeacherInfo() is NULL!");
	    }
	    
	    // Kiểm tra thêm
	    System.out.println("6. payment.getMonth(): " + payment.getMonth());
	    System.out.println("7. payment.getYear(): " + payment.getYear());
	    System.out.println("=================================");
	    
	    // Comment dòng này để test tạm
	    /*
	    if (!payment.getTeacherInfo().getId().equals(teacherId)) {
	        throw new RuntimeException("You cannot access this payroll");
	    }
	    */
	    
	    return payrollQueryService.getPayrollById(paymentId);
	}
}