package com.management.student_center.repository;

import com.management.student_center.entity.Teacher;
import com.management.student_center.entity.User;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class TeacherSpecification {

	public static Specification<Teacher> hasRole(String roleId) {
		return (root, query, cb) -> {
			Join<Teacher, User> userJoin = root.join("userInfo");
			return cb.equal(userJoin.get("roleId"), roleId);
		};
	}

	public static Specification<Teacher> nameContains(String name) {
		if (name == null || name.isEmpty())
			return null; // Bỏ qua nếu filter rỗng
		return (root, query, cb) -> {
			Join<Teacher, User> userJoin = root.join("userInfo");
			return cb.like(cb.lower(userJoin.get("fullName")), "%" + name.toLowerCase() + "%");
		};
	}

	public static Specification<Teacher> genderIs(Boolean gender) {
		if (gender == null)
			return null; // Bỏ qua nếu filter rỗng
		return (root, query, cb) -> {
			Join<Teacher, User> userJoin = root.join("userInfo");
			return cb.equal(userJoin.get("gender"), gender);
		};
	}

	public static Specification<Teacher> specialtyContains(String specialty) {
		if (specialty == null || specialty.isEmpty())
			return null;
		return (root, query, cb) -> {
			// SỬA THÀNH: "specialty"
			return cb.like(cb.lower(root.get("specialty")), "%" + specialty.toLowerCase() + "%");
		};
	}
}