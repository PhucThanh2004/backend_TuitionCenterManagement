package com.management.student_center.dto.leave;

import java.math.BigDecimal;

public class AssignReplacementTeacherDTO {

    private Integer replacementTeacherId;

    private String adminNote;

    // lương cho buổi dạy thay
    private BigDecimal replacementSalary;

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

    public BigDecimal getReplacementSalary() {
        return replacementSalary;
    }

    public void setReplacementSalary(BigDecimal replacementSalary) {
        this.replacementSalary = replacementSalary;
    }
}