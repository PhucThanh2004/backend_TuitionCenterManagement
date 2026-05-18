package com.management.student_center.repository;

import com.management.student_center.entity.Announcement;
import com.management.student_center.entity.AnnouncementView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

public interface AnnouncementViewRepository extends JpaRepository<AnnouncementView, Long> {
    
    @Query("SELECT COUNT(v) FROM AnnouncementView v WHERE v.announcement.id = :announcementId")
    long countByAnnouncementId(@Param("announcementId") Long announcementId);
    
    @Query("SELECT COUNT(DISTINCT v.ipAddress) FROM AnnouncementView v WHERE v.announcement.id = :announcementId")
    long countUniqueViewsByAnnouncementId(@Param("announcementId") Long announcementId);
    
    // Kiểm tra xem IP đã xem trong khoảng thời gian gần đây chưa (tránh spam view)
    Optional<AnnouncementView> findByAnnouncementAndIpAddressAndViewedAtAfter(
        Announcement announcement, String ipAddress, LocalDateTime since);
    
    // Thống kê view theo ngày
    @Query("SELECT DATE(v.viewedAt), COUNT(v) FROM AnnouncementView v " +
           "WHERE v.announcement.id = :announcementId " +
           "GROUP BY DATE(v.viewedAt) " +
           "ORDER BY DATE(v.viewedAt) DESC")
    java.util.List<Object[]> countViewsByDay(@Param("announcementId") Long announcementId);
    
    // Xóa tất cả views của một announcement
    @Modifying
    @Transactional
    void deleteByAnnouncement(Announcement announcement);
}