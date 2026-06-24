package com.management.student_center.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.management.student_center.entity.Session;
import com.management.student_center.entity.SessionTeacher;
import com.management.student_center.entity.Subject;
import com.management.student_center.entity.Teacher;
import com.management.student_center.entity.TeacherSubject;
import com.management.student_center.enums.AssignmentStatus;
import com.management.student_center.enums.AssignmentType;
import com.management.student_center.enums.SalaryType;
import com.management.student_center.repository.SessionRepository;
import com.management.student_center.repository.SessionTeacherRepository;
import com.management.student_center.repository.TeacherSubjectRepository;

@Service
public class SessionTeacherSyncService {
	private final SessionTeacherRepository sessionTeacherRepository;
	private final TeacherSubjectRepository teacherSubjectRepository;
	private final SessionRepository sessionRepository;

	public SessionTeacherSyncService(SessionTeacherRepository sessionTeacherRepository,
			TeacherSubjectRepository teacherSubjectRepository, SessionRepository sessionRepository) {

		this.sessionTeacherRepository = sessionTeacherRepository;
		this.teacherSubjectRepository = teacherSubjectRepository;
		this.sessionRepository = sessionRepository;
	}

	@Transactional
	public void createMainTeacherAssignments(Session session) {

		if (session.getSubject() == null) {
			return;
		}

		List<TeacherSubject> teacherSubjects = teacherSubjectRepository
				.findAllBySubjectId(session.getSubject().getId());

		if (teacherSubjects.isEmpty()) {
			return;
		}

		List<SessionTeacher> assignments = new ArrayList<>();

		for (TeacherSubject ts : teacherSubjects) {

			boolean exists = sessionTeacherRepository.existsBySessionInfoIdAndTeacherInfoIdAndAssignmentType(
					session.getId(), ts.getTeacher().getId(), AssignmentType.MAIN);

			if (exists) {
				continue;
			}

			SessionTeacher st = new SessionTeacher();

			st.setSessionInfo(session);
			st.setTeacherInfo(ts.getTeacher());

			st.setAssignmentType(AssignmentType.MAIN);
			st.setAssignmentStatus(AssignmentStatus.ASSIGNED);

			st.setSalaryType(SalaryType.PER_HOUR);
			st.setSalaryRate(ts.getSalaryRate());

			assignments.add(st);
		}

		if (!assignments.isEmpty()) {
			sessionTeacherRepository.saveAll(assignments);
		}
	}

	@Transactional
	public void syncTeacherToExistingSessions(Teacher teacher, Subject subject, BigDecimal salaryRate) {

		List<Session> sessions = sessionRepository.findBySubject_IdOrderBySessionDateAsc(subject.getId());

		List<SessionTeacher> assignments = new ArrayList<>();

		for (Session session : sessions) {

			boolean exists = sessionTeacherRepository.existsBySessionInfoIdAndTeacherInfoIdAndAssignmentType(
					session.getId(), teacher.getId(), AssignmentType.MAIN);

			if (exists) {
				continue;
			}

			SessionTeacher st = new SessionTeacher();

			st.setSessionInfo(session);
			st.setTeacherInfo(teacher);

			st.setAssignmentType(AssignmentType.MAIN);
			st.setAssignmentStatus(AssignmentStatus.ASSIGNED);

			st.setSalaryType(SalaryType.PER_HOUR);
			st.setSalaryRate(salaryRate);

			assignments.add(st);
		}

		if (!assignments.isEmpty()) {
			sessionTeacherRepository.saveAll(assignments);
		}
	}

	@Transactional
	public void updateSalaryRateForFutureSessions(Teacher teacher, Subject subject, BigDecimal salaryRate) {

		List<SessionTeacher> assignments = sessionTeacherRepository.findFutureSessionsForSalaryUpdate(teacher.getId(),
				subject.getId(), AssignmentType.MAIN, LocalDate.now());

		for (SessionTeacher st : assignments) {
			st.setSalaryRate(salaryRate);
		}

		sessionTeacherRepository.saveAll(assignments);
	}
}