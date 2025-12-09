package com.management.student_center.controller;

import com.management.student_center.entity.TeacherPayment;
import com.management.student_center.service.TeacherPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/payments")
public class TeacherPaymentController {

	private final TeacherPaymentService paymentService;

	public TeacherPaymentController(TeacherPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	/**
	 * Tạo bảng lương cho cả tháng
	 */
	@PostMapping("/create")
	public ResponseEntity<Map<String, Object>> createPayments(@RequestParam int month, @RequestParam int year,
			@RequestParam(required = false, defaultValue = "") String notes) {
		try {
			List<TeacherPayment> payments = paymentService.createTeacherPayments(month, year, notes);

			Map<String, Object> response = new HashMap<>();
			response.put("errCode", 0);
			response.put("message", "Tạo bảng lương thành công cho " + payments.size() + " giáo viên.");
			response.put("data", payments);

			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			return createErrorResponse(e);
		}
	}

	/**
	 * Thanh toán lương cho 1 giáo viên
	 */
	@PostMapping("/pay")
	public ResponseEntity<Map<String, Object>> paySalary(@RequestBody Map<String, Object> payload) {
		try {
			Long teacherId = Long.valueOf(payload.get("teacherId").toString());
			int month = Integer.parseInt(payload.get("month").toString());
			int year = Integer.parseInt(payload.get("year").toString());

			TeacherPayment result = paymentService.payTeacherSalary(teacherId, month, year);

			Map<String, Object> response = new HashMap<>();
			response.put("errCode", 0);
			response.put("message", "Thanh toán thành công!");
			response.put("data", result);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return createErrorResponse(e);
		}
	}

	/**
	 * API Lấy danh sách lương đã lưu trong DB theo tháng GET
	 * /v1/api/payments/list?month=12&year=2024
	 */
	@GetMapping("/list")
	public ResponseEntity<Map<String, Object>> getListByMonth(@RequestParam int month, @RequestParam int year) {
		try {
			List<TeacherPayment> list = paymentService.getPaymentsByMonth(month, year);

			Map<String, Object> response = new HashMap<>();
			response.put("errCode", 0);
			response.put("message", "Lấy danh sách thành công");
			response.put("data", list);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return createErrorResponse(e);
		}
	}

	/**
	 * Xem chi tiết lương
	 */
	@GetMapping("/detail")
	public ResponseEntity<Map<String, Object>> getDetail(@RequestParam Long teacherId, @RequestParam int month,
			@RequestParam int year) {
		try {
			TeacherPayment result = paymentService.getTeacherSalaryDetail(teacherId, month, year);

			if (result == null) {
				Map<String, Object> response = new HashMap<>();
				response.put("errCode", 1);
				response.put("message", "Không tìm thấy dữ liệu lương.");
				return ResponseEntity.ok(response);
			}

			Map<String, Object> response = new HashMap<>();
			response.put("errCode", 0);
			response.put("message", "OK");
			response.put("data", result);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return createErrorResponse(e);
		}
	}

	private ResponseEntity<Map<String, Object>> createErrorResponse(Exception e) {
		Map<String, Object> response = new HashMap<>();
		response.put("errCode", 500); // Hoặc 1 tùy quy ước
		response.put("message", e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
}