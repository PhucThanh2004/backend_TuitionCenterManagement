package com.management.student_center.repository;

import com.management.student_center.entity.ConsultationRequest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRequestRepository
        extends JpaRepository<ConsultationRequest, Long> {
	
	 List<ConsultationRequest> findAllByOrderByCreatedAtDesc();
	 
	 long countByLastContactAtIsNull();
}