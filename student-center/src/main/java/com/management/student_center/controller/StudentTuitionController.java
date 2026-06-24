package com.management.student_center.controller;

import com.management.student_center.dto.tuition.TuitionCalculationDTO;
import com.management.student_center.dto.tuition.TuitionDetailUpdateRequest;
import com.management.student_center.dto.tuition.TuitionPaymentRequest;
import com.management.student_center.entity.StudentTuition;
import com.management.student_center.enums.StudentTuitionStatus;
import com.management.student_center.service.StudentTuitionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/tuitions")
@CrossOrigin(origins = "*")
public class StudentTuitionController {

	private final StudentTuitionService tuitionService;

	public StudentTuitionController(StudentTuitionService tuitionService) {
		this.tuitionService = tuitionService;
	}

	/**
	 * Generate invoice theo tháng
	 */
	@PostMapping("/create")
	public ResponseEntity<Map<String, Object>> createTuitions(@RequestParam int month, @RequestParam int year,
			@RequestParam(required = false) String note) {

		try {

			List<StudentTuition> data = tuitionService.createTuitions(month, year, note);

			Map<String, Object> response = new HashMap<>();

			response.put("errCode", 0);
			response.put("message", "Generate thành công");
			response.put("data", data);

			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} catch (Exception e) {

			return createErrorResponse(e);
		}
	}

	/**
	 * Danh sách hóa đơn
	 */
	@GetMapping("/list")
	public ResponseEntity<Map<String, Object>> getList(

			@RequestParam int month,

			@RequestParam int year,

			@RequestParam(required = false) String name,

			@RequestParam(required = false) String grade,

			@RequestParam(required = false) StudentTuitionStatus status) {

		try {

			List<TuitionCalculationDTO> data = tuitionService.getTuitionsWithFilter(month, year, name, grade, status);

			Map<String, Object> response = new HashMap<>();

			response.put("errCode", 0);
			response.put("message", "Success");
			response.put("data", data);

			return ResponseEntity.ok(response);

		} catch (Exception e) {

			return createErrorResponse(e);
		}
	}

	/**
	 * Chi tiết hóa đơn
	 */
	@GetMapping("/detail")
	public ResponseEntity<Map<String, Object>> getDetail(

			@RequestParam Long studentId,

			@RequestParam int month,

			@RequestParam int year) {

		try {

			StudentTuition tuition = tuitionService.getTuitionDetail(studentId, month, year);

			if (tuition == null) {

				Map<String, Object> response = new HashMap<>();

				response.put("errCode", 1);
				response.put("message", "Không tìm thấy hóa đơn");

				return ResponseEntity.ok(response);
			}

			Map<String, Object> response = new HashMap<>();

			response.put("errCode", 0);
			response.put("message", "Success");
response.put("data", tuition);

			return ResponseEntity.ok(response);

		} catch (Exception e) {

			return createErrorResponse(e);
		}
	}

	/**
	 * Thanh toán
	 */
	@PostMapping("/pay")
	public ResponseEntity<Map<String, Object>> payTuition(@RequestBody TuitionPaymentRequest request) {

		try {

			StudentTuition tuition = tuitionService.payTuition(request.getTuitionId(), request.getAmount());

			Map<String, Object> response = new HashMap<>();

			response.put("errCode", 0);
			response.put("message", "Thanh toán thành công");
			response.put("data", tuition);

			return ResponseEntity.ok(response);

		} catch (Exception e) {

			return createErrorResponse(e);
		}
	}

	/**
	 * Chỉnh sửa chi tiết học phí
	 */
	@PostMapping("/detail/update")
	public ResponseEntity<Map<String, Object>> updateDetail(@RequestBody TuitionDetailUpdateRequest request) {

		try {

			StudentTuition tuition = tuitionService.updateTuitionDetail(request);

			Map<String, Object> response = new HashMap<>();

			response.put("errCode", 0);
			response.put("message", "Cập nhật thành công");
			response.put("data", tuition);

			return ResponseEntity.ok(response);

		} catch (Exception e) {

			return createErrorResponse(e);
		}
	}

	private ResponseEntity<Map<String, Object>> createErrorResponse(Exception e) {

		e.printStackTrace();

		Map<String, Object> response = new HashMap<>();

		response.put("errCode", 1);
		response.put("message", e.getMessage());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
}