package com.management.student_center.service;

import com.management.student_center.dto.tuition.TuitionCalculationDTO;
import com.management.student_center.dto.tuition.TuitionDetailUpdateRequest;
// Import DTO này
import com.management.student_center.entity.*;
import com.management.student_center.enums.StudentTuitionStatus;
import com.management.student_center.service.tuition.TuitionInvoiceGeneratorService;
import com.management.student_center.service.tuition.TuitionPaymentService;
import com.management.student_center.service.tuition.TuitionQueryService;
import com.management.student_center.service.tuition.TuitionUpdateService;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
@Service
public class StudentTuitionService {

    private final TuitionInvoiceGeneratorService invoiceGeneratorService;

    private final TuitionPaymentService paymentService;

    private final TuitionQueryService queryService;

    private final TuitionUpdateService updateService;

    public StudentTuitionService(
            TuitionInvoiceGeneratorService invoiceGeneratorService,
            TuitionPaymentService paymentService,
            TuitionQueryService queryService,
            TuitionUpdateService updateService
    ) {
        this.invoiceGeneratorService = invoiceGeneratorService;
        this.paymentService = paymentService;
        this.queryService = queryService;
        this.updateService = updateService;
    }

    public List<StudentTuition> createTuitions(
            int month,
            int year,
            String note
    ) {
        return invoiceGeneratorService.generate(
                month,
                year
        );
    }

    public StudentTuition payTuition(
            Long tuitionId,
            BigDecimal amount
    ) {
        return paymentService.pay(
                tuitionId,
                amount,
                "CASH",
                null
        );
    }

    public List<TuitionCalculationDTO> getTuitionsWithFilter(
            int month,
            int year,
            String name,
            String grade,
            StudentTuitionStatus status
    ) {
        return queryService.getTuitionsWithFilter(
                month,
                year,
                name,
                grade,
                status
        );
    }

    public StudentTuition getTuitionDetail(
            Long studentId,
            int month,
            int year
    ) {
        return queryService.getTuitionDetail(
                studentId,
                month,
                year
        );
    }

    public StudentTuition updateTuitionDetail(
            TuitionDetailUpdateRequest request
    ) {
        return updateService.updateTuitionDetail(
                request
        );
    }
}