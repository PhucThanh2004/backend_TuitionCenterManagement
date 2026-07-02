package com.management.student_center.service.payroll;

import com.management.student_center.dto.payroll.PaymentDetailStatusDTO;
import com.management.student_center.dto.payroll.PayrollPaymentRequestDTO;
import com.management.student_center.dto.payroll.PayrollPaymentResponseDTO;
import com.management.student_center.entity.TeacherPayment;
import com.management.student_center.entity.TeacherPaymentDetail;
import com.management.student_center.enums.TeacherPaymentStatus;
import com.management.student_center.repository.TeacherPaymentDetailRepository;
import com.management.student_center.repository.TeacherPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PayrollPaymentService {

    private final TeacherPaymentRepository paymentRepository;
    private final TeacherPaymentDetailRepository detailRepository;

    public PayrollPaymentService(TeacherPaymentRepository paymentRepository,
                                 TeacherPaymentDetailRepository detailRepository) {
        this.paymentRepository = paymentRepository;
        this.detailRepository = detailRepository;
    }

    @Transactional
    public PayrollPaymentResponseDTO processPayment(PayrollPaymentRequestDTO request) {
        // 1. Kiểm tra bảng lương
        TeacherPayment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bảng lương"));


        if (payment.getStatus() != TeacherPaymentStatus.FINALIZED && 
            payment.getStatus() != TeacherPaymentStatus.PARTIAL_PAID) {
            throw new IllegalStateException("Chỉ có thể thanh toán khi bảng lương đã được chốt. Trạng thái hiện tại: " + payment.getStatus());
        }

        BigDecimal currentPaidAmount = payment.getPaidAmount() != null ? payment.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal totalAmount = payment.getAmount();
        BigDecimal remainingAmount = totalAmount.subtract(currentPaidAmount);

        BigDecimal amountToPay = request.getPaidAmount();
        
        List<TeacherPaymentDetail> details = detailRepository.findByPaymentInfoId(payment.getId());
        
        List<TeacherPaymentDetail> detailsToPay = new ArrayList<>();
        BigDecimal maxAllowedAmount = BigDecimal.ZERO;

        if (request.getDetailIds() != null && !request.getDetailIds().isEmpty()) {            
            detailsToPay = details.stream()
                    .filter(d -> {
                        Long detailId = d.getId();
                        return request.getDetailIds().stream()
                                .anyMatch(reqId -> reqId.longValue() == detailId.longValue());
                    })
                    .collect(Collectors.toList());
                        
            if (detailsToPay.isEmpty()) {
                throw new IllegalArgumentException("Không tìm thấy môn học nào được chọn. Detail IDs: " + request.getDetailIds());
            }
            
            maxAllowedAmount = detailsToPay.stream()
                    .map(TeacherPaymentDetail::getFinalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                        
            if (amountToPay == null || amountToPay.compareTo(BigDecimal.ZERO) <= 0) {
                amountToPay = maxAllowedAmount;
            }
            
        } 
        else if (request.getPayAllDetails() != null && request.getPayAllDetails()) {
            maxAllowedAmount = remainingAmount;
            if (amountToPay == null || amountToPay.compareTo(BigDecimal.ZERO) <= 0) {
                amountToPay = remainingAmount;
            }
        } 
        else {
            maxAllowedAmount = remainingAmount;
            if (amountToPay == null || amountToPay.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Vui lòng nhập số tiền thanh toán");
            }
        }

        if (amountToPay.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền thanh toán phải lớn hơn 0");
        }
        
        if (amountToPay.compareTo(maxAllowedAmount) > 0) {
            throw new IllegalArgumentException(
                String.format("Số tiền thanh toán không được vượt quá %s", 
                    formatCurrency(maxAllowedAmount))
            );
        }

        BigDecimal newPaidAmount = currentPaidAmount.add(amountToPay);
        if (newPaidAmount.compareTo(totalAmount) > 0) {
            throw new IllegalArgumentException(
                String.format("Tổng số tiền đã thanh toán (%s) không được vượt quá tổng lương (%s)", 
                    formatCurrency(newPaidAmount), formatCurrency(totalAmount))
            );
        }

        payment.setPaidAmount(newPaidAmount);
        
        if (newPaidAmount.compareTo(totalAmount) >= 0) {
            payment.setStatus(TeacherPaymentStatus.PAID);
        } else {
            payment.setStatus(TeacherPaymentStatus.PARTIAL_PAID);
        }
        
        payment.setPaymentDate(request.getPaymentDate() != null ? request.getPaymentDate() : LocalDate.now());
        
        String existingNote = payment.getPayrollNote();
        String newNote = String.format("Thanh toán %s vào ngày %s%s", 
            formatCurrency(amountToPay),
            LocalDate.now(),
            request.getPaymentNote() != null ? " - Ghi chú: " + request.getPaymentNote() : ""
        );
        payment.setPayrollNote(existingNote != null ? existingNote + "\n" + newNote : newNote);
        
        TeacherPayment savedPayment = paymentRepository.save(payment);


        // 7. Xây dựng response
        return buildResponse(savedPayment, details);
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.0f", amount) + "đ";
    }

    private PayrollPaymentResponseDTO buildResponse(TeacherPayment payment, List<TeacherPaymentDetail> details) {
        PayrollPaymentResponseDTO response = new PayrollPaymentResponseDTO();
        response.setPaymentId(payment.getId());
        response.setTotalAmount(payment.getAmount());
        response.setPaidAmount(payment.getPaidAmount());
        response.setRemainingAmount(payment.getAmount().subtract(payment.getPaidAmount()));
        response.setStatus(payment.getStatus().name());
        response.setPaymentDate(payment.getPaymentDate());
        response.setPaymentNote(payment.getPayrollNote());

        // Build detail statuses
        List<PaymentDetailStatusDTO> detailStatuses = new ArrayList<>();
        for (TeacherPaymentDetail detail : details) {
            PaymentDetailStatusDTO dto = new PaymentDetailStatusDTO();
            dto.setDetailId(detail.getId());
            dto.setSubjectName(detail.getSessionTeacherInfo().getSessionInfo().getSubject().getName());
            dto.setAmount(detail.getFinalAmount());
            // Tạm thời coi như chưa thanh toán chi tiết
            dto.setPaidAmount(BigDecimal.ZERO);
            dto.setRemainingAmount(detail.getFinalAmount());
            dto.setFullyPaid(false);
            detailStatuses.add(dto);
        }
        response.setDetailStatuses(detailStatuses);

        return response;
    }
}