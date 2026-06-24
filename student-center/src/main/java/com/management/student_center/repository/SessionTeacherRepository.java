
package com.management.student_center.repository;

import com.management.student_center.entity.SessionTeacher;
import com.management.student_center.enums.AssignmentStatus;
import com.management.student_center.enums.AssignmentType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SessionTeacherRepository extends JpaRepository<SessionTeacher, Long> {

	List<SessionTeacher> findBySessionInfoId(Long sessionId);

	@Query("""
			    SELECT st
			    FROM SessionTeacher st
			    JOIN FETCH st.sessionInfo s
			    JOIN FETCH st.teacherInfo t
			    JOIN FETCH s.subject sub
			    WHERE t.id = :teacherId
			    AND s.sessionDate BETWEEN :startDate AND :endDate
			    AND s.status = 'completed'
			    AND st.assignmentStatus IN :statuses
			    AND st.payrollLocked = false
			    ORDER BY s.sessionDate ASC
			""")
	List<SessionTeacher> findPayrollSessions(@Param("teacherId") Long teacherId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
			@Param("statuses") List<AssignmentStatus> statuses);

	@Query("""
			    SELECT st
			    FROM SessionTeacher st
			    WHERE st.teacherInfo.id = :teacherId
			    AND st.payrollLocked = true
			""")
	List<SessionTeacher> findLockedPayrollSessions(@Param("teacherId") Long teacherId);

	boolean existsBySessionInfoId(Long sessionId);

	boolean existsBySessionInfoIdAndTeacherInfoIdAndAssignmentType(Long sessionId, Long teacherId,
			AssignmentType assignmentType);

	@Query("""
			    SELECT st
			    FROM SessionTeacher st
			    JOIN st.sessionInfo s
			    WHERE st.teacherInfo.id = :teacherId
			      AND s.subject.id = :subjectId
			      AND st.payrollLocked = false
			      AND st.assignmentType = 'MAIN'
			""")
	List<SessionTeacher> findByTeacherAndSubjectAndPayrollUnlocked(@Param("teacherId") Long teacherId,
			@Param("subjectId") Long subjectId);

	@Query("""
			    SELECT st
			    FROM SessionTeacher st
			    JOIN st.sessionInfo s
			    WHERE st.teacherInfo.id = :teacherId
			      AND s.subject.id = :subjectId
			      AND st.assignmentType = :assignmentType
			      AND st.payrollLocked = false
			      AND s.sessionDate >= :today
			""")
	List<SessionTeacher> findFutureSessionsForSalaryUpdate(@Param("teacherId") Long teacherId,
			@Param("subjectId") Long subjectId, @Param("assignmentType") AssignmentType assignmentType,
			@Param("today") LocalDate today);

	@Query("""
			select st
			from SessionTeacher st
			where st.teacherInfo.id = :teacherId
			and st.assignmentType =
			    com.management.student_center.enums.AssignmentType.MAIN
			and st.sessionInfo.sessionDate
			        between :startDate and :endDate
			and st.assignmentStatus <>
			    com.management.student_center.enums.AssignmentStatus.CANCELLED
			and st.payrollLocked = false
			""")
	List<SessionTeacher> findTeacherSessions(Long teacherId, LocalDate startDate, LocalDate endDate);

}