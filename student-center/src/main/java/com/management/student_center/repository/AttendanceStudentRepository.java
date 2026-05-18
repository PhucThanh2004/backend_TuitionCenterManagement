	package com.management.student_center.repository;
	
	import com.management.student_center.entity.AttendanceStudent;
	import com.management.student_center.entity.Session;
	import com.management.student_center.entity.Student;
	import org.springframework.data.jpa.repository.JpaRepository;
	
	import java.util.List;
	import java.util.Optional;
	
	import org.springframework.data.jpa.repository.Query;
	import org.springframework.data.repository.query.Param;
	import java.time.LocalDate;
	
	public interface AttendanceStudentRepository extends JpaRepository<AttendanceStudent, Long> {
	
	    Optional<AttendanceStudent> findBySessionAndStudent(Session session, Student student);
	
	    List<AttendanceStudent> findAllByStudentAndSessionIn(Student student, List<Session> sessions);
	    
	    @Query("SELECT ats FROM AttendanceStudent ats " +
	            "JOIN ats.session s " +
	            "WHERE ats.student.id = :studentId " +
	            "AND s.subject.id = :subjectId " +
	            "AND s.sessionDate BETWEEN :startDate AND :endDate " +
	            "AND s.status = 'completed' " +
	            "AND (ats.status = 'present' OR ats.status = 'late')")
	     List<AttendanceStudent> findValidAttendanceForTuition(
	             @Param("studentId") Long studentId,
	             @Param("subjectId") Long subjectId,
	             @Param("startDate") LocalDate startDate,
	             @Param("endDate") LocalDate endDate
	     );
	    
	    // Lấy tất cả học sinh vắng hoặc đi trễ trong khoảng thời gian sessionDate
	    @Query("SELECT a FROM AttendanceStudent a " +
	           "WHERE a.status IN ('absent', 'late') " +
	           "AND a.session.sessionDate BETWEEN :startDate AND :endDate")
	    List<AttendanceStudent> findAbsentOrLateBetweenDates(@Param("startDate") LocalDate startDate,
	                                                        @Param("endDate") LocalDate endDate);
	    
	    List<AttendanceStudent> findAllBySessionIn(List<Session> sessions);
	    
	    List<AttendanceStudent> findBySession(Session session);
	    
	    // Optional: Kiểm tra xem có attendance present/late không
	    boolean existsBySessionAndStatusIn(Session session, List<String> statuses);
	}
