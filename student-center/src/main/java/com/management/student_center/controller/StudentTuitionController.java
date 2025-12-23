package com.management.student_center.controller;

import com.management.student_center.dto.tuition.TuitionCalculationDTO;
import com.management.student_center.dto.tuition.TuitionDetailUpdateRequest;
// Import DTO vừa tạo
import com.management.student_center.dto.tuition.TuitionPaymentRequest;
import com.management.student_center.entity.StudentTuition;
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
	 * 1. 🟢 Tạo hóa đơn học phí cho cả tháng
	 */
	@PostMapping("/create")
	public ResponseEntity<Map<String, Object>> createTuitions(@RequestParam int month, @RequestParam int year,
			@RequestParam(required = false, defaultValue = "") String notes) {
		try {
			List<StudentTuition> tuitions = tuitionService.createTuitions(month, year, notes);

			Map<String, Object> response = new HashMap<>();
			response.put("errCode", 0);
			response.put("message", "Tạo hóa đơn thành công cho " + tuitions.size() + " học sinh.");
			response.put("data", tuitions);

			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			return createErrorResponse(e);
		}
	}

	/**
	 * 2. 🔵 Lấy danh sách hóa đơn theo tháng
	 */
	@GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getList(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String status) {
        try {
            // Gọi hàm getTuitionsWithFilter mới trong Service
            List<TuitionCalculationDTO> list = tuitionService.getTuitionsWithFilter(month, year, name, grade, status);

            Map<String, Object> response = new HashMap<>();
            response.put("errCode", 0);
            response.put("message", "Lấy dữ liệu thành công");
            response.put("data", list); // Trả về DTO đã tính toán số dư

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

	/**
	 * 3. 🟡 Xem chi tiết hóa đơn của 1 học sinh
	 */
	@GetMapping("/detail")
    public ResponseEntity<Map<String, Object>> getDetail(
            @RequestParam Long studentId, 
            @RequestParam int month,
            @RequestParam int year) {
        try {
            // Gọi đúng hàm có sẵn trong Service của bạn
            StudentTuition result = tuitionService.getTuitionDetail(studentId, month, year);

            if (result == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("errCode", 1);
                response.put("message", "Không tìm thấy dữ liệu học phí cho học sinh này.");
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

	/**
	 * 4. 🟣 Thanh toán học phí Sử dụng DTO đã tách file: TuitionPaymentRequest
	 */
	@PostMapping("/pay")
    public ResponseEntity<Map<String, Object>> payTuition(@RequestBody TuitionPaymentRequest request) {
        try {
            // Validate đầu vào
            if (request.getTuitionId() == null || request.getAmount() == null) {
                throw new IllegalArgumentException("Thiếu thông tin tuitionId hoặc amount.");
            }

            // Gọi hàm payTuition mới trong Service (nhận ID và Amount)
            StudentTuition result = tuitionService.payTuition(request.getTuitionId(), request.getAmount());

            Map<String, Object> response = new HashMap<>();
            response.put("errCode", 0);
            response.put("message", "Thanh toán thành công!");
            response.put("data", result); // Trả về hóa đơn đã cập nhật số tiền đã đóng

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    // --- Hàm phụ trợ trả lỗi ---
    private ResponseEntity<Map<String, Object>> createErrorResponse(Exception e) {
        e.printStackTrace(); // Log lỗi ra console để debug
        Map<String, Object> response = new HashMap<>();

        // Xử lý các loại lỗi cụ thể nếu cần
        if (e instanceof IllegalArgumentException) {
            response.put("errCode", 400);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("errCode", 1);
        response.put("message", e.getMessage() != null ? e.getMessage() : "Lỗi hệ thống");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    @PostMapping("/detail/update")
    public ResponseEntity<Map<String, Object>> updateDetail(@RequestBody TuitionDetailUpdateRequest request) {
        try {
            StudentTuition updatedTuition = tuitionService.updateTuitionDetail(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("errCode", 0);
            response.put("message", "Cập nhật thành công");
            response.put("data", updatedTuition); // Trả về hóa đơn mới nhất để FE cập nhật
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // ... xử lý lỗi như cũ
            return createErrorResponse(e);
        }
    }
}