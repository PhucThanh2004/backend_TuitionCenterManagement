package com.management.student_center.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.management.student_center.entity.SubjectType;

public interface SubjectTypeRepository extends JpaRepository<SubjectType, Long> {
}