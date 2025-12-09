package com.management.student_center.repository;

import com.management.student_center.dto.RoomScheduleDTO;
import com.management.student_center.entity.Room;
import com.management.student_center.entity.Session;
import com.management.student_center.entity.Subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
	
	 List<Session> findBySubject_IdOrderBySessionDateAsc(Long subjectId);

    @Query("SELECT s FROM Session s LEFT JOIN FETCH s.room r LEFT JOIN FETCH s.schedule sch " +
           "WHERE s.subject.id = :subjectId " +
           "ORDER BY s.sessionDate ASC, s.startTime ASC")
    List<Session> findBySubjectIdWithRoomAndScheduleOrder(@Param("subjectId") Long subjectId);

    @Query("SELECT s FROM Session s LEFT JOIN FETCH s.room r LEFT JOIN FETCH s.schedule sch WHERE s.id = :id")
    Session findByIdWithRoomAndSchedule(@Param("id") Long id);
    
 // --- Method check session tồn tại ---
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM Session s " +
           "WHERE s.subject = :subject " +
           "AND s.sessionDate = :sessionDate " +
           "AND s.startTime = :startTime")
    boolean existsBySubjectAndSessionDateAndStartTime(
            @Param("subject") Subject subject,
            @Param("sessionDate") LocalDate sessionDate,
            @Param("startTime") LocalTime startTime
    );
    
 // Kiểm tra trùng giờ phòng
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM Session s " +
           "WHERE s.room = :room " +
           "AND s.sessionDate = :date " +
           "AND (" +
           "  (s.startTime <= :start AND s.endTime > :start) OR " +
           "  (s.startTime < :end AND s.endTime >= :end) OR " +
           "  (s.startTime >= :start AND s.endTime <= :end)" +
           ")")
    boolean existsByRoomAndDateAndTimeOverlap(
            @Param("room") Room room,
            @Param("date") LocalDate date,
            @Param("start") LocalTime start,
            @Param("end") LocalTime end
    );

    // Kiểm tra trùng giờ lớp
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM Session s " +
           "WHERE s.subject = :subject " +
           "AND s.sessionDate = :date " +
           "AND (" +
           "  (s.startTime <= :start AND s.endTime > :start) OR " +
           "  (s.startTime < :end AND s.endTime >= :end) OR " +
           "  (s.startTime >= :start AND s.endTime <= :end)" +
           ")")
    boolean existsBySubjectAndDateAndTimeOverlap(
            @Param("subject") Subject subject,
            @Param("date") LocalDate date,
            @Param("start") LocalTime start,
            @Param("end") LocalTime end
    );

    // Kiểm tra trùng giờ phòng, trừ chính session
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM Session s " +
           "WHERE s.room = :room " +
           "AND s.sessionDate = :date " +
           "AND s.id <> :excludeId " +
           "AND (" +
           "  (s.startTime <= :start AND s.endTime > :start) OR " +
           "  (s.startTime < :end AND s.endTime >= :end) OR " +
           "  (s.startTime >= :start AND s.endTime <= :end)" +
           ")")
    boolean existsByRoomAndDateAndTimeOverlapExcludingId(
            @Param("room") Room room,
            @Param("date") LocalDate date,
            @Param("start") LocalTime start,
            @Param("end") LocalTime end,
            @Param("excludeId") Long excludeId
    );

    // Kiểm tra trùng giờ lớp, trừ chính session
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM Session s " +
           "WHERE s.subject = :subject " +
           "AND s.sessionDate = :date " +
           "AND s.id <> :excludeId " +
           "AND (" +
           "  (s.startTime <= :start AND s.endTime > :start) OR " +
           "  (s.startTime < :end AND s.endTime >= :end) OR " +
           "  (s.startTime >= :start AND s.endTime <= :end)" +
           ")")
    boolean existsBySubjectAndDateAndTimeOverlapExcludingId(
            @Param("subject") Subject subject,
            @Param("date") LocalDate date,
            @Param("start") LocalTime start,
            @Param("end") LocalTime end,
            @Param("excludeId") Long excludeId
    );

    
 // Query thay thế cho đoạn logic phức tạp trong Node.js:
    // Session status='completed', Date between range, TeacherAttendance status='present'
    @Query("SELECT s FROM Session s " +
           "JOIN s.teacherAttendances ta " +
           "WHERE s.subject.id = :subjectId " +
           "AND s.status = 'completed' " +
           "AND s.sessionDate BETWEEN :startDate AND :endDate " +
           "AND ta.teacher.id = :teacherId " +
           "AND ta.status = 'present'")
    List<Session> findValidSessionsForSalary(
            @Param("teacherId") Long teacherId,
            @Param("subjectId") Long subjectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
 // lấy các Session của phòng trong khoảng ngày
    @Query("SELECT new com.management.student_center.dto.RoomScheduleDTO(" +
            "r.id, r.name, r.seatCapacity, " +
            "ss.id, s.name, ss.dayOfWeek, ss.startTime, ss.endTime, " +
            "ses.id, ses.sessionDate, ses.startTime, ses.endTime, ses.status) " +
            "FROM Session ses " +
            "JOIN ses.room r " +
            "JOIN ses.schedule ss " +
            "JOIN ses.subject s " +
            "WHERE r.id = :roomId " +
            "AND ses.sessionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY ses.sessionDate, ses.startTime")
    List<RoomScheduleDTO> findRoomSchedule(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    @Query("""
            SELECT s FROM Session s
            WHERE s.subject.id IN (
                SELECT ts.subject.id FROM TeacherSubject ts
                WHERE ts.teacher.id = :teacherId
            )
            AND s.sessionDate BETWEEN :startDate AND :endDate
            ORDER BY s.sessionDate ASC, s.startTime ASC
        """)
        List<Session> findTeacherSessionsInRange(
                @Param("teacherId") Long teacherId,
                @Param("startDate") LocalDate startDate,
                @Param("endDate") LocalDate endDate
        );
}
