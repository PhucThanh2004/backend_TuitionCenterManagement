package com.management.student_center.controller;

import com.management.student_center.dto.payroll.MonthlyPayrollPreviewDTO;
import com.management.student_center.dto.payroll.MonthlyPayrollStatsDTO;
import com.management.student_center.dto.payroll.PayrollDetailResponseDTO;
import com.management.student_center.dto.payroll.PayrollFinalizeDTO;
import com.management.student_center.dto.payroll.PayrollListItemDTO;
import com.management.student_center.dto.payroll.PayrollPreviewRequestDTO;
import com.management.student_center.dto.payroll.PayrollPreviewResponseDTO;
import com.management.student_center.dto.payroll.TeacherPayrollConfirmDTO;
import com.management.student_center.entity.TeacherPayment;
import com.management.student_center.service.payroll.PayrollBatchService;
import com.management.student_center.service.payroll.PayrollGenerationService;
import com.management.student_center.service.payroll.PayrollPreviewService;
import com.management.student_center.service.payroll.PayrollQueryService;
import com.management.student_center.dto.payroll.TeacherPayrollSummaryDTO;
import com.management.student_center.service.payroll.TeacherPayrollService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/payroll")
public class PayrollController {

	private final PayrollPreviewService payrollPreviewService;

	private final PayrollGenerationService payrollGenerationService;

	private final PayrollQueryService payrollQueryService;

	private final PayrollBatchService payrollBatchService;

	private final TeacherPayrollService teacherPayrollService;

	public PayrollController(PayrollPreviewService payrollPreviewService,
			PayrollGenerationService payrollGenerationService, PayrollQueryService payrollQueryService,
			PayrollBatchService payrollBatchService, TeacherPayrollService teacherPayrollService) {

		this.payrollPreviewService = payrollPreviewService;

		this.payrollGenerationService = payrollGenerationService;

		this.payrollQueryService = payrollQueryService;

		this.payrollBatchService = payrollBatchService;

		this.teacherPayrollService = teacherPayrollService;
	}

	/*
	 * PREVIEW PAYROLL
	 */
	@PostMapping("/preview")
	public ResponseEntity<PayrollPreviewResponseDTO> previewPayroll(@RequestBody PayrollPreviewRequestDTO request) {

		PayrollPreviewResponseDTO response = payrollPreviewService.previewPayroll(request);

		return ResponseEntity.ok(response);
	}

	/*
	 * GENERATE PAYROLL
	 */
	@PostMapping("/generate")
	public ResponseEntity<TeacherPayment> generatePayroll(@RequestBody PayrollPreviewRequestDTO request) {

		TeacherPayment payment = payrollGenerationService.generatePayroll(request);

		return ResponseEntity.ok(payment);
	}

	/*
	 * TEACHER CONFIRM PAYROLL
	 */
	@PostMapping("/confirm")
	public ResponseEntity<String> confirmPayroll(@RequestBody TeacherPayrollConfirmDTO request) {

		payrollGenerationService.confirmPayroll(request);

		return ResponseEntity.ok("Teacher confirmed payroll successfully");
	}

	/*
	 * FINALIZE PAYROLL
	 */
	@PostMapping("/finalize")
	public ResponseEntity<String> finalizePayroll(@RequestBody PayrollFinalizeDTO request,
			@RequestParam Integer adminId) {

		payrollGenerationService.finalizePayroll(request, adminId);

		return ResponseEntity.ok("Payroll finalized successfully");
	}

	@GetMapping
	public ResponseEntity<List<PayrollListItemDTO>> getAllPayrolls() {

	    return ResponseEntity.ok(
	            payrollQueryService.getAllPayrolls()
	    );
	}

	@GetMapping("/{id}")
	public ResponseEntity<PayrollDetailResponseDTO> getPayrollById(@PathVariable Integer id) {

		return ResponseEntity.ok(payrollQueryService.getPayrollById(id));
	}

	@GetMapping("/{id}/export")
	public ResponseEntity<PayrollDetailResponseDTO> exportPayroll(@PathVariable Integer id) {

		return ResponseEntity.ok(payrollQueryService.exportPayroll(id));
	}

	@GetMapping("/monthly-preview")
	public ResponseEntity<MonthlyPayrollPreviewDTO> previewMonthlyPayroll(

			@RequestParam Integer month,

			@RequestParam Integer year) {

		return ResponseEntity.ok(payrollBatchService.previewMonthlyPayroll(month, year));
	}

	@PostMapping("/generate-month")
	public ResponseEntity<List<TeacherPayment>> generateMonthlyPayroll(

			@RequestParam Integer month,

			@RequestParam Integer year) {

		return ResponseEntity.ok(payrollBatchService.generateMonthlyPayroll(month, year));
	}

	@GetMapping("/monthly-stats")
	public ResponseEntity<MonthlyPayrollStatsDTO> getMonthlyStats(

			@RequestParam Integer month,

			@RequestParam Integer year) {

		return ResponseEntity.ok(payrollBatchService.getMonthlyStats(month, year));
	}

	@GetMapping("/my")
	public ResponseEntity<List<TeacherPayrollSummaryDTO>> getMyPayrolls(

			@RequestParam Integer teacherId) {

		return ResponseEntity.ok(teacherPayrollService.getMyPayrolls(teacherId));
	}

	@GetMapping("/my/{paymentId}")
	public ResponseEntity<PayrollDetailResponseDTO> getMyPayrollDetail(

			@PathVariable Integer paymentId,

			@RequestParam Integer teacherId) {

		return ResponseEntity.ok(teacherPayrollService.getMyPayrollDetail(teacherId, paymentId));
	}
}