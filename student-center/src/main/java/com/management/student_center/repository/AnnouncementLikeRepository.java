package com.management.student_center.repository;

import com.management.student_center.entity.Announcement;
import com.management.student_center.entity.AnnouncementLike;
import com.management.student_center.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface AnnouncementLikeRepository extends JpaRepository<AnnouncementLike, Long> {
    
    Optional<AnnouncementLike> findByAnnouncementAndUser(Announcement announcement, User user);
    
    @Query("SELECT COUNT(l) FROM AnnouncementLike l WHERE l.announcement.id = :announcementId")
    long countByAnnouncementId(@Param("announcementId") Long announcementId);
    
    @Modifying
    @Transactional
    void deleteByAnnouncementAndUser(Announcement announcement, User user);
    
    boolean existsByAnnouncementAndUser(Announcement announcement, User user);
    
    // Lấy danh sách người dùng đã like một announcement
    Page<AnnouncementLike> findByAnnouncementId(Long announcementId, Pageable pageable);
    
    // Xóa tất cả likes của một announcement (khi xóa bài viết)
    @Modifying
    @Transactional
    void deleteByAnnouncement(Announcement announcement);
}