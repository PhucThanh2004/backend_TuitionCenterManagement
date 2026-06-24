package com.management.student_center.service.tuition;

import com.management.student_center.dto.tuition.TuitionCalculationDTO;
import com.management.student_center.entity.StudentTuition;
import com.management.student_center.enums.StudentTuitionStatus;
import com.management.student_center.repository.StudentTuitionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TuitionQueryService {

	private final StudentTuitionRepository tuitionRepository;

	public TuitionQueryService(StudentTuitionRepository tuitionRepository) {
		this.tuitionRepository = tuitionRepository;
	}

	public List<TuitionCalculationDTO> getTuitionsWithFilter(int month, int year, String name, String grade,
			StudentTuitionStatus status) {

		List<StudentTuition> entities = tuitionRepository.searchTuitions(month, year, name, grade, status);

		List<TuitionCalculationDTO> result = new ArrayList<>();

		for (StudentTuition entity : entities) {
			result.add(toDTO(entity));
		}

		return result;
	}

	public StudentTuition getTuitionDetail(Long studentId, int month, int year) {

		return tuitionRepository.findByStudentIdAndMonthAndYear(studentId, month, year).orElse(null);
	}

	private TuitionCalculationDTO toDTO(StudentTuition entity) {

		TuitionCalculationDTO dto = new TuitionCalculationDTO();

		dto.setTuitionId(entity.getId());

		dto.setStudentId(entity.getStudent().getId());

		dto.setFullName(entity.getStudent().getUserInfo().getFullName());

		dto.setPhoneNumber(entity.getStudent().getUserInfo().getPhoneNumber());

		dto.setGrade(entity.getStudent().getGrade());

		dto.setTotalAmount(entity.getTotalAmount());

		dto.setPaidAmount(entity.getPaidAmount());

		dto.setRemainingAmount(entity.getRemainingAmount());

		dto.setStatus(entity.getStatus().name());

		return dto;
	}
}