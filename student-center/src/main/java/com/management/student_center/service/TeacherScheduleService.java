package com.management.student_center.service;

import com.management.student_center.dto.TeacherScheduleDTO;
import com.management.student_center.entity.Session;
import com.management.student_center.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherScheduleService {

    private final SessionRepository sessionRepository;

    public TeacherScheduleService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public List<TeacherScheduleDTO> getTeacherSchedule(Long teacherId, LocalDate startDate, LocalDate endDate) {
        List<Session> sessions = sessionRepository.findTeacherSessionsInRange(teacherId, startDate, endDate);

        return sessions.stream()
                .map(session -> new TeacherScheduleDTO(
                        session.getId(),
                        session.getSessionDate(),
                        session.getStartTime(),
                        session.getEndTime(),
                        resolveStatus(session),
                        session.getSubject().getId(),
                        session.getSubject().getName(),
                        session.getRoom() != null ? session.getRoom().getId() : null,
                        session.getRoom() != null ? session.getRoom().getName() : null
                ))
                .collect(Collectors.toList());
    }

    private String resolveStatus(Session session) {
        String dbStatus = session.getStatus();

        // Nếu đã bị hủy hoặc hoàn thành từ database, giữ nguyên
        if ("canceled".equals(dbStatus)) return "canceled";
        if ("completed".equals(dbStatus)) return "completed";

        // Tính toán trạng thái dựa trên thời gian thực
        LocalDate sessionDate = session.getSessionDate();
        LocalDate today = LocalDate.now();

        LocalTime now = LocalTime.now();
        LocalTime start = session.getStartTime();
        LocalTime end = session.getEndTime();

        if (sessionDate.isAfter(today)) return "scheduled";
        if (sessionDate.isBefore(today)) return "expired";

        // Hôm nay
        if (now.isBefore(start)) return "scheduled";
        if (now.isAfter(end)) return "expired";

        return "ongoing";
    }
}