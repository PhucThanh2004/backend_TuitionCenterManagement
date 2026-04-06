package com.management.student_center.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.management.student_center.dto.DailySessionDTO;
import com.management.student_center.dto.UpcomingSessionDTO;
import com.management.student_center.entity.Session;
import com.management.student_center.entity.Subject;
import com.management.student_center.repository.SessionRepository;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public List<DailySessionDTO> getSessionsByDate(LocalDate date) {

        List<Session> sessions = sessionRepository.findSessionsByDate(date);

        return sessions.stream().map(session -> {

            Subject subject = session.getSubject();

            List<String> teachers = subject.getTeacherSubjects()
                    .stream()
                    .map(ts -> ts.getTeacher().getUserInfo().getFullName())
                    .toList();

            return new DailySessionDTO(
                    session.getId(),
                    subject.getName(),
                    session.getStartTime(),
                    session.getEndTime(),
                    session.getRoom() != null ? session.getRoom().getName() : null,
                    teachers
            );

        }).toList();
    }
    
    public List<UpcomingSessionDTO> getTop4UpcomingSessions() {
        Pageable pageable = PageRequest.of(0, 4);
        return sessionRepository.findUpcomingSessions(pageable);
    }
}
