package com.management.student_center.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcement_views")
public class AnnouncementView {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "announcement_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_view_announcement"))
    private Announcement announcement;
    
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_view_user"))
    private User user; // có thể null nếu người dùng chưa đăng nhập
    
    @Column(nullable = false)
    private String ipAddress;
    
    @Column(nullable = false)
    private LocalDateTime viewedAt;
    
    @Column(length = 50)
    private String userAgent;
    
    @PrePersist
    protected void onCreate() {
        viewedAt = LocalDateTime.now();
    }
    
    // Constructors
    public AnnouncementView() {}
    
    public AnnouncementView(Announcement announcement, User user, String ipAddress, String userAgent) {
        this.announcement = announcement;
        this.user = user;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Announcement getAnnouncement() { return announcement; }
    public void setAnnouncement(Announcement announcement) { this.announcement = announcement; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public LocalDateTime getViewedAt() { return viewedAt; }
    public void setViewedAt(LocalDateTime viewedAt) { this.viewedAt = viewedAt; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}