package com.management.student_center.dto.payroll;

import com.management.student_center.enums.TeacherPaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PayrollDetailResponseDTO {

    private Integer paymentId;

    private Long teacherId;

    private String teacherName;

    private Integer month;

    private Integer year;

    private BigDecimal amount;
    
    private BigDecimal paidAmount;

    private Integer totalSessions;

    private TeacherPaymentStatus status;

    private LocalDate paymentDate;

    private List<PayrollSessionDetailDTO> details;
    
    private Integer revisionNo;

    private BigDecimal previousAmount;

    private String lastAdjustmentReason;

    private LocalDateTime adjustedAt;

    private String teacherFeedback;

    private LocalDateTime teacherRejectedAt;

    private List<FeedbackHistoryDTO> feedbackHistory;
    
	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public Long getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(Long teacherId) {
		this.teacherId = teacherId;
	}

	public String getTeacherName() {
		return teacherName;
	}

	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public Integer getTotalSessions() {
		return totalSessions;
	}

	public void setTotalSessions(Integer totalSessions) {
		this.totalSessions = totalSessions;
	}

	public TeacherPaymentStatus getStatus() {
		return status;
	}

	public void setStatus(TeacherPaymentStatus status) {
		this.status = status;
	}

	public LocalDate getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDate paymentDate) {
		this.paymentDate = paymentDate;
	}

	public List<PayrollSessionDetailDTO> getDetails() {
		return details;
	}

	public void setDetails(List<PayrollSessionDetailDTO> details) {
		this.details = details;
	}

	public Integer getRevisionNo() {
		return revisionNo;
	}

	public void setRevisionNo(Integer revisionNo) {
		this.revisionNo = revisionNo;
	}

	public BigDecimal getPreviousAmount() {
		return previousAmount;
	}

	public void setPreviousAmount(BigDecimal previousAmount) {
		this.previousAmount = previousAmount;
	}

	public String getLastAdjustmentReason() {
		return lastAdjustmentReason;
	}

	public void setLastAdjustmentReason(String lastAdjustmentReason) {
		this.lastAdjustmentReason = lastAdjustmentReason;
	}

	public LocalDateTime getAdjustedAt() {
		return adjustedAt;
	}

	public void setAdjustedAt(LocalDateTime adjustedAt) {
		this.adjustedAt = adjustedAt;
	}

	public String getTeacherFeedback() {
		return teacherFeedback;
	}

	public void setTeacherFeedback(String teacherFeedback) {
		this.teacherFeedback = teacherFeedback;
	}

	public LocalDateTime getTeacherRejectedAt() {
		return teacherRejectedAt;
	}

	public void setTeacherRejectedAt(LocalDateTime teacherRejectedAt) {
		this.teacherRejectedAt = teacherRejectedAt;
	}

	public List<FeedbackHistoryDTO> getFeedbackHistory() {
		return feedbackHistory;
	}

	public void setFeedbackHistory(List<FeedbackHistoryDTO> feedbackHistory) {
		this.feedbackHistory = feedbackHistory;
	}
	
}