package com.management.student_center.repository;

import com.management.student_center.dto.RoomScheduleDTO;
import com.management.student_center.dto.UpcomingSessionDTO;
import com.management.student_center.entity.Room;
import com.management.student_center.entity.Session;
import com.management.student_center.entity.Subject;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
	
	Optional<Session> findById (Long sessionId);
	boolean existsByPlannedSessionDetailId(Long sessionDetailId);

	List<Session> findBySubject_IdOrderBySessionDateAsc(Long subjectId);

	Optional<Session> findBySubjectIdAndSessionDate(Long subjectId, LocalDate sessionDate);

	@Query("SELECT s FROM Session s LEFT JOIN FETCH s.room r LEFT JOIN FETCH s.schedule sch "
			+ "WHERE s.subject.id = :subjectId " + "ORDER BY s.sessionDate ASC, s.startTime ASC")
	List<Session> findBySubjectIdWithRoomAndScheduleOrder(@Param("subjectId") Long subjectId);

	@Query("SELECT s FROM Session s LEFT JOIN FETCH s.room r LEFT JOIN FETCH s.schedule sch WHERE s.id = :id")
	Session findByIdWithRoomAndSchedule(@Param("id") Long id);

	// --- Method check session tồn tại ---
	@Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " + "FROM Session s " + "WHERE s.subject = :subject "
			+ "AND s.sessionDate = :sessionDate " + "AND s.startTime = :startTime")
	boolean existsBySubjectAndSessionDateAndStartTime(@Param("subject") Subject subject,
			@Param("sessionDate") LocalDate sessionDate, @Param("startTime") LocalTime startTime);

	// Kiểm tra trùng giờ phòng
	@Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " + "FROM Session s " + "WHERE s.room = :room "
			+ "AND s.sessionDate = :date " + "AND (" + "  (s.startTime <= :start AND s.endTime > :start) OR "
			+ "  (s.startTime < :end AND s.endTime >= :end) OR " + "  (s.startTime >= :start AND s.endTime <= :end)"
			+ ")")
	boolean existsByRoomAndDateAndTimeOverlap(@Param("room") Room room, @Param("date") LocalDate date,
			@Param("start") LocalTime start, @Param("end") LocalTime end);

	// Kiểm tra trùng giờ lớp
	@Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " + "FROM Session s " + "WHERE s.subject = :subject "
			+ "AND s.sessionDate = :date " + "AND (" + "  (s.startTime <= :start AND s.endTime > :start) OR "
			+ "  (s.startTime < :end AND s.endTime >= :end) OR " + "  (s.startTime >= :start AND s.endTime <= :end)"
			+ ")")
	boolean existsBySubjectAndDateAndTimeOverlap(@Param("subject") Subject subject, @Param("date") LocalDate date,
			@Param("start") LocalTime start, @Param("end") LocalTime end);

	// Kiểm tra trùng giờ phòng, trừ chính session
	@Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " + "FROM Session s " + "WHERE s.room = :room "
			+ "AND s.sessionDate = :date " + "AND s.id <> :excludeId " + "AND ("
			+ "  (s.startTime <= :start AND s.endTime > :start) OR "
			+ "  (s.startTime < :end AND s.endTime >= :end) OR " + "  (s.startTime >= :start AND s.endTime <= :end)"
			+ ")")
	boolean existsByRoomAndDateAndTimeOverlapExcludingId(@Param("room") Room room, @Param("date") LocalDate date,
			@Param("start") LocalTime start, @Param("end") LocalTime end, @Param("excludeId") Long excludeId);

	// Kiểm tra trùng giờ lớp, trừ chính session
	@Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " + "FROM Session s " + "WHERE s.subject = :subject "
			+ "AND s.sessionDate = :date " + "AND s.id <> :excludeId " + "AND ("
			+ "  (s.startTime <= :start AND s.endTime > :start) OR "
			+ "  (s.startTime < :end AND s.endTime >= :end) OR " + "  (s.startTime >= :start AND s.endTime <= :end)"
			+ ")")
	boolean existsBySubjectAndDateAndTimeOverlapExcludingId(@Param("subject") Subject subject,
			@Param("date") LocalDate date, @Param("start") LocalTime start, @Param("end") LocalTime end,
			@Param("excludeId") Long excludeId);

	// Query thay thế cho đoạn logic phức tạp trong Node.js:
	// Session status='completed', Date between range, TeacherAttendance
	// status='present'
	@Query("SELECT s FROM Session s " + "JOIN s.teacherAttendances ta " + "WHERE s.subject.id = :subjectId "
			+ "AND s.status = 'completed' " + "AND s.sessionDate BETWEEN :startDate AND :endDate "
			+ "AND ta.teacher.id = :teacherId " + "AND ta.status = 'present'")
	List<Session> findValidSessionsForSalary(@Param("teacherId") Long teacherId, @Param("subjectId") Long subjectId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	// lấy các Session của phòng trong khoảng ngày
	@Query("SELECT new com.management.student_center.dto.RoomScheduleDTO(" + "r.id, r.name, r.seatCapacity, "
			+ "ss.id, s.name, ss.dayOfWeek, ss.startTime, ss.endTime, "
			+ "ses.id, ses.sessionDate, ses.startTime, ses.endTime, ses.status) " + "FROM Session ses "
			+ "JOIN ses.room r " + "JOIN ses.schedule ss " + "JOIN ses.subject s " + "WHERE r.id = :roomId "
			+ "AND ses.sessionDate BETWEEN :startDate AND :endDate " + "ORDER BY ses.sessionDate, ses.startTime")
	List<RoomScheduleDTO> findRoomSchedule(@Param("roomId") Long roomId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	@Query("""
			    SELECT s FROM Session s
			    WHERE s.subject.id IN (
			        SELECT ts.subject.id FROM TeacherSubject ts
			        WHERE ts.teacher.id = :teacherId
			    )
			    AND s.sessionDate BETWEEN :startDate AND :endDate
			    ORDER BY s.sessionDate ASC, s.startTime ASC
			""")
	List<Session> findTeacherSessionsInRange(@Param("teacherId") Long teacherId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	// Lấy tất cả các môn học trong 1 ngày
	@Query("""
			    SELECT DISTINCT s
			    FROM Session s
			    JOIN FETCH s.subject sub
			    LEFT JOIN FETCH sub.teacherSubjects ts
			    LEFT JOIN FETCH ts.teacher t
			    LEFT JOIN FETCH t.userInfo u
			    LEFT JOIN FETCH s.room r
			    WHERE s.sessionDate = :date

			""")
	List<Session> findSessionsByDate(@Param("date") LocalDate date);

	// Lấy buổi học gần nhất so với thời điểm hiện t
	@Query("""
			    SELECT new com.management.student_center.dto.UpcomingSessionDTO(
			        s.name,
			        s.grade,
			        u.fullName,
			        se.sessionDate,
			        se.startTime,
			        se.endTime
			    )
			    FROM Session se
			    JOIN se.subject s
			    LEFT JOIN s.teacherSubjects ts
			    LEFT JOIN ts.teacher t
			    LEFT JOIN t.userInfo u
			    WHERE
			        (
			            se.sessionDate > CURRENT_DATE
			            OR (se.sessionDate = CURRENT_DATE AND se.startTime > CURRENT_TIME)
			        )
			        AND se.status = 'scheduled'
			    ORDER BY se.sessionDate ASC, se.startTime ASC
			""")
	List<UpcomingSessionDTO> findUpcomingSessions(Pageable pageable);

	// Đếm số lượng session đang hoạt động (chưa kết thúc)
	@Query("SELECT COUNT(s) FROM Session s " + "WHERE s.room.id = :roomId " + "AND s.sessionDate = CURRENT_DATE "
			+ "AND s.startTime <= CURRENT_TIME " + "AND s.endTime > CURRENT_TIME " + "AND s.status = 'scheduled'")
	Long countActiveSessionsNow(@Param("roomId") Long roomId);

	// Kiểm tra xem phòng có session trong tương lai không
	@Query("SELECT COUNT(s) > 0 FROM Session s " + "WHERE s.room.id = :roomId " + "AND s.sessionDate >= CURRENT_DATE "
			+ "AND s.status = 'scheduled'")
	boolean hasFutureSessions(@Param("roomId") Long roomId);

	@Query("""
			    SELECT s FROM Session s
			    WHERE s.room.id = :roomId
			    AND s.sessionDate = CURRENT_DATE
			    AND CURRENT_TIME BETWEEN s.startTime AND s.endTime
			    AND s.status = 'scheduled'
			""")
	Session findActiveSessionByRoomId(@Param("roomId") Long roomId);

	@Query("SELECT s FROM Session s WHERE s.subject.id IN :subjectIds AND s.sessionDate BETWEEN :startDate AND :endDate")
	List<Session> findSessionsForLeave(@Param("subjectIds") List<Integer> subjectIds,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query("""
			    SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
			    FROM Session s
			    JOIN s.subject sub
			    JOIN sub.teacherSubjects ts
			    WHERE ts.teacher.id = :teacherId
			    AND s.sessionDate = :date
			    AND s.status = 'scheduled'
			    AND (
			        s.startTime < :endTime
			        AND s.endTime > :startTime
			    )
			""")
	boolean existsTeacherSessionOverlap(@Param("teacherId") Long teacherId, @Param("date") LocalDate date,
			@Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);
	List<Session> findBySubjectId(Long subjectId);
	
	 /**
     * Lấy lịch dạy của giáo viên trong khoảng thời gian
     * @param teacherId ID của giáo viên
     * @param startDate Ngày bắt đầu
     * @param endDate Ngày kết thúc
     * @return Danh sách các buổi học
     */
    @Query("SELECT s FROM Session s WHERE s.subject IN " +
           "(SELECT ts.subject FROM TeacherSubject ts WHERE ts.teacher.id = :teacherId) " +
           "AND s.sessionDate BETWEEN :startDate AND :endDate")
    List<Session> findTeacherSessionsInDateRange(@Param("teacherId") Long teacherId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

}