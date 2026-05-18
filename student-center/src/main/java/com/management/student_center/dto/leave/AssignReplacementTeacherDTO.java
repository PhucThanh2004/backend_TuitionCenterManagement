package com.management.student_center.dto.leave;

public class AssignReplacementTeacherDTO {

    private Integer replacementTeacherId;

    private String adminNote;

    public Integer getReplacementTeacherId() {
        return replacementTeacherId;
    }

    public void setReplacementTeacherId(Integer replacementTeacherId) {
        this.replacementTeacherId = replacementTeacherId;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }
}