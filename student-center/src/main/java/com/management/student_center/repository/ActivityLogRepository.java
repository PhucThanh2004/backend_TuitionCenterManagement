package com.management.student_center.repository;

import com.management.student_center.entity.ActivityLog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByOrderByCreatedAtDesc(Pageable pageable);
    
    @Query("""
    	    SELECT a
    	    FROM ActivityLog a

    	    LEFT JOIN Announcement an
    	        ON a.targetId = an.id
    	        AND a.targetType = 'ANNOUNCEMENT'

    	    WHERE

    	        a.user.id = :userId

    	        OR (

    	            a.targetType = 'ANNOUNCEMENT'

    	            AND an.status = 'active'

    	            AND a.actionType IN ('CREATE', 'UPDATE')

    	        )

    	    ORDER BY a.createdAt DESC
    	""")
    	List<ActivityLog> getTeacherActivities(
    	        @Param("userId") Long userId,
    	        Pageable pageable
    	);
    
    long countByUserIdAndIsReadFalse(Long userId);

    @Modifying
    @Query("UPDATE ActivityLog a SET a.isRead = true WHERE a.user.id = :userId AND a.isRead = false")
    void markAllAsReadByUser(@Param("userId") Long userId);

    // Lấy danh sách activity chưa đọc của user
    @Query("SELECT a FROM ActivityLog a WHERE a.user.id = :userId AND a.isRead = false ORDER BY a.createdAt DESC")
    List<ActivityLog> findUnreadByUserId(@Param("userId") Long userId, Pageable pageable);

}