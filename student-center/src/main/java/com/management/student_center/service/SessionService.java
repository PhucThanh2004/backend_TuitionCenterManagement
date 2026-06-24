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

import jakarta.transaction.Transactional;

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
            "CANCELED".equals(leave.getStatus())) {
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
        
        // ========== 1. XỬ LÝ NỘI DUNG BUỔI HỌC ==========
        Boolean isFollowingPlan = session.getIsFollowPlan() != null ? session.getIsFollowPlan() : true;
        String displayTopic = null;
        String displayContent = null;
        String displayHomework = null;
        String plannedTopic = null;
        Long plannedSessionDetailId = null;
        String deviationReason = session.getDeviationReason();
        String noteForNextSession = session.getNoteForNextSession();
        
        if (isFollowingPlan && session.getPlannedSessionDetail() != null) {
            // Dạy đúng kế hoạch: hiển thị nội dung từ planned session detail
            SessionDetail plan = session.getPlannedSessionDetail();
            displayTopic = plan.getTopic();
            displayContent = plan.getContent();
            displayHomework = plan.getHomework();
            plannedSessionDetailId = plan.getId();
            plannedTopic = plan.getTopic();
        } else if (!isFollowingPlan) {
            // Dạy lệch: hiển thị nội dung thực tế
            displayTopic = session.getActualTopic();
            displayContent = session.getActualContent();
            displayHomework = session.getActualHomework();
            
            // Vẫn hiển thị kế hoạch để so sánh (nếu có)
            if (session.getPlannedSessionDetail() != null) {
                plannedTopic = session.getPlannedSessionDetail().getTopic();
                plannedSessionDetailId = session.getPlannedSessionDetail().getId();
            }
        } else if (session.getPlannedSessionDetail() != null) {
            // Trường hợp isFollowingPlan = null, mặc định hiển thị kế hoạch
            SessionDetail plan = session.getPlannedSessionDetail();
            displayTopic = plan.getTopic();
            displayContent = plan.getContent();
            displayHomework = plan.getHomework();
            plannedSessionDetailId = plan.getId();
            plannedTopic = plan.getTopic();
            isFollowingPlan = true;
        }
        
        // ========== 2. LẤY THÔNG TIN GIÁO VIÊN ==========
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
        
        // ========== 3. LẤY THÔNG TIN PHÒNG HỌC ==========
        SessionDetailDTO.RoomInfo roomInfo = null;
        if (session.getRoom() != null) {
            roomInfo = new SessionDetailDTO.RoomInfo(
                    session.getRoom().getId(),
                    session.getRoom().getName(),
                    session.getRoom().getSeatCapacity()
            );
        }
        
        // ========== 4. LẤY DANH SÁCH HỌC SINH ==========
        List<SessionDetailDTO.StudentAttendanceInfo> studentAttendanceInfos = new ArrayList<>();
        
        if (subject.getStudentSubjects() != null) {
            for (StudentSubject studentSubject : subject.getStudentSubjects()) {
                LocalDate enrollmentDate = studentSubject.getEnrollmentDate();
                LocalDate deletedAt = studentSubject.getDeletedAt();
                
                // Kiểm tra học sinh có active tại thời điểm session không
                boolean isActive = !sessionDate.isBefore(enrollmentDate) && 
                                  (deletedAt == null || sessionDate.isBefore(deletedAt));
                
                if (!isActive) {
                    continue;
                }
                
                Student student = studentSubject.getStudent();
                User studentUser = student.getUserInfo();
                
                String attendanceStatus = null;
                String attendanceNote = null;
                
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
        
        // ========== 5. LẤY TÊN LỚP HỌC ==========
        String className = null;
        if (session.getSchedule() != null) {
            className = session.getSchedule().getClass().getName();
        }
        
        // ========== 6. LẤY SLUG ==========
        String subjectSlug = null;
        if (subject.getSubjectType() != null) {
            subjectSlug = subject.getSubjectType().getName() + "-" + 
                          subject.getSubjectType().getAcademicLevel().getName();
        }
        
        // ========== 7. TẠO DTO ==========
        SessionDetailDTO dto = new SessionDetailDTO(
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
        
        // ========== 8. SET CÁC FIELD NỘI DUNG BUỔI HỌC ==========
        dto.setIsFollowingPlan(isFollowingPlan);
        dto.setDisplayTopic(displayTopic);
        dto.setDisplayContent(displayContent);
        dto.setDisplayHomework(displayHomework);
        dto.setPlannedTopic(plannedTopic);
        dto.setPlannedSessionDetailId(plannedSessionDetailId);
        dto.setDeviationReason(deviationReason);
        dto.setNoteForNextSession(noteForNextSession);
        
        return dto;
    }

        
    @Transactional
    public void updateActualContent(Long sessionId, SessionActualContentDTO request) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy buổi học với id: " + sessionId));
        Boolean isFollowPlan = request.getIsFollowPlan();
        if (isFollowPlan == null) {
            isFollowPlan = true;
        }
        
        // ✅ Set vào session
        session.setIsFollowPlan(isFollowPlan);
        
        if (isFollowPlan) {
            // Dạy đúng kế hoạch
            System.out.println("Dạy đúng kế hoạch");
            
            // ✅ QUAN TRỌNG: Set planned session detail
            Long plannedDetailId = request.getPlannedSessionDetailId();
            if (plannedDetailId != null) {
                SessionDetail plannedDetail = sessionDetailRepository.findById(plannedDetailId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy kế hoạch với id: " + plannedDetailId));
                session.setPlannedSessionDetail(plannedDetail);
                System.out.println("Set plannedSessionDetailId: " + plannedDetailId);
            } else {
                System.out.println("Warning: plannedSessionDetailId is null!");
            }
            
            // Clear các field thực tế
            session.setActualTopic(null);
            session.setActualContent(null);
            session.setActualHomework(null);
            session.setDeviationReason(null);
            session.setNoteForNextSession(null);
        } else {
            // Dạy lệch - cập nhật thông tin thực tế
            System.out.println("Dạy lệch kế hoạch");
            
            // Có thể vẫn giữ planned session detail để tham khảo
            Long plannedDetailId = request.getPlannedSessionDetailId();
            if (plannedDetailId != null) {
                SessionDetail plannedDetail = sessionDetailRepository.findById(plannedDetailId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy kế hoạch với id: " + plannedDetailId));
                session.setPlannedSessionDetail(plannedDetail);
            }
            
            session.setActualTopic(request.getActualTopic());
            session.setActualContent(request.getActualContent());
            session.setActualHomework(request.getActualHomework());
            session.setDeviationReason(request.getDeviationReason());
            session.setNoteForNextSession(request.getNoteForNextSession());
        }
        
        Session saved = sessionRepository.save(session);
        System.out.println("Saved session ID: " + saved.getId());
        System.out.println("Final plannedSessionDetailId: " + 
            (saved.getPlannedSessionDetail() != null ? saved.getPlannedSessionDetail().getId() : "null"));
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
