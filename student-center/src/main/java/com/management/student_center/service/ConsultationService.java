package com.management.student_center.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.management.student_center.dto.assistant.CreateConsultationRequestDTO;
import com.management.student_center.entity.ConsultationRequest;
import com.management.student_center.repository.ConsultationRequestRepository;

@Service
public class ConsultationService {
	private final ConsultationRequestRepository repository;
	public ConsultationService(ConsultationRequestRepository repository) {
		this.repository = repository;
	}
	public Long create(CreateConsultationRequestDTO request) {

		if (request.getParentName() == null || request.getParentName().isBlank()) {
			throw new RuntimeException("Vui lòng nhập tên phụ huynh.");
		}
		if ((request.getPhone() == null || request.getPhone().isBlank())
				&& (request.getEmail() == null || request.getEmail().isBlank())) {
			throw new RuntimeException("Vui lòng nhập số điện thoại hoặc email.");
		}
		ConsultationRequest consultation = new ConsultationRequest();
		consultation.setParentName(request.getParentName());
		consultation.setPhone(request.getPhone());
		consultation.setEmail(request.getEmail());
		repository.save(consultation);
		return consultation.getId();
	}

	public List<ConsultationRequest> getAll() {
		return repository.findAllByOrderByCreatedAtDesc();
	}
	public void markAsContacted(Long id) {
		ConsultationRequest consultation = repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu tư vấn."));
		consultation.setLastContactAt(LocalDateTime.now());
		repository.save(consultation);
	}
	
	public long countPendingConsultations() {
	    return repository.countByLastContactAtIsNull();
	}
}