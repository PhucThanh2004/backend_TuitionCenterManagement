package com.management.student_center.dto;

import java.util.List;

public class PaginatedResponseDTO<T> {
	public List<T> data;
	public PaginationDTO pagination; // Nó sẽ tham chiếu đến class ở file kia

	public PaginatedResponseDTO(List<T> data, PaginationDTO pagination) {
		this.data = data;
		this.pagination = pagination;
	}
}