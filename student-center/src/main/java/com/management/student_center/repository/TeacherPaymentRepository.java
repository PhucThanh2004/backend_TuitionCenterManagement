package com.management.student_center.repository;

import com.management.student_center.entity.TeacherPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherPaymentRepository extends JpaRepository<TeacherPayment, Long> {
	// Thay thế cho [Op.like] %notes%
	List<TeacherPayment> findByNotesContaining(String notePart);

	// Tìm bảng lương cụ thể của giáo viên theo tháng (dựa vào notes)
	Optional<TeacherPayment> findByTeacherIdAndNotesContaining(Long teacherId, String notePart);

	boolean existsByTeacherIdAndMonthAndYear(Long teacherId, int month, int year);
	

    @Query("""
        SELECT COUNT(tp) 
        FROM TeacherPayment tp
        WHERE tp.teacher.id = :teacherId
          AND (tp.status = 'unpaid' OR tp.status = 'partial')
    """)
    long countUnpaidByTeacher(@Param("teacherId") Long teacherId);

	// Tìm chi tiết 1 bảng lương
	Optional<TeacherPayment> findByTeacherIdAndMonthAndYear(Long teacherId, int month, int year);

	/**
	 * 🔍 Tìm kiếm nâng cao: - Theo tháng, năm - Theo tên giáo viên (tìm gần đúng) -
	 * Theo trạng thái (paid, partial, unpaid)
	 */
	@Query("SELECT p FROM TeacherPayment p " + "WHERE p.month = :month AND p.year = :year "
			+ "AND (:name IS NULL OR lower(p.teacher.userInfo.fullName) LIKE lower(concat('%', :name, '%'))) "
			+ "AND (:status IS NULL OR :status = '' OR p.status = :status)")
	List<TeacherPayment> searchPayments(@Param("month") int month, @Param("year") int year, @Param("name") String name,
			@Param("status") String status);

	@Query("SELECT p.month, SUM(p.paidAmount) " + "FROM TeacherPayment p "
			+ "WHERE p.year = :year AND p.paidAmount IS NOT NULL " + "GROUP BY p.month")
	List<Object[]> sumTotalExpenseByYear(@Param("year") int year);

	// Lưu ý: Trong bảng bạn gửi, TeacherPaymentDetail dùng 'paymentId' và
	// 'subjectId'
	// Trong TeacherPaymentRepository.java

	@Query("SELECT s.name, SUM(d.totalMoney) " +
	       "FROM TeacherPaymentDetail d " +
	       "JOIN d.payment p " +    // <--- SỬA: Đổi 'teacherPayment' thành 'payment'
	       "JOIN d.subject s " +
	       "WHERE p.year = :year " +
	       "GROUP BY s.name")
	List<Object[]> sumExpenseBySubject(@Param("year") int year);
}