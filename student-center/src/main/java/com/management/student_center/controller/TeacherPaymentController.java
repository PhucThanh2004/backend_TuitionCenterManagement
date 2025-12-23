package com.management.student_center.controller;

import com.management.student_center.dto.payment.PaymentRequest; 
import com.management.student_center.dto.payment.TeacherPaymentDetailUpdateRequest; // <--- Import DTO Update mới
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
     * 1. Tạo bảng lương cho cả tháng
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createPayments(
            @RequestParam int month, 
            @RequestParam int year,
            @RequestParam(required = false, defaultValue = "") String notes) {
        try {
            List<TeacherPayment> payments = paymentService.createTeacherPayments(month, year, notes);

            Map<String, Object> response = new HashMap<>();
            response.put("errCode", 0);
            response.put("message", "Tạo bảng lương thành công!");
            response.put("data", payments);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            // Xử lý lỗi trùng lặp (Logic cũ của bạn)
            if (e.getMessage().contains("đã được tạo trước đó")) {
                Map<String, Object> response = new HashMap<>();
                response.put("errCode", 409); 
                response.put("message", e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            return createErrorResponse(e);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    /**
     * 2. Lấy danh sách (Kèm bộ lọc Tên, Trạng thái)
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getList(
            @RequestParam int month, 
            @RequestParam int year,
            @RequestParam(required = false) String name,   // Thêm lọc theo tên
            @RequestParam(required = false) String status) // Thêm lọc theo trạng thái
    {
        try {
            // Gọi hàm getPaymentsWithFilter thay vì getPaymentsByMonth cũ
            List<TeacherPayment> list = paymentService.getPaymentsWithFilter(month, year, name, status);
            
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
     * 3. Xem chi tiết lương 1 giáo viên
     */
    @GetMapping("/detail")
    public ResponseEntity<Map<String, Object>> getDetail(
            @RequestParam Long teacherId, 
            @RequestParam int month,
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

    /**
     * 4. Thanh toán lương (Hỗ trợ trả từng phần)
     * Request Body cần: paymentId (ID hóa đơn) và amount (Số tiền trả)
     */
    @PostMapping("/pay")
    public ResponseEntity<Map<String, Object>> paySalary(@RequestBody PaymentRequest request) {
        try {
            // Logic mới: Dùng ID hóa đơn và số tiền
            TeacherPayment result = paymentService.payTeacherSalary(
                request.getPaymentId(), 
                request.getAmount()
            );

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
     * 5. Cập nhật chi tiết lương (Số buổi/Tiền/Ghi chú)
     * API Mới thêm vào
     */
    @PostMapping("/update-detail")
    public ResponseEntity<Map<String, Object>> updateDetail(@RequestBody TeacherPaymentDetailUpdateRequest request) {
        try {
            TeacherPayment result = paymentService.updatePaymentDetail(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("errCode", 0);
            response.put("message", "Cập nhật chi tiết thành công!");
            response.put("data", result); // Trả về object cha đã được tính lại tổng tiền

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    // Hàm helper trả lỗi chung
    private ResponseEntity<Map<String, Object>> createErrorResponse(Exception e) {
        e.printStackTrace();
        Map<String, Object> response = new HashMap<>();
        response.put("errCode", 1);
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}