package com.management.student_center.service;

import com.management.student_center.dto.CreateSubjectScheduleRequest;
import com.management.student_center.dto.ManualSessionRequest;
import com.management.student_center.dto.RoomDTO;
import com.management.student_center.dto.ScheduleInfoDTO;
import com.management.student_center.dto.SessionDTO;
import com.management.student_center.dto.SubjectScheduleDTO;
import com.management.student_center.entity.Room;
import com.management.student_center.entity.Session;
import com.management.student_center.entity.Subject;
import com.management.student_center.entity.SubjectSchedule;
import com.management.student_center.repository.RoomRepository;
import com.management.student_center.repository.SessionRepository;
import com.management.student_center.repository.SubjectRepository;
import com.management.student_center.repository.SubjectScheduleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubjectScheduleService {

	 private final SubjectScheduleRepository scheduleRepository;
	    private final SessionRepository sessionRepository;
	    private final SubjectRepository subjectRepository;
	    private final RoomRepository roomRepository;

	    public SubjectScheduleService(SubjectScheduleRepository scheduleRepository,
	                                  SessionRepository sessionRepository,
	                                  SubjectRepository subjectRepository,
	                                  RoomRepository roomRepository) {
	        this.scheduleRepository = scheduleRepository;
	        this.sessionRepository = sessionRepository;
	        this.subjectRepository = subjectRepository;
	        this.roomRepository = roomRepository;
	    }

    /**
     * Lấy tất cả sessions theo subjectId (include room.name và subjectSchedule(dayOfWeek, startTime, endTime))
     */
    @Transactional(readOnly = true)
    public List<SessionDTO> getScheduleBySubjectId(Long subjectId) {
        if (subjectId == null) {
            throw new IllegalArgumentException("subjectId is required");
        }

        List<Session> sessions = sessionRepository.findBySubjectIdWithRoomAndScheduleOrder(subjectId);
        return sessions.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Xoá session theo id
     */
    @Transactional
    public Map<String, Object> deleteSession(Long sessionId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("Thiếu sessionId để xoá");
        }

        Optional<Session> opt = sessionRepository.findById(sessionId);
        if (opt.isEmpty()) {
            throw new NoSuchElementException("Buổi học không tồn tại");
        }

        sessionRepository.delete(opt.get());

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Đã xoá buổi học thành công");
        result.put("id", sessionId);
        return result;
    }

    /**
     * Lấy session theo id (include room + schedule)
     */
    @Transactional(readOnly = true)
    public SessionDTO getSessionById(Long sessionId) {
        Session s = sessionRepository.findByIdWithRoomAndSchedule(sessionId);
        if (s == null) {
            throw new NoSuchElementException("Session không tồn tại");
        }
        return toDto(s);
    }

    // helper mapper
    private SessionDTO toDto(Session s) {

        RoomDTO roomDto = null;
        if (s.getRoom() != null) {
            roomDto = new RoomDTO(s.getRoom().getName());
        }

        ScheduleInfoDTO scheduleDto = null;
        if (s.getSchedule() != null) {
            scheduleDto = new ScheduleInfoDTO(
                    s.getSchedule().getDayOfWeek(),
                    s.getSchedule().getStartTime(),
                    s.getSchedule().getEndTime()
            );
        }

        return new SessionDTO(
                s.getId(),
                s.getSubject() != null ? s.getSubject().getId() : null,
                s.getSchedule() != null ? s.getSchedule().getId() : null,
                s.getSessionDate(),
                s.getStartTime(),
                s.getEndTime(),
                s.getRoom() != null ? s.getRoom().getId() : null,
                s.getStatus(),
                roomDto,
                scheduleDto
        );
    }
    
    @Transactional
    public Map<String, Object> createSubjectSchedule(CreateSubjectScheduleRequest req) {
        // 1️⃣ validate required
        if (req.getSubjectId() == null || req.getDayOfWeek() == null ||
            req.getStartTime() == null || req.getEndTime() == null ||
            req.getStartDate() == null) {
            throw new IllegalArgumentException("Thiếu thông tin lịch học bắt buộc");
        }

        if (req.getEndTime() != null && !req.getEndTime().isAfter(req.getStartTime())) {
            throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu");
        }

        Subject subject = subjectRepository.findById(req.getSubjectId())
                .orElseThrow(() -> new NoSuchElementException("Lớp học không tồn tại"));

        Room room = null;
        if (req.getRoomId() != null) {
            room = roomRepository.findById(req.getRoomId())
                    .orElseThrow(() -> new NoSuchElementException("Phòng học không tồn tại"));
        }

        // 2️⃣ Kiểm tra trùng lịch cùng phòng
        if (room != null) {
            boolean overlapRoom = scheduleRepository.existsByRoomAndDayOfWeekAndTimeOverlap(
                    room, req.getDayOfWeek(), req.getStartTime(), req.getEndTime());
            if (overlapRoom) {
                throw new IllegalArgumentException("Phòng học này đã có lịch trong cùng ngày");
            }
        }

        // 3️⃣ Kiểm tra trùng lịch cùng lớp
        boolean overlapSubject = scheduleRepository.existsBySubjectAndDayOfWeekAndTimeOverlap(
                subject, req.getDayOfWeek(), req.getStartTime(), req.getEndTime());
        if (overlapSubject) {
            throw new IllegalArgumentException("Lớp học này đã có lịch trong cùng ngày");
        }

        // 4️⃣ Tạo SubjectSchedule mới
        SubjectSchedule schedule = new SubjectSchedule();
        schedule.setSubject(subject);
        schedule.setDayOfWeek(req.getDayOfWeek());
        schedule.setStartTime(req.getStartTime());
        schedule.setEndTime(req.getEndTime());
        schedule.setRoom(room);
        schedule.setStartDate(req.getStartDate());
        schedule.setEndDate(req.getEndDate());
        scheduleRepository.save(schedule);

        // 5️⃣ Sinh các Session theo ngày
        List<Session> sessions = generateSessions(schedule);

     // Convert to DTOs
        SubjectScheduleDTO scheduleDto = new SubjectScheduleDTO(
                schedule.getId(),
                schedule.getSubject().getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getRoom() != null ? schedule.getRoom().getId() : null,
                schedule.getStartDate(),
                schedule.getEndDate()
        );

        List<SessionDTO> sessionDtos = sessions.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("schedule", scheduleDto);
        result.put("sessions", sessionDtos);
        return result;

    }

    private List<Session> generateSessions(SubjectSchedule schedule) {
        List<Session> sessionsToCreate = new ArrayList<>();
        LocalDate start = schedule.getStartDate();
        LocalDate end = schedule.getEndDate() != null ? schedule.getEndDate() : start;
        LocalTime startTime = schedule.getStartTime();
        LocalTime endTime = schedule.getEndTime();
        int dayOfWeek = schedule.getDayOfWeek();

        LocalDate current = start;
        while (!current.isAfter(end)) {
            if (current.getDayOfWeek().getValue() % 7 == dayOfWeek) { // Java: Monday=1 ... Sunday=7
            	boolean exists = sessionRepository.existsBySubjectAndSessionDateAndStartTime(
            	        schedule.getSubject(),
            	        current,
            	        startTime
            	);
                if (!exists && current.isAfter(LocalDate.now())) {
                    Session session = new Session();
                    session.setSubject(schedule.getSubject());
                    session.setSchedule(schedule);
                    session.setSessionDate(current);
                    session.setStartTime(startTime);
                    session.setEndTime(endTime);
                    session.setRoom(schedule.getRoom());
                    session.setStatus("scheduled");
                    sessionsToCreate.add(session);
                }
            }
            current = current.plusDays(1);
        }

        if (!sessionsToCreate.isEmpty()) {
            sessionRepository.saveAll(sessionsToCreate);
        }
        return sessionsToCreate;
    }
    
    @Transactional
    public SessionDTO addManualSession(ManualSessionRequest req) {
        if (req.getSubjectId() == null || req.getSessionDate() == null
                || req.getStartTime() == null || req.getEndTime() == null) {
            throw new IllegalArgumentException("Thiếu thông tin bắt buộc để tạo session");
        }

        if (!req.getEndTime().isAfter(req.getStartTime())) {
            throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu");
        }

        Subject subject = subjectRepository.findById(req.getSubjectId())
                .orElseThrow(() -> new NoSuchElementException("Lớp học không tồn tại"));

        Room room = null;
        if (req.getRoomId() != null) {
            room = roomRepository.findById(req.getRoomId())
                    .orElseThrow(() -> new NoSuchElementException("Phòng học không tồn tại"));
        }

        // Kiểm tra trùng giờ phòng
        boolean overlapRoom = sessionRepository.existsByRoomAndDateAndTimeOverlap(
                room, req.getSessionDate(), req.getStartTime(), req.getEndTime());
        if (overlapRoom) {
            throw new IllegalArgumentException("Phòng này đã có buổi khác trong cùng ngày");
        }

        // Kiểm tra trùng giờ lớp
        boolean overlapSubject = sessionRepository.existsBySubjectAndDateAndTimeOverlap(
                subject, req.getSessionDate(), req.getStartTime(), req.getEndTime());
        if (overlapSubject) {
            throw new IllegalArgumentException("Lớp học này đã có buổi khác trong cùng ngày");
        }

        Session session = new Session();
        session.setSubject(subject);
        session.setSchedule(req.getScheduleId() != null ? 
            scheduleRepository.findById(req.getScheduleId()).orElse(null) : null);
        session.setSessionDate(req.getSessionDate());
        session.setStartTime(req.getStartTime());
        session.setEndTime(req.getEndTime());
        session.setRoom(room);
        session.setStatus(req.getStatus() != null ? req.getStatus() : "scheduled");

        sessionRepository.save(session);
        return toDto(session);
    }

    @Transactional
    public SessionDTO updateSession(Long sessionId, ManualSessionRequest req) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Buổi học không tồn tại"));

        LocalDate newDate = req.getSessionDate() != null ? req.getSessionDate() : session.getSessionDate();
        LocalTime newStart = req.getStartTime() != null ? req.getStartTime() : session.getStartTime();
        LocalTime newEnd = req.getEndTime() != null ? req.getEndTime() : session.getEndTime();
        Room room = req.getRoomId() != null ? roomRepository.findById(req.getRoomId()).orElse(null) : session.getRoom();

        if (!newEnd.isAfter(newStart)) {
            throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu");
        }

        // Kiểm tra trùng giờ phòng (trừ chính nó)
        boolean overlapRoom = sessionRepository.existsByRoomAndDateAndTimeOverlapExcludingId(
                room, newDate, newStart, newEnd, sessionId);
        if (overlapRoom) {
            throw new IllegalArgumentException("Phòng này đã có buổi khác trong cùng ngày");
        }

        // Kiểm tra trùng giờ lớp (trừ chính nó)
        boolean overlapSubject = sessionRepository.existsBySubjectAndDateAndTimeOverlapExcludingId(
                session.getSubject(), newDate, newStart, newEnd, sessionId);
        if (overlapSubject) {
            throw new IllegalArgumentException("Lớp học này đã có buổi khác trong cùng ngày");
        }

        session.setSessionDate(newDate);
        session.setStartTime(newStart);
        session.setEndTime(newEnd);
        session.setRoom(room);
        session.setStatus(req.getStatus() != null ? req.getStatus() : session.getStatus());

        sessionRepository.save(session);
        return toDto(session);
    }


}
