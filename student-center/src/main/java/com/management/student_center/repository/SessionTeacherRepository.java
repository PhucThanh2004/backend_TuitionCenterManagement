
package com.management.student_center.repository;

import com.management.student_center.entity.SessionTeacher;
import com.management.student_center.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SessionTeacherRepository
        extends JpaRepository<SessionTeacher, Long> {

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
    List<SessionTeacher> findPayrollSessions(
            @Param("teacherId") Long teacherId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") List<AssignmentStatus> statuses
    );

    @Query("""
        SELECT st
        FROM SessionTeacher st
        WHERE st.teacherInfo.id = :teacherId
        AND st.payrollLocked = true
    """)
    List<SessionTeacher> findLockedPayrollSessions(
            @Param("teacherId") Long teacherId
    );
}

