package com.management.student_center.service.tuition;

import com.management.student_center.entity.StudentTuition;
import com.management.student_center.enums.StudentTuitionStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TuitionStatusService {

	public StudentTuitionStatus calculateStatus(BigDecimal totalAmount, BigDecimal paidAmount) {

		if (totalAmount == null) {
			totalAmount = BigDecimal.ZERO;
		}

		if (paidAmount == null) {
			paidAmount = BigDecimal.ZERO;
		}

		if (paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
			return StudentTuitionStatus.WAITING_PAYMENT;
		}

		if (paidAmount.compareTo(totalAmount) >= 0) {
			return StudentTuitionStatus.PAID;
		}

		return StudentTuitionStatus.PARTIAL_PAID;
	}

	public void refreshStatus(StudentTuition tuition) {

		tuition.setStatus(calculateStatus(tuition.getTotalAmount(), tuition.getPaidAmount()));
	}
}