package com.management.student_center.dto.payroll;

public class TeacherPayrollConfirmDTO {

    private Integer paymentId;

    private String teacherFeedback;

    public TeacherPayrollConfirmDTO() {
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public String getTeacherFeedback() {
        return teacherFeedback;
    }

    public void setTeacherFeedback(String teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
    }
}