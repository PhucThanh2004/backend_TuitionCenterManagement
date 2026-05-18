// dto/AnnouncementResponse.java
package com.management.student_center.dto;

import com.management.student_center.entity.Announcement;
import java.util.List;

public class AnnouncementResponse {
    private Announcement announcement;
    private long likeCount;
    private long viewCount;
    private boolean isLikedByCurrentUser;
    
    // Constructors
    public AnnouncementResponse(Announcement announcement, long likeCount, long viewCount, boolean isLikedByCurrentUser) {
        this.announcement = announcement;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.isLikedByCurrentUser = isLikedByCurrentUser;
    }
    
    // Getters and Setters
    public Announcement getAnnouncement() { return announcement; }
    public void setAnnouncement(Announcement announcement) { this.announcement = announcement; }
    public long getLikeCount() { return likeCount; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }
    public long getViewCount() { return viewCount; }
    public void setViewCount(long viewCount) { this.viewCount = viewCount; }
    public boolean isLikedByCurrentUser() { return isLikedByCurrentUser; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { isLikedByCurrentUser = likedByCurrentUser; }
}