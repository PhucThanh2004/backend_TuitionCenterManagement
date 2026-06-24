package com.management.student_center.service.tuition;

import com.management.student_center.dto.tuition.TuitionDetailResult;
import com.management.student_center.entity.*;
import com.management.student_center.enums.StudentTuitionStatus;
import com.management.student_center.repository.StudentRepository;
import com.management.student_center.repository.StudentTuitionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class TuitionInvoiceGeneratorService {

	private final StudentRepository studentRepository;
	private final StudentTuitionRepository tuitionRepository;
	private final TuitionCalculationService calculationService;

	public TuitionInvoiceGeneratorService(StudentRepository studentRepository,
			StudentTuitionRepository tuitionRepository, TuitionCalculationService calculationService) {
		this.studentRepository = studentRepository;
		this.tuitionRepository = tuitionRepository;
		this.calculationService = calculationService;
	}

	@Transactional
	public List<StudentTuition> generate(int month, int year) {

		List<StudentTuition> result = new ArrayList<>();

		List<Student> students = studentRepository.findAll();

		for (Student student : students) {
			StudentTuition existing = tuitionRepository.findByStudentIdAndMonthAndYear(student.getId(), month, year)
					.orElse(null);

			if (existing != null) {
				continue;
			}

			StudentTuition tuition = new StudentTuition();

			tuition.setStudent(student);
			tuition.setMonth(month);
			tuition.setYear(year);
			tuition.setStatus(StudentTuitionStatus.WAITING_PAYMENT);

			List<StudentTuitionDetail> details = new ArrayList<>();

			BigDecimal total = BigDecimal.ZERO;

			for (StudentSubject ss : student.getStudentSubjects()) {

			    if (ss.getDeletedAt() != null) {
			        continue;
			    }

				TuitionDetailResult calc = calculationService.calculate(student, ss, month, year);

				if (calc.isSkip()) {
					continue;
				}

				if (calc.getAmount() == null || calc.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				StudentTuitionDetail detail = new StudentTuitionDetail();

				detail.setStudentTuition(tuition);

				detail.setSubject(ss.getSubject());

				detail.setBillingType(ss.getBillingTypeSnapshot());

				detail.setPaymentPlanType(ss.getPaymentPlanTypeSnapshot());

				detail.setInstallmentNo(calc.getInstallmentNo());

				detail.setTotalInstallments(ss.getInstallmentCountSnapshot());

				detail.setAttendedSessions(calc.getSessions() == null ? 0 : calc.getSessions());

				detail.setTotalHours(calc.getHours() == null ? 0 : calc.getHours());

				detail.setTotalMoney(calc.getAmount());

				details.add(detail);

				total = total.add(calc.getAmount());
			}

			if (details.isEmpty()) {
				continue;
			}

			tuition.setDetails(details);
			tuition.setTotalAmount(total);
			tuition.setPaidAmount(BigDecimal.ZERO);

			result.add(tuitionRepository.save(tuition));
		}

		return result;
	}
}