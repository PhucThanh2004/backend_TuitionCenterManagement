package com.management.student_center.repository;

import com.management.student_center.entity.Session;
import com.management.student_center.entity.Teacher;
import com.management.student_center.entity.TeacherAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherAttendanceRepository extends JpaRepository<TeacherAttendance, Long> {

    Optional<TeacherAttendance> findBySessionAndTeacher(Session session, Teacher teacher);

    List<TeacherAttendance> findAllByTeacherAndSessionIn(Teacher teacher, List<Session> sessions);
}
