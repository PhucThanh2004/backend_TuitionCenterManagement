package com.management.student_center.repository;

import com.management.student_center.entity.StudentTuition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentTuitionRepository extends JpaRepository<StudentTuition, Long> {
	// Tìm hóa đơn theo ghi chú để check trùng (hoặc bạn có thể query theo studentId
	// + month + year)
	List<StudentTuition> findByNotesContaining(String note);

	// Tìm hóa đơn của 1 học sinh theo tháng năm (để tránh tạo trùng)
	boolean existsByStudentIdAndMonthAndYear(Long studentId, int month, int year);

	List<StudentTuition> findByMonthAndYear(int month, int year);

	Optional<StudentTuition> findByStudentIdAndMonthAndYear(Long studentId, int month, int year);

	@Query("SELECT t FROM StudentTuition t " + "JOIN t.student s " + "LEFT JOIN s.userInfo u "
			+ "WHERE t.month = :month AND t.year = :year "
			+ "AND (:name IS NULL OR :name = '' OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :name, '%'))) "
			+ "AND (:grade IS NULL OR :grade = '' OR s.grade = :grade) "
			+ "AND (:status IS NULL OR :status = '' OR t.status = :status)")
	List<StudentTuition> searchTuitions(@Param("month") int month, @Param("year") int year, @Param("name") String name,
			@Param("grade") String grade, @Param("status") String status);

	@Query("SELECT t.month, SUM(t.paidAmount) " + "FROM StudentTuition t "
			+ "WHERE t.year = :year AND t.paidAmount IS NOT NULL " + "GROUP BY t.month")
	List<Object[]> sumTotalRevenueByYear(@Param("year") int year);

	@Query("SELECT s.name, SUM(d.totalMoney) " + "FROM StudentTuitionDetail d " + "JOIN d.studentTuition t "
			+ "JOIN d.subject s " + // Join với bảng môn học để lấy tên
			"WHERE t.year = :year " + "GROUP BY s.name")
	List<Object[]> sumRevenueBySubject(@Param("year") int year);
}