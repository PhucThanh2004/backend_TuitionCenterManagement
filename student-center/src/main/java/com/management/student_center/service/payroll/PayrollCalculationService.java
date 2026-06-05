package com.management.student_center.service.payroll;

import com.management.student_center.entity.SessionTeacher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

@Service
public class PayrollCalculationService {

	public BigDecimal calculateSessionSalary(SessionTeacher sessionTeacher) {

		if (sessionTeacher == null) {
			return BigDecimal.ZERO;
		}

		if (sessionTeacher.getSalaryType() == null) {
			return BigDecimal.ZERO;
		}

		return switch (sessionTeacher.getSalaryType()) {

		case PER_HOUR -> calculatePerHourSalary(sessionTeacher);

		case PER_SESSION -> calculatePerSessionSalary(sessionTeacher);

		default -> BigDecimal.ZERO;
		};
	}

	public BigDecimal calculateWorkedHours(SessionTeacher sessionTeacher) {

		if (sessionTeacher.getWorkedHours() != null && sessionTeacher.getWorkedHours().compareTo(BigDecimal.ZERO) > 0) {

			return sessionTeacher.getWorkedHours();
		}

		if (sessionTeacher.getSessionInfo() == null) {
			return BigDecimal.ZERO;
		}

		if (sessionTeacher.getSessionInfo().getStartTime() == null
				|| sessionTeacher.getSessionInfo().getEndTime() == null) {

			return BigDecimal.ZERO;
		}

		Duration duration = Duration.between(sessionTeacher.getSessionInfo().getStartTime(),
				sessionTeacher.getSessionInfo().getEndTime());

		return BigDecimal.valueOf(duration.toMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
	}

	private BigDecimal calculatePerHourSalary(SessionTeacher sessionTeacher) {

		BigDecimal workedHours = calculateWorkedHours(sessionTeacher);

		BigDecimal salaryRate = sessionTeacher.getSalaryRate() == null ? BigDecimal.ZERO
				: sessionTeacher.getSalaryRate();

		return workedHours.multiply(salaryRate).setScale(2, RoundingMode.HALF_UP);
	}

	private BigDecimal calculatePerSessionSalary(SessionTeacher sessionTeacher) {

		return sessionTeacher.getSalaryRate() == null ? BigDecimal.ZERO : sessionTeacher.getSalaryRate();
	}

	public boolean isEligibleForPayroll(SessionTeacher sessionTeacher) {

		if (sessionTeacher == null) {
			return false;
		}

		if (sessionTeacher.getSessionInfo() == null) {
			return false;
		}

		if (!"completed".equalsIgnoreCase(sessionTeacher.getSessionInfo().getStatus())) {
			return false;
		}

		if (sessionTeacher.getAssignmentStatus() == null) {
			return false;
		}

		return switch (sessionTeacher.getAssignmentStatus()) {

		case COMPLETED -> true;

		default -> false;
		};
	}
}