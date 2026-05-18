package com.management.student_center.dto.leave;

import java.util.List;

import com.management.student_center.entity.TeacherLeave;

public class TeacherLeaveApproveDTO {

    /**
     * APPROVED / REJECTED
     */
    private TeacherLeave.LeaveStatus action;

    /**
     * CANCEL / REPLACE
     */
    private TeacherLeave.AffectType affectType;

    private String comment;
    
    private List<PreviewReplacementSelectionDTO> replacements;

    // ================= GETTER SETTER =================

    public TeacherLeave.LeaveStatus getAction() {
        return action;
    }

    public void setAction(TeacherLeave.LeaveStatus action) {
        this.action = action;
    }

    public TeacherLeave.AffectType getAffectType() {
        return affectType;
    }

    public void setAffectType(TeacherLeave.AffectType affectType) {
        this.affectType = affectType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public List<PreviewReplacementSelectionDTO> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<PreviewReplacementSelectionDTO> replacements) {
        this.replacements = replacements;
    }
}