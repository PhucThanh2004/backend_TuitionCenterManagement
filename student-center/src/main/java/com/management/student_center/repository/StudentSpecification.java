package com.management.student_center.repository;

import com.management.student_center.entity.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class StudentSpecification {

	public static Specification<Student> hasRole(String roleId) {
		return (root, query, cb) -> {
			Join<Student, User> userJoin = root.join("userInfo");
			return cb.equal(userJoin.get("roleId"), roleId);
		};
	}

	public static Specification<Student> nameContains(String name) {
		if (name == null || name.isEmpty())
			return null;
		return (root, query, cb) -> {
			Join<Student, User> userJoin = root.join("userInfo");
			return cb.like(cb.lower(userJoin.get("fullName")), "%" + name.toLowerCase() + "%");
		};
	}

	public static Specification<Student> genderIs(Boolean gender) {
		if (gender == null)
			return null;
		return (root, query, cb) -> {
			Join<Student, User> userJoin = root.join("userInfo");
			return cb.equal(userJoin.get("gender"), gender);
		};
	}

	public static Specification<Student> gradeContains(String grade) {
		if (grade == null || grade.isEmpty())
			return null;
		return (root, query, cb) -> cb.like(root.get("grade"), "%" + grade + "%");
	}

	public static Specification<Student> schoolNameContains(String schoolName) {
		if (schoolName == null || schoolName.isEmpty())
			return null;
		return (root, query, cb) -> cb.like(cb.lower(root.get("schoolName")), "%" + schoolName.toLowerCase() + "%");
	}

	// 🔥 Lọc theo tên môn học (Join Student -> StudentSubject -> Subject)
	public static Specification<Student> hasSubjectName(String subjectName) {
		if (subjectName == null || subjectName.isEmpty())
			return null;
		return (root, query, cb) -> {
			// Vì Student có List<StudentSubject> studentSubjects (kiểm tra lại tên trường
			// trong Entity Student)
			Join<Student, StudentSubject> ssJoin = root.join("studentSubjects");
			Join<StudentSubject, Subject> subjectJoin = ssJoin.join("subject");
			return cb.like(cb.lower(subjectJoin.get("name")), "%" + subjectName.toLowerCase() + "%");
		};
	}

	public static Specification<Student> hasStatus(Boolean status) {
		return (root, query, cb) -> {
			if (status == null)
				return null;
			return cb.equal(root.get("userInfo").get("status"), status);
		};
	}
}