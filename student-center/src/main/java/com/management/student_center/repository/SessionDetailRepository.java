package com.management.student_center.repository;

import com.management.student_center.entity.SessionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SessionDetailRepository extends JpaRepository<SessionDetail, Long> {
    
    // Lấy danh sách theo display_order (thứ tự hiển thị)
    List<SessionDetail> findByCurriculumIdOrderByDisplayOrderAsc(Long curriculumId);
    
    // Lấy danh sách theo session_number (dùng cho trường công hoặc copy)
    List<SessionDetail> findByCurriculumIdOrderBySessionNumberAsc(Long curriculumId);
    
    // Lấy danh sách đơn giản
    List<SessionDetail> findByCurriculumId(Long curriculumId);
    
    // Kiểm tra session_number đã tồn tại
    boolean existsByCurriculumIdAndSessionNumber(Long curriculumId, Integer sessionNumber);
    
    // Đếm số lượng theo curriculum
    long countByCurriculumId(Long curriculumId);
    
    // Lấy max display_order
    @Query("SELECT MAX(s.displayOrder) FROM SessionDetail s WHERE s.curriculum.id = :curriculumId")
    Integer findMaxDisplayOrderByCurriculumId(@Param("curriculumId") Long curriculumId);
    
    // Đếm số lượng đã được sử dụng trong session thực tế
    @Query("SELECT COUNT(sd) FROM SessionDetail sd WHERE sd.curriculum.id = :curriculumId AND sd.id IN (SELECT sess.plannedSessionDetail.id FROM Session sess)")
    long countByCurriculumIdAndIsUsed(@Param("curriculumId") Long curriculumId);
}