package com.management.student_center.repository;

import com.management.student_center.entity.TeacherLeave;
import com.management.student_center.entity.TeacherLeave.LeaveStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TeacherLeaveRepository
		extends JpaRepository<TeacherLeave, Long>, JpaSpecificationExecutor<TeacherLeave> {

	// ================= LIST =================

	Page<TeacherLeave> findByTeacherId(Long teacherId, Pageable pageable);

	Page<TeacherLeave> findByStatus(LeaveStatus status, Pageable pageable);

	Page<TeacherLeave> findByTeacherIdAndStatus(Long teacherId, LeaveStatus status, Pageable pageable);

	// ================= COUNT =================

	long countByStatus(LeaveStatus status);

	// ================= CHECK OVERLAP =================

	/**
	 * Check nghỉ trùng ngày + giờ
	 */
	@Query("""
			    SELECT COUNT(l)
			    FROM TeacherLeave l
			    WHERE l.teacher.id = :teacherId
			    AND l.status IN (
			        com.management.student_center.entity.TeacherLeave$LeaveStatus.PENDING,
			        com.management.student_center.entity.TeacherLeave$LeaveStatus.APPROVED
			    )
			    AND l.startDate <= :endDate
			    AND l.endDate >= :startDate

			    AND (
			        (
			            l.startTime IS NULL
			            OR l.endTime IS NULL
			        )
			        OR
			        (
			            :startTime < l.endTime
			            AND :endTime > l.startTime
			        )
			    )
			""")
	int countOverlappingLeave(@Param("teacherId") Long teacherId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate, @Param("startTime") LocalTime startTime,
			@Param("endTime") LocalTime endTime);

	@Query("""
			    SELECT CASE WHEN COUNT(tl) > 0 THEN true ELSE false END
			    FROM TeacherLeave tl
			    WHERE tl.teacher.id = :teacherId
			    AND tl.status IN ('PENDING', 'APPROVED')
			    AND :date BETWEEN tl.startDate AND tl.endDate
			    AND (
			        tl.startTime IS NULL
			        OR tl.endTime IS NULL
			        OR (
			            tl.startTime < :endTime
			            AND tl.endTime > :startTime
			        )
			    )
			""")
	boolean existsTeacherLeaveOverlap(@Param("teacherId") Long teacherId, @Param("date") LocalDate date,
			@Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

	@Query("""
			    SELECT tl FROM TeacherLeave tl
			    WHERE tl.teacher.id = :teacherId
			    AND :date BETWEEN tl.startDate AND tl.endDate
			""")
	List<TeacherLeave> findByTeacherIdAndDate(@Param("teacherId") Long teacherId, @Param("date") LocalDate date);

	@Query("""
			    SELECT tl
			    FROM TeacherLeave tl
			    WHERE tl.status = 'APPROVED'
			    AND tl.startDate <= :weekEnd
			    AND tl.endDate >= :weekStart
			""")
	List<TeacherLeave> findApprovedLeavesInWeek(LocalDate weekStart, LocalDate weekEnd);
}