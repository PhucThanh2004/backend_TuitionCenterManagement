package com.management.student_center.service.tuition;

import com.management.student_center.dto.tuition.TuitionDetailResult;
import com.management.student_center.entity.*;
import com.management.student_center.enums.BillingType;
import com.management.student_center.enums.PaymentPlanType;
import com.management.student_center.repository.AttendanceStudentRepository;
import com.management.student_center.repository.StudentTuitionDetailRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;

@Service
public class TuitionCalculationService {

	private final AttendanceStudentRepository attendanceRepository;
	private final StudentTuitionDetailRepository detailRepository;

	public TuitionCalculationService(AttendanceStudentRepository attendanceRepository,
			StudentTuitionDetailRepository detailRepository) {
		this.attendanceRepository = attendanceRepository;
		this.detailRepository = detailRepository;
	}

	public TuitionDetailResult calculate(Student student, StudentSubject ss, int month, int year) {

		BillingType billingType = ss.getBillingTypeSnapshot();

		if (billingType == BillingType.PER_HOUR) {
			return calculatePerHour(student, ss, month, year);
		}

		return calculatePerSubject(student, ss);
	}

	private TuitionDetailResult calculatePerHour(Student student, StudentSubject ss, int month, int year) {

		YearMonth ym = YearMonth.of(year, month);

		List<AttendanceStudent> attendances = attendanceRepository.findValidAttendanceForTuition(student.getId(),
				ss.getSubject().getId(), ym.atDay(1), ym.atEndOfMonth());

		double totalHours = 0;

		for (AttendanceStudent attendance : attendances) {

			Session session = attendance.getSession();
			if (session.getStartTime() == null || session.getEndTime() == null) {
				continue;
			}

			long minutes = java.time.Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();

			totalHours += (double) minutes / 60.0;
		}

		BigDecimal money = BigDecimal.valueOf(totalHours).multiply(ss.getFeeAmountSnapshot()).setScale(0,
				RoundingMode.HALF_UP);

		TuitionDetailResult result = new TuitionDetailResult();

		result.setAmount(money);
		result.setHours((float) totalHours);
		result.setSessions(attendances.size());

		return result;
	}

	private TuitionDetailResult calculatePerSubject(Student student, StudentSubject ss) {

		TuitionDetailResult result = new TuitionDetailResult();

		if (ss.getFeeAmountSnapshot() == null) {
			throw new IllegalStateException("Fee snapshot missing. StudentSubject id=" + ss.getId());
		}

		if (ss.getPaymentPlanTypeSnapshot() == PaymentPlanType.ONE_TIME) {

			long count = detailRepository.countOneTimeInvoices(student.getId(), ss.getSubject().getId());

			if (count > 0) {
				result.setSkip(true);
				return result;
			}

			result.setAmount(ss.getFeeAmountSnapshot());

			return result;
		}

		if (ss.getInstallmentCountSnapshot() == null || ss.getInstallmentCountSnapshot() <= 0) {

			throw new IllegalStateException("Installment count invalid. StudentSubject id=" + ss.getId());
		}

		long generated = detailRepository.countGeneratedInstallments(student.getId(), ss.getSubject().getId());

		int totalInstallments = ss.getInstallmentCountSnapshot();

		BigDecimal fee = ss.getFeeAmountSnapshot();

		if (generated >= totalInstallments) {
			result.setSkip(true);
			return result;
		}

		BigDecimal baseAmount = fee.divide(BigDecimal.valueOf(totalInstallments), 0, RoundingMode.DOWN);

		BigDecimal installmentAmount = baseAmount;

		if (generated == totalInstallments - 1) {

			BigDecimal paidBefore = baseAmount.multiply(BigDecimal.valueOf(totalInstallments - 1));

			installmentAmount = fee.subtract(paidBefore);
		}

		result.setAmount(installmentAmount);
		result.setInstallmentNo((int) generated + 1);

		return result;
	}
}