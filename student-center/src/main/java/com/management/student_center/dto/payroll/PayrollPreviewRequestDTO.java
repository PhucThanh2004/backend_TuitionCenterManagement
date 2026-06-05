package com.management.student_center.dto.payroll;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PayrollPreviewRequestDTO {

    private Integer month;

    private Integer year;

    private Long teacherId;

    private Boolean overwriteExisting = false;

    public PayrollPreviewRequestDTO() {
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Boolean getOverwriteExisting() {
        return overwriteExisting;
    }

    public void setOverwriteExisting(Boolean overwriteExisting) {
        this.overwriteExisting = overwriteExisting;
    }
}