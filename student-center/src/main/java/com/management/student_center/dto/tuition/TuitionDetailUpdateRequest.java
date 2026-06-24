package com.management.student_center.dto.tuition;

import java.math.BigDecimal;

public class TuitionDetailUpdateRequest {

    private Long detailId;

    private Integer attendedSessions;

    private BigDecimal totalMoney;

    private String note;

    public Long getDetailId() {
        return detailId;
    }

    public void setDetailId(Long detailId) {
        this.detailId = detailId;
    }

    public Integer getAttendedSessions() {
        return attendedSessions;
    }

    public void setAttendedSessions(Integer attendedSessions) {
        this.attendedSessions = attendedSessions;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}