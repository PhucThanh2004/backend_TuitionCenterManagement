package com.management.student_center.service.payroll;

import com.management.student_center.dto.payroll.PayrollPreviewRequestDTO;
import com.management.student_center.dto.payroll.PayrollPreviewResponseDTO;
import com.management.student_center.dto.payroll.PayrollSessionDetailDTO;
import com.management.student_center.entity.Session;
import com.management.student_center.entity.SessionTeacher;
import com.management.student_center.entity.Teacher;
import com.management.student_center.entity.TeacherAttendance;
import com.management.student_center.enums.AssignmentStatus;
import com.management.student_center.enums.AssignmentType;
import com.management.student_center.repository.SessionTeacherRepository;
import com.management.student_center.repository.TeacherAttendanceRepository;
import com.management.student_center.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayrollPreviewService {

	private final SessionTeacherRepository sessionTeacherRepository;

	private final TeacherRepository teacherRepository;

	private final PayrollCalculationService payrollCalculationService;

	private final TeacherAttendanceRepository teacherAttendanceRepository;

	public PayrollPreviewService(SessionTeacherRepository sessionTeacherRepository, TeacherRepository teacherRepository,
			PayrollCalculationService payrollCalculationService,
			TeacherAttendanceRepository teacherAttendanceRepository) {

		this.sessionTeacherRepository = sessionTeacherRepository;

		this.teacherRepository = teacherRepository;

		this.payrollCalculationService = payrollCalculationService;

		this.teacherAttendanceRepository = teacherAttendanceRepository;
	}

	public PayrollPreviewResponseDTO previewPayroll(PayrollPreviewRequestDTO request) {

		if (request.getMonth() == null || request.getMonth() < 1 || request.getMonth() > 12) {

			throw new RuntimeException("Invalid month");
		}

		if (request.getYear() == null) {

			throw new RuntimeException("Year is required");
		}

		if (request.getTeacherId() == null) {

			throw new RuntimeException("Teacher is required");
		}
		LocalDate startDate = LocalDate.of(request.getYear(), request.getMonth(), 1);

		LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

		Teacher teacher = teacherRepository.findById(request.getTeacherId())
				.orElseThrow(() -> new RuntimeException("Teacher not found"));

		List<AssignmentStatus> validStatuses = List.of(AssignmentStatus.COMPLETED);

		List<SessionTeacher> sessionTeachers = sessionTeacherRepository.findPayrollSessions(request.getTeacherId(),
				startDate, endDate, validStatuses);

		List<PayrollSessionDetailDTO> sessionDTOs = new ArrayList<>();

		BigDecimal totalAmount = BigDecimal.ZERO;

		for (SessionTeacher st : sessionTeachers) {

			if (!payrollCalculationService.isEligibleForPayroll(st)) {
				continue;
			}

			TeacherAttendance attendance = teacherAttendanceRepository
					.findBySessionAndTeacher(st.getSessionInfo(), st.getTeacherInfo()).orElse(null);

			if (attendance == null) {
				continue;
			}

			if (!"present".equalsIgnoreCase(attendance.getStatus())
			        && !"late".equalsIgnoreCase(attendance.getStatus())) {
			    continue;
			}

			Session session = st.getSessionInfo();

			BigDecimal amount = payrollCalculationService.calculateSessionSalary(st);

			PayrollSessionDetailDTO dto = new PayrollSessionDetailDTO();

			dto.setSessionTeacherId(st.getId());

			dto.setSessionId(session.getId());

			dto.setSubjectName(session.getSubject().getName());

			dto.setSessionDate(session.getSessionDate());

			dto.setStartTime(session.getStartTime());

			dto.setEndTime(session.getEndTime());

			dto.setAssignmentType(st.getAssignmentType());

			dto.setSalaryType(st.getSalaryType());

			dto.setSalaryRate(st.getSalaryRate());

			BigDecimal workedHours = payrollCalculationService.calculateWorkedHours(st);

			dto.setWorkedHours(workedHours);

			dto.setAmount(amount);

			dto.setReplacement(st.getAssignmentType() == AssignmentType.REPLACEMENT);

			dto.setNote(st.getNote());

			totalAmount = totalAmount.add(amount);

			sessionDTOs.add(dto);
		}

		PayrollPreviewResponseDTO response = new PayrollPreviewResponseDTO();

		response.setTeacherId(teacher.getId());

		response.setTeacherName(teacher.getUserInfo().getFullName());

		response.setMonth(request.getMonth());

		response.setYear(request.getYear());

		response.setTotalSessions(sessionDTOs.size());

		response.setTotalAmount(totalAmount);

		response.setSessions(sessionDTOs);

		return response;
	}
}