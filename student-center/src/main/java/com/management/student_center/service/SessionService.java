package com.management.student_center.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.management.student_center.dto.DailySessionDTO;
import com.management.student_center.dto.SessionActualContentDTO;
import com.management.student_center.dto.SessionContentDTO;
import com.management.student_center.dto.SessionDetailDTO;
import com.management.student_center.dto.SessionDetailDTO.TeacherAttendanceInfo;
import com.management.student_center.dto.UpcomingSessionDTO;
import com.management.student_center.entity.Session;
import com.management.student_center.entity.SessionDetail;
import com.management.student_center.entity.Student;
import com.management.student_center.entity.StudentSubject;
import com.management.student_center.entity.Subject;
import com.management.student_center.entity.Teacher;
import com.management.student_center.entity.TeacherAttendance;
import com.management.student_center.entity.TeacherLeave;
import com.management.student_center.entity.User;
import com.management.student_center.entity.AttendanceStudent;
import com.management.student_center.entity.LeaveAffectedSession;
import com.management.student_center.repository.LeaveAffectedSessionRepository;
import com.management.student_center.repository.SessionDetailRepository;
import com.management.student_center.repository.SessionRepository;
import com.management.student_center.repository.TeacherLeaveRepository;
import com.management.student_center.repository.TeacherRepository;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private TeacherLeaveRepository teacherLeaveRepository;

    @Autowired
    private LeaveAffectedSessionRepository leaveAffectedSessionRepository;
    
    @Autowired 
    private SessionDetailRepository sessionDetailRepository;

    @Autowired
    private TeacherRepository teacherRepository;
    
    private String resolveStatus(Session session) {

        String dbStatus = session.getStatus();

        if ("canceled".equals(dbStatus)) return "canceled";
        if ("completed".equals(dbStatus)) return "completed";

        LocalDate sessionDate = session.getSessionDate();
        LocalDate today = LocalDate.now();

        LocalTime now = LocalTime.now();
        LocalTime start = session.getStartTime();
        LocalTime end = session.getEndTime();

        if (sessionDate.isAfter(today)) return "scheduled";

        if (sessionDate.isBefore(today)) return "expired";

        // today
        if (now.isBefore(start)) return "scheduled";

        if (now.isAfter(end)) return "expired";

        return "ongoing";
    }
    
    private DailySessionDTO.TeacherLeaveInfo resolveTeacherLeave(Session session) {

        Subject subject = session.getSubject();

        var teacherSubject = subject.getTeacherSubjects()
                .stream()
                .findFirst()
                .orElse(null);

        if (teacherSubject == null) {
            return new DailySessionDTO.TeacherLeaveInfo("NONE", null, null);
        }

        Long teacherId = teacherSubject.getTeacher().getId();
        LocalDate date = session.getSessionDate();

        List<TeacherLeave> leaves =
                teacherLeaveRepository.findByTeacherIdAndDate(teacherId, date);

        if (leaves.isEmpty()) {
            return new DailySessionDTO.TeacherLeaveInfo("NONE", null, null);
        }

        TeacherLeave leave = leaves.get(0);

        // 1. PENDING
        if ("PENDING".equals(leave.getStatus())) {
            return new DailySessionDTO.TeacherLeaveInfo("PENDING", null, null);
        }

        // 2. REJECTED / CANCELLED
        if ("REJECTED".equals(leave.getStatus()) ||
            "CANCELLED".equals(leave.getStatus())) {
            return new DailySessionDTO.TeacherLeaveInfo("NONE", null, null);
        }

        // 3. APPROVED
        var affectedOpt =
                leaveAffectedSessionRepository.findByLeaveIdAndSessionId(
                        leave.getId(), session.getId()
                );

        if (affectedOpt.isEmpty()) {
            return new DailySessionDTO.TeacherLeaveInfo("APPROVED", null, null);
        }

        var affected = affectedOpt.get();

        // 3.1 PENDING replacement
        if (affected.getStatus() == LeaveAffectedSession.Status.PENDING) {
            return new DailySessionDTO.TeacherLeaveInfo(
                    "AWAITING_REPLACEMENT",
                    null,
                    null
            );
        }

        // 3.2 SKIPPED
        if (affected.getStatus() == LeaveAffectedSession.Status.SKIPPED) {
            return new DailySessionDTO.TeacherLeaveInfo("NONE", null, null);
        }

        // 3.3 RESOLVED
        if (affected.getStatus() == LeaveAffectedSession.Status.RESOLVED) {

            var teacherOpt = teacherRepository.findById(
                    affected.getReplacementTeacherId().longValue()
            );

            return new DailySessionDTO.TeacherLeaveInfo(
                    "RESOLVED",
                    affected.getReplacementTeacherId().longValue(),
                    teacherOpt.map(t -> t.getUserInfo().getFullName()).orElse(null)
            );
        }

        return new DailySessionDTO.TeacherLeaveInfo("NONE", null, null);
    }

    public List<DailySessionDTO> getSessionsByDate(LocalDate date) {

        List<Session> sessions = sessionRepository.findSessionsByDate(date);

        return sessions.stream().map(session -> {

            Subject subject = session.getSubject();

            var teacherSubject = subject.getTeacherSubjects()
                    .stream()
                    .findFirst()
                    .orElse(null);

            DailySessionDTO.TeacherInfo teacher = null;

            if (teacherSubject != null) {
                teacher = new DailySessionDTO.TeacherInfo(
                        teacherSubject.getTeacher().getId(),
                        teacherSubject.getTeacher().getUserInfo().getFullName()
                );
            }
            return new DailySessionDTO(
                    session.getId(),
                    session.getSessionDate(),
                    subject.getName(),
                    session.getStartTime(),
                    session.getEndTime(),
                    session.getRoom() != null ? session.getRoom().getName() : null,
                    teacher,
                    resolveStatus(session),
                    resolveTeacherLeave(session) 
            );

        }).toList();
    }
    
    public List<UpcomingSessionDTO> getTop4UpcomingSessions() {
        Pageable pageable = PageRequest.of(0, 4);
        return sessionRepository.findUpcomingSessions(pageable);
    }
    
    public SessionDetailDTO getSessionDetail(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));
        
        Subject subject = session.getSubject();
        LocalDate sessionDate = session.getSessionDate();
        
        // Tính tổng thời gian
        long totalMinutes = java.time.Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();
        
        // Lấy thông tin giáo viên
        SessionDetailDTO.TeacherInfo teacherInfo = null;
        SessionDetailDTO.TeacherAttendanceInfo teacherAttendanceInfo = null;
        
        var teacherSubject = subject.getTeacherSubjects()
                .stream()
                .findFirst()
                .orElse(null);
        
        if (teacherSubject != null) {
            Teacher teacher = teacherSubject.getTeacher();
            User user = teacher.getUserInfo();
            
            teacherInfo = new SessionDetailDTO.TeacherInfo(
                    teacher.getId(),
                    user.getFullName(),
                    teacher.getSpecialty(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getImage()
            );
            
            // Lấy điểm danh giáo viên
            if (session.getTeacherAttendances() != null && !session.getTeacherAttendances().isEmpty()) {
                TeacherAttendance teacherAttendance = session.getTeacherAttendances().get(0);
                teacherAttendanceInfo = new SessionDetailDTO.TeacherAttendanceInfo(
                        teacherAttendance.getTeacher().getId(),
                        teacherAttendance.getTeacher().getUserInfo().getFullName(),
                        teacherAttendance.getStatus(),
                        teacherAttendance.getNote()
                );
            }
        }
        
        // Lấy thông tin phòng học
        SessionDetailDTO.RoomInfo roomInfo = null;
        if (session.getRoom() != null) {
            roomInfo = new SessionDetailDTO.RoomInfo(
                    session.getRoom().getId(),
                    session.getRoom().getName(),
                    session.getRoom().getSeatCapacity()
            );
        }
        
        // Lấy danh sách học sinh ĐANG ACTIVE tại thời điểm session diễn ra
        List<SessionDetailDTO.StudentAttendanceInfo> studentAttendanceInfos = new ArrayList<>();
        
        // Lọc học sinh đăng ký môn này và còn hiệu lực tại thời điểm session
        if (subject.getStudentSubjects() != null) {
            for (StudentSubject studentSubject : subject.getStudentSubjects()) {
                LocalDate enrollmentDate = studentSubject.getEnrollmentDate();
                LocalDate deletedAt = studentSubject.getDeletedAt();
                
                // Kiểm tra học sinh có active tại thời điểm session không
                boolean isActive = !sessionDate.isBefore(enrollmentDate) && 
                                  (deletedAt == null || sessionDate.isBefore(deletedAt));
                
                if (!isActive) {
                    continue; // Bỏ qua học sinh không active
                }
                
                Student student = studentSubject.getStudent();
                User studentUser = student.getUserInfo();
                
                String attendanceStatus = null;
                String attendanceNote = null;
                
                // Tìm điểm danh của học sinh này trong session
                if (session.getAttendanceStudents() != null) {
                    for (AttendanceStudent attendance : session.getAttendanceStudents()) {
                        if (attendance.getStudent().getId().equals(student.getId())) {
                            attendanceStatus = attendance.getStatus();
                            attendanceNote = attendance.getNote();
                            break;
                        }
                    }
                }
                
                SessionDetailDTO.StudentAttendanceInfo studentInfo = 
                        new SessionDetailDTO.StudentAttendanceInfo(
                                student.getId(),
                                studentUser.getFullName(),
                                studentUser.getEmail(),
                                studentUser.getPhoneNumber(),
                                attendanceStatus,
                                attendanceNote
                        );
                studentAttendanceInfos.add(studentInfo);
            }
        }
        
        // Lấy tên lớp học (nếu có schedule)
        String className = null;
        if (session.getSchedule() != null) {
            className = session.getSchedule().getClass().getName();
        }
        
        // Lấy slug từ subjectType
        String subjectSlug = null;
        if (subject.getSubjectType() != null) {
            subjectSlug = subject.getSubjectType().getName() + "-" + subject.getSubjectType().getAcademicLevel().getName();
        }
        
        return new SessionDetailDTO(
                session.getId(),
                resolveStatus(session),
                className,
                subject.getName(),
                subjectSlug,
                session.getSessionDate(),
                session.getStartTime(),
                session.getEndTime(),
                totalMinutes,
                teacherInfo,
                roomInfo,
                studentAttendanceInfos,
                teacherAttendanceInfo
        );
    }
    
    public void updateActualContent(Long sessionId, SessionActualContentDTO request) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy buổi học với id: " + sessionId));
        
        session.setIsFollowPlan(request.getIsFollowPlan());
        
        if (request.getIsFollowPlan()) {
            // Nếu dạy đúng kế hoạch, chỉ cần set planned session detail
            if (request.getPlannedSessionDetailId() != null) {
                SessionDetail plannedDetail = sessionDetailRepository.findById(request.getPlannedSessionDetailId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy kế hoạch"));
                session.setPlannedSessionDetail(plannedDetail);
            }
            // Clear các field thực tế
            session.setActualTopic(null);
            session.setActualContent(null);
            session.setActualHomework(null);
            session.setDeviationReason(null);
        } else {
            // Nếu dạy lệch, cập nhật đầy đủ thông tin thực tế
            if (request.getPlannedSessionDetailId() != null) {
                SessionDetail plannedDetail = sessionDetailRepository.findById(request.getPlannedSessionDetailId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy kế hoạch"));
                session.setPlannedSessionDetail(plannedDetail);
            }
            session.setActualTopic(request.getActualTopic());
            session.setActualContent(request.getActualContent());
            session.setActualHomework(request.getActualHomework());
            session.setDeviationReason(request.getDeviationReason());
            session.setNoteForNextSession(request.getNoteForNextSession());
        }
        
        sessionRepository.save(session);
    }

    public SessionContentDTO getSessionContent(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy buổi học"));
        
        SessionContentDTO content = new SessionContentDTO();
        content.setSessionId(session.getId());
        content.setSessionDate(session.getSessionDate());
        content.setStartTime(session.getStartTime());
        content.setEndTime(session.getEndTime());
        
        if (session.getIsFollowPlan() && session.getPlannedSessionDetail() != null) {
            // Dạy đúng kế hoạch
            SessionDetail plan = session.getPlannedSessionDetail();
            content.setDisplayTopic(plan.getTopic());
            content.setDisplayContent(plan.getContent());
            content.setDisplayHomework(plan.getHomework());
            content.setIsFollowingPlan(true);
        } else if (!session.getIsFollowPlan()) {
            // Dạy lệch
            content.setDisplayTopic(session.getActualTopic());
            content.setDisplayContent(session.getActualContent());
            content.setDisplayHomework(session.getActualHomework());
            content.setIsFollowingPlan(false);
            content.setDeviationReason(session.getDeviationReason());
            content.setNoteForNextSession(session.getNoteForNextSession());
            
            // Vẫn hiển thị kế hoạch để so sánh
            if (session.getPlannedSessionDetail() != null) {
                content.setPlannedTopic(session.getPlannedSessionDetail().getTopic());
            }
        }
        
        return content;
    }

}
