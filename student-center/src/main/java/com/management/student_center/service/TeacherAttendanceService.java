package com.management.student_center.service;

import com.management.student_center.dto.TeacherAttendanceResponseDTO;
import com.management.student_center.entity.*;
import com.management.student_center.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherAttendanceService {

    private final SessionRepository sessionRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherAttendanceRepository teacherAttendanceRepository;

    public TeacherAttendanceService(SessionRepository sessionRepository,
                                    TeacherRepository teacherRepository,
                                    TeacherAttendanceRepository teacherAttendanceRepository) {
        this.sessionRepository = sessionRepository;
        this.teacherRepository = teacherRepository;
        this.teacherAttendanceRepository = teacherAttendanceRepository;
    }

    // Lấy danh sách điểm danh giáo viên theo subject
    public TeacherAttendanceResponseDTO getAttendanceBySubject(Long subjectId) {
        // 1. Lấy tất cả teacher của môn học
        List<Teacher> teachers = teacherRepository.findAllByTeacherSubjects_Subject_Id(subjectId);

        // 2. Lấy tất cả session của môn học
        List<Session> sessions = sessionRepository.findBySubject_IdOrderBySessionDateAsc(subjectId);

        // 3. Map session thành DTO
        List<TeacherAttendanceResponseDTO.SessionDTO> sessionDTOs = sessions.stream()
                .map(s -> new TeacherAttendanceResponseDTO.SessionDTO(
                        s.getId(),
                        s.getSessionDate(),
                        s.getStartTime(),
                        s.getEndTime()))
                .toList();

        // 4. Map teacher + attendance
        List<TeacherAttendanceResponseDTO.TeacherAttendanceDTO> teacherDTOs = teachers.stream().map(t -> {

            // Lấy tất cả attendance của teacher này trong các session
            List<TeacherAttendance> attendances = teacherAttendanceRepository.findAllByTeacherAndSessionIn(t, sessions);

            // Map attendance cho từng session, nếu chưa có attendance thì để trống
            List<TeacherAttendanceResponseDTO.AttendanceItem> attendanceItems = sessions.stream().map(s -> {
                TeacherAttendance att = attendances.stream()
                        .filter(a -> a.getSession().getId().equals(s.getId()))
                        .findFirst()
                        .orElse(null);
                return new TeacherAttendanceResponseDTO.AttendanceItem(
                        s.getId(),
                        att != null ? att.getStatus() : "", // chưa có attendance -> rỗng
                        att != null ? att.getNote() : ""
                );
            }).toList();

            return new TeacherAttendanceResponseDTO.TeacherAttendanceDTO(
                    t.getId(),
                    t.getUserInfo().getFullName(),
                    attendanceItems
            );
        }).toList();

        // 5. Build response
        TeacherAttendanceResponseDTO response = new TeacherAttendanceResponseDTO();
        response.setSubjectId(subjectId);
        response.setSessions(sessionDTOs);
        response.setTeachers(teacherDTOs);

        return response;
    }


    // Cập nhật trạng thái giáo viên
    public String updateStatus(Long teacherId, Long sessionId, String status) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        TeacherAttendance existing = teacherAttendanceRepository
                .findBySessionAndTeacher(session, teacher)
                .orElse(null);

        if (existing != null) {
            existing.setStatus(status);
            teacherAttendanceRepository.save(existing);
            return "Cập nhật trạng thái điểm danh giáo viên thành công";
        }

        TeacherAttendance newAttendance = new TeacherAttendance();
        newAttendance.setTeacher(teacher);
        newAttendance.setSession(session);
        newAttendance.setStatus(status);
        teacherAttendanceRepository.save(newAttendance);

        return "Thêm mới trạng thái điểm danh giáo viên thành công";
    }

    // Cập nhật ghi chú giáo viên
    public String updateNote(Long teacherId, Long sessionId, String note) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        TeacherAttendance existing = teacherAttendanceRepository
                .findBySessionAndTeacher(session, teacher)
                .orElse(null);

        if (existing != null) {
            existing.setNote(note);
            teacherAttendanceRepository.save(existing);
            return "Cập nhật ghi chú giáo viên thành công";
        }

        TeacherAttendance newAttendance = new TeacherAttendance();
        newAttendance.setTeacher(teacher);
        newAttendance.setSession(session);
        newAttendance.setNote(note);
        teacherAttendanceRepository.save(newAttendance);

        return "Thêm mới ghi chú giáo viên thành công";
    }
}
