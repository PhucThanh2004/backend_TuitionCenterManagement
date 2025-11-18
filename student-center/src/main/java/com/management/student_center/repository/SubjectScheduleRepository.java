package com.management.student_center.repository;

import com.management.student_center.entity.SubjectSchedule;
import com.management.student_center.entity.Room;
import com.management.student_center.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubjectScheduleRepository extends JpaRepository<SubjectSchedule, Long> {

    // Kiểm tra trùng giờ cùng phòng
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM SubjectSchedule s " +
           "WHERE s.room = :room " +
           "AND s.dayOfWeek = :dayOfWeek " +
           "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    boolean existsByRoomAndDayOfWeekAndTimeOverlap(
            @Param("room") Room room,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endTime") java.time.LocalTime endTime
    );

    // Kiểm tra trùng giờ cùng lớp
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
           "FROM SubjectSchedule s " +
           "WHERE s.subject = :subject " +
           "AND s.dayOfWeek = :dayOfWeek " +
           "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    boolean existsBySubjectAndDayOfWeekAndTimeOverlap(
            @Param("subject") Subject subject,
            @Param("dayOfWeek") Integer dayOfWeek,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endTime") java.time.LocalTime endTime
    );
}
