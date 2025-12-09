package com.management.student_center.service;

import com.management.student_center.dto.TeacherScheduleDTO;
import com.management.student_center.entity.Session;
import com.management.student_center.repository.SessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        return sessions.stream().map(session -> new TeacherScheduleDTO(
                session.getId(),
                session.getSessionDate(),
                session.getStartTime(),
                session.getEndTime(),
                session.getStatus(),
                session.getSubject().getId(),
                session.getSubject().getName(),
                session.getRoom() != null ? session.getRoom().getId() : null,
                session.getRoom() != null ? session.getRoom().getName() : null
        )).collect(Collectors.toList());
    }
}
