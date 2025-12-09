package com.management.student_center.dto;

import java.util.List;

public class UpdateAnnouncementRequest {
	private String title;
	private String content;
	private String imageURL;
	private String status;

	private List<String> attachments;
	private Boolean clearImage;
	private Boolean clearAttachments;

	public Boolean getClearImage() {
		return clearImage;
	}

	public void setClearImage(Boolean clearImage) {
		this.clearImage = clearImage;
	}

	public Boolean getClearAttachments() {
		return clearAttachments;
	}

	public void setClearAttachments(Boolean clearAttachments) {
		this.clearAttachments = clearAttachments;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}
}