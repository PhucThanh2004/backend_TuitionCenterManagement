package com.management.student_center.service.tuition;

import com.management.student_center.dto.tuition.TuitionDetailUpdateRequest;
import com.management.student_center.entity.StudentTuition;
import com.management.student_center.entity.StudentTuitionDetail;
import com.management.student_center.enums.StudentTuitionStatus;
import com.management.student_center.repository.StudentTuitionDetailRepository;
import com.management.student_center.repository.StudentTuitionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TuitionUpdateService {

	private final StudentTuitionDetailRepository detailRepository;

	private final StudentTuitionRepository tuitionRepository;

	private final TuitionStatusService tuitionStatusService;

	public TuitionUpdateService(StudentTuitionDetailRepository detailRepository,
			StudentTuitionRepository tuitionRepository, TuitionStatusService tuitionStatusService) {
		this.detailRepository = detailRepository;
		this.tuitionRepository = tuitionRepository;
		this.tuitionStatusService = tuitionStatusService;
	}

	@Transactional
	public StudentTuition updateTuitionDetail(TuitionDetailUpdateRequest request) {

		StudentTuitionDetail detail = detailRepository.findById(request.getDetailId())
				.orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết học phí"));

		if (request.getAttendedSessions() != null) {

			int oldSessions = detail.getAttendedSessions();

			float oldHours = detail.getTotalHours();

			int newSessions = request.getAttendedSessions();

			float hoursPerSession = 0f;

			if (oldSessions > 0) {
				hoursPerSession = oldHours / oldSessions;
			}

			float newHours = hoursPerSession * newSessions;

			detail.setAttendedSessions(newSessions);

			detail.setTotalHours(newHours);

			BigDecimal unitPrice = detail.getUnitPrice() != null ? detail.getUnitPrice() : BigDecimal.ZERO;

			BigDecimal amount = BigDecimal.valueOf(newHours).multiply(unitPrice).setScale(0, RoundingMode.HALF_UP);

			detail.setTotalMoney(amount);
		}

		if (request.getTotalMoney() != null) {

			detail.setTotalMoney(request.getTotalMoney());
		}

		if (request.getNote() != null) {

			detail.setNote(request.getNote());
		}

		detailRepository.save(detail);

		StudentTuition tuition = detail.getStudentTuition();

		BigDecimal total = tuition.getDetails().stream().map(StudentTuitionDetail::getTotalMoney)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		tuition.setTotalAmount(total);

		StudentTuitionStatus status = tuitionStatusService.calculateStatus(total, tuition.getPaidAmount());

		tuition.setStatus(status);

		return tuitionRepository.save(tuition);
	}
}