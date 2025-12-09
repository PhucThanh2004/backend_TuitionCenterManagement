package com.management.student_center.dto;

import java.util.List;

public class PaginationDTO {
	public long total;
	public int page;
	public int limit;
	public int totalPages;

	public PaginationDTO(long total, int page, int limit, int totalPages) {
		this.total = total;
		this.page = page;
		this.limit = limit;
		this.totalPages = totalPages;
	}
}
