package com.management.student_center.controller;

import com.management.student_center.dto.payroll.MonthlyPayrollPreviewDTO;
import com.management.student_center.dto.payroll.MonthlyPayrollStatsDTO;
import com.management.student_center.dto.payroll.PayrollDetailResponseDTO;
import com.management.student_center.dto.payroll.PayrollFinalizeDTO;
import com.management.student_center.dto.payroll.PayrollListItemDTO;
import com.management.student_center.dto.payroll.PayrollPreviewRequestDTO;
import com.management.student_center.dto.payroll.PayrollPreviewResponseDTO;
import com.management.student_center.dto.payroll.TeacherPaymentResponseDTO;
import com.management.student_center.dto.payroll.TeacherPayrollAdjustmentDTO;
import com.management.student_center.dto.payroll.TeacherPayrollConfirmDTO;
import com.management.student_center.dto.payroll.TeacherPayrollRejectDTO;
import com.management.student_center.entity.TeacherPayment;
import com.management.student_center.service.payroll.PayrollBatchService;
import com.management.student_center.service.payroll.PayrollGenerationService;
import com.management.student_center.service.payroll.PayrollPreviewService;
import com.management.student_center.service.payroll.PayrollQueryService;
import com.management.student_center.service.payroll.TeacherPayrollActionService;
import com.management.student_center.dto.payroll.TeacherPayrollSummaryDTO;
import com.management.student_center.service.payroll.TeacherPayrollService;
import com.management.student_center.service.payroll.PayrollExcelExportService;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/v1/api/payroll")
public class PayrollController {

	private final PayrollPreviewService payrollPreviewService;

	private final PayrollGenerationService payrollGenerationService;

	private final PayrollQueryService payrollQueryService;

	private final PayrollBatchService payrollBatchService;

	private final TeacherPayrollService teacherPayrollService;

	private final PayrollExcelExportService excelExportService;

	private final TeacherPayrollActionService teacherPayrollActionService;

	public PayrollController(PayrollPreviewService payrollPreviewService,
			PayrollGenerationService payrollGenerationService, PayrollQueryService payrollQueryService,
			PayrollBatchService payrollBatchService, TeacherPayrollService teacherPayrollService,
			PayrollExcelExportService excelExportService, TeacherPayrollActionService teacherPayrollActionService) {

		this.payrollPreviewService = payrollPreviewService;

		this.payrollGenerationService = payrollGenerationService;

		this.payrollQueryService = payrollQueryService;

		this.payrollBatchService = payrollBatchService;

		this.teacherPayrollService = teacherPayrollService;

		this.excelExportService = excelExportService;

		this.teacherPayrollActionService = teacherPayrollActionService;
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

		teacherPayrollActionService.confirmPayroll(request);

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

		return ResponseEntity.ok(payrollQueryService.getAllPayrolls());
	}

	@GetMapping("/{id}")
	public ResponseEntity<PayrollDetailResponseDTO> getPayrollById(@PathVariable Integer id) {

		return ResponseEntity.ok(payrollQueryService.getPayrollById(id));
	}

	@GetMapping("/{id}/export")
	public ResponseEntity<InputStreamResource> exportPayroll(@PathVariable Integer id) {
		PayrollDetailResponseDTO payroll = payrollQueryService.getPayrollById(id);
		ByteArrayInputStream excelStream = excelExportService.exportPayrollDetailToExcel(payroll);

		String filename = "bang_luong_gv_" + payroll.getTeacherName() + "_" + payroll.getMonth() + "_"
				+ payroll.getYear() + ".xlsx";
		// Loại bỏ ký tự đặc biệt trong tên file
		filename = filename.replaceAll("[^a-zA-Z0-9._-]", "_");

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.contentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(new InputStreamResource(excelStream));
	}

	// Xuất tất cả bảng lương ra Excel
	@GetMapping("/export/all")
	public ResponseEntity<InputStreamResource> exportAllPayrolls() {
		List<PayrollListItemDTO> payrolls = payrollQueryService.getAllPayrolls();
		ByteArrayInputStream excelStream = excelExportService.exportAllPayrollsToExcel(payrolls);

		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String filename = "danh_sach_bang_luong_" + timestamp + ".xlsx";

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
				.contentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(new InputStreamResource(excelStream));
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

	@PostMapping("/reject")
	public ResponseEntity<String> rejectPayroll(@RequestBody TeacherPayrollRejectDTO request) {

		teacherPayrollActionService.rejectPayroll(request);

		return ResponseEntity.ok("Teacher rejected payroll successfully");
	}

	@PostMapping("/regenerate")
	public ResponseEntity<TeacherPaymentResponseDTO> regenerate(@RequestBody PayrollPreviewRequestDTO request) {

		TeacherPaymentResponseDTO response =
	            payrollGenerationService.regeneratePayroll(request);

		return ResponseEntity.ok(response);
	}
	
}