package com.management.student_center.service;

import com.management.student_center.dto.AttendanceResponseDTO;
import com.management.student_center.dto.AttendanceStudentDTO;
import com.management.student_center.dto.TodayAttendanceDTO;
import com.management.student_center.entity.*;
import com.management.student_center.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final SessionRepository sessionRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final AttendanceStudentRepository attendanceStudentRepository;
    private final StudentRepository studentRepository;
    
    public AttendanceService(
            SessionRepository sessionRepository,
            StudentSubjectRepository studentSubjectRepository,
            AttendanceStudentRepository attendanceStudentRepository,
            StudentRepository studentRepository
    ) {
        this.sessionRepository = sessionRepository;
        this.studentSubjectRepository = studentSubjectRepository;
        this.attendanceStudentRepository = attendanceStudentRepository;
        this.studentRepository = studentRepository;
    }
    
    public List<AttendanceStudentDTO> getAbsentOrLateStudentsInDateRange(LocalDate startDate, LocalDate endDate) {
        List<AttendanceStudent> records = attendanceStudentRepository.findAbsentOrLateBetweenDates(startDate, endDate);

        // Map sang DTO có thêm thông tin lớp học
        return records.stream()
                .map(a -> new AttendanceStudentDTO(
                        a.getStudent().getId(),
                        a.getStudent().getUserInfo().getFullName(),
                        a.getStudent().getUserInfo().getEmail(),
                        a.getStudent().getGrade(),
                        a.getStudent().getSchoolName(),
                        a.getStatus(),
                        a.getNote(),
                        a.getSession().getSessionDate(),
                        a.getSession().getSubject().getId(),
                        a.getSession().getSubject().getName()
                ))
                .collect(Collectors.toList());
    }
    

    // Lấy danh sách điểm danh
    /*public AttendanceResponseDTO getAttendanceBySubject(Long subjectId) {

        List<Session> sessions = sessionRepository.findBySubject_IdOrderBySessionDateAsc(subjectId);

        List<StudentSubject> studentSubjects = studentSubjectRepository.findBySubject_Id(subjectId);

        AttendanceResponseDTO response = new AttendanceResponseDTO();

        // map sessions
        var sessionDTOs = sessions.stream()
                .map(s -> new AttendanceResponseDTO.SessionDTO(
                        s.getId(),
                        s.getSessionDate(),
                        s.getStartTime(),
                        s.getEndTime()))
                .toList();

        // map students + attendances
        var students = studentSubjects.stream().map(ss -> {
            Student student = ss.getStudent();

            List<AttendanceStudent> attendances =
                    attendanceStudentRepository.findAllByStudentAndSessionIn(student, sessions);

            var attendanceItems = attendances.stream()
                    .map(a -> new AttendanceResponseDTO.AttendanceItem(
                            a.getSession().getId(),
                            a.getStatus(),
                            a.getNote()
                    ))
                    .toList();

            return new AttendanceResponseDTO.StudentAttendanceDTO(
                    student.getId(),
                    student.getUserInfo().getFullName(),
                    attendanceItems
            );
        }).toList();
        
        response.setSubjectId(subjectId);
        response.setSessions(sessionDTOs);
        response.setStudents(students);

        return response;
    }*/
    
    public AttendanceResponseDTO getAttendanceBySubject(Long subjectId) {
        // 1. Lấy tất cả sessions
        List<Session> sessions = sessionRepository.findBySubject_IdOrderBySessionDateAsc(subjectId);
        
        if (sessions.isEmpty()) {
            AttendanceResponseDTO response = new AttendanceResponseDTO();
            response.setSubjectId(subjectId);
            response.setSessions(Collections.emptyList());
            response.setStudents(Collections.emptyList());
            return response;
        }
        
        // 2. Lấy TẤT CẢ student subjects (kể cả đã xóa)
        List<StudentSubject> allStudentSubjects = studentSubjectRepository.findBySubject_Id(subjectId);
        
        // 3. Group student subjects theo studentId, sắp xếp theo thời gian
        Map<Long, List<StudentSubject>> studentSubjectsByStudentId = allStudentSubjects.stream()
            .collect(Collectors.groupingBy(ss -> ss.getStudent().getId()));
        
        // 4. Với mỗi student, xử lý các giai đoạn đăng ký khác nhau
        Map<Long, Student> studentMap = new HashMap<>();
        Map<Long, List<StudentSubject>> studentPeriodsMap = new HashMap<>();
        
        for (Map.Entry<Long, List<StudentSubject>> entry : studentSubjectsByStudentId.entrySet()) {
            Long studentId = entry.getKey();
            List<StudentSubject> periods = entry.getValue();
            
            // Sắp xếp theo thời gian enrollmentDate
            periods.sort(Comparator.comparing(StudentSubject::getEnrollmentDate));
            
            // Lấy student từ bản ghi đầu tiên
            if (!periods.isEmpty()) {
                studentMap.put(studentId, periods.get(0).getStudent());
            }
            
            // Xử lý các giai đoạn: ghép các khoảng thời gian
            List<StudentSubject> mergedPeriods = new ArrayList<>();
            
            for (StudentSubject period : periods) {
                LocalDate start = period.getEnrollmentDate();
                LocalDate end = period.getDeletedAt() != null ? period.getDeletedAt() : LocalDate.MAX;
                
                // Nếu danh sách rỗng, thêm trực tiếp
                if (mergedPeriods.isEmpty()) {
                    mergedPeriods.add(period);
                    continue;
                }
                
                // Kiểm tra xem có overlap hoặc liền kề với period cuối không
                StudentSubject lastPeriod = mergedPeriods.get(mergedPeriods.size() - 1);
                LocalDate lastEnd = lastPeriod.getDeletedAt() != null ? lastPeriod.getDeletedAt() : LocalDate.MAX;
                
                // Nếu period mới bắt đầu trước hoặc ngay sau khi period cũ kết thúc
                if (!start.isAfter(lastEnd)) {
                    // Merge: kéo dài deletedAt nếu cần
                    if (end.isAfter(lastEnd)) {
                        if (lastPeriod.getDeletedAt() == null) {
                            // Không thể merge vì period cũ chưa kết thúc
                            mergedPeriods.add(period);
                        } else {
                            // Cập nhật deletedAt của period cũ
                            // Note: StudentSubject là entity, không nên modify trực tiếp trong logic này
                            // Tốt nhất là tạo DTO riêng cho period
                        }
                    }
                } else {
                    mergedPeriods.add(period);
                }
            }
            
            studentPeriodsMap.put(studentId, mergedPeriods);
        }
        
        // 5. Lấy tất cả attendance records
        List<AttendanceStudent> allAttendances = attendanceStudentRepository.findAllBySessionIn(sessions);
        Map<String, AttendanceStudent> attendanceMap = new HashMap<>();
        for (AttendanceStudent attendance : allAttendances) {
            String key = attendance.getStudent().getId() + "_" + attendance.getSession().getId();
            attendanceMap.put(key, attendance);
        }
        
        // 6. Xây dựng DTO cho từng học sinh
        List<AttendanceResponseDTO.StudentAttendanceDTO> studentDTOs = new ArrayList<>();
        
        for (Map.Entry<Long, List<StudentSubject>> entry : studentPeriodsMap.entrySet()) {
            Long studentId = entry.getKey();
            Student student = studentMap.get(studentId);
            List<StudentSubject> periods = entry.getValue();
            
            // Tìm period active tại mỗi session và xây dựng attendanceItems
            List<AttendanceResponseDTO.AttendanceItem> attendanceItems = new ArrayList<>();
            LocalDate currentEnrollmentDate = null;
            LocalDate currentDeletedAt = null;
            
            for (Session session : sessions) {
                Long sessionId = session.getId();
                LocalDate sessionDate = session.getSessionDate();
                
                // Tìm period phù hợp cho session này
                StudentSubject activePeriod = null;
                for (StudentSubject period : periods) {
                    LocalDate startDate = period.getEnrollmentDate();
                    LocalDate endDate = period.getDeletedAt() != null ? period.getDeletedAt() : LocalDate.MAX;
                    
                    if (!sessionDate.isBefore(startDate) && sessionDate.isBefore(endDate)) {
                        activePeriod = period;
                        break;
                    }
                }
                
                String key = studentId + "_" + sessionId;
                AttendanceStudent attendance = attendanceMap.get(key);
                
                if (activePeriod == null) {
                    // Không có period nào active ở session này
                    // Kiểm tra xem có period nào bắt đầu sau không
                    boolean hasFuturePeriod = periods.stream()
                        .anyMatch(p -> p.getEnrollmentDate().isAfter(sessionDate));
                    
                    if (hasFuturePeriod) {
                        attendanceItems.add(new AttendanceResponseDTO.AttendanceItem(
                            sessionId, "not_enrolled_yet", "Chưa đăng ký học tại thời điểm này"
                        ));
                    } else {
                        attendanceItems.add(new AttendanceResponseDTO.AttendanceItem(
                            sessionId, "completed", "Đã hoàn thành khóa học trước đó"
                        ));
                    }
                } else {
                    // Có period active
                    if (attendance != null) {
                        attendanceItems.add(new AttendanceResponseDTO.AttendanceItem(
                            sessionId, attendance.getStatus(), attendance.getNote()
                        ));
                    } else {
                        attendanceItems.add(new AttendanceResponseDTO.AttendanceItem(
                            sessionId, "pending", "Chưa điểm danh"
                        ));
                    }
                }
            }
            
            // Lấy enrollmentDate và deletedAt hiện tại (period cuối cùng)
            StudentSubject lastPeriod = periods.get(periods.size() - 1);
            LocalDate enrollmentDate = lastPeriod.getEnrollmentDate();
            LocalDate deletedAt = lastPeriod.getDeletedAt();
            
            AttendanceResponseDTO.StudentAttendanceDTO studentDTO = 
                new AttendanceResponseDTO.StudentAttendanceDTO(
                    studentId,
                    student.getUserInfo().getFullName(),
                    attendanceItems,
                    enrollmentDate,
                    deletedAt
                );
            
            studentDTOs.add(studentDTO);
        }
        
        // 7. Đóng gói response
        AttendanceResponseDTO response = new AttendanceResponseDTO();
        response.setSubjectId(subjectId);
        response.setSessions(sessions.stream()
            .map(s -> new AttendanceResponseDTO.SessionDTO(
                s.getId(), s.getSessionDate(), s.getStartTime(), s.getEndTime()))
            .collect(Collectors.toList()));
        response.setStudents(studentDTOs);
        
        return response;
    }

    @Transactional
    public String updateStatus(Long studentId, Long sessionId, String status) {
        // Validate status
        if (!Arrays.asList("present", "late", "absent").contains(status)) {
            throw new IllegalArgumentException("Status không hợp lệ. Chấp nhận: present, late, absent");
        }
        
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + sessionId));
        
        if ("canceled".equals(session.getStatus())) {
            throw new IllegalStateException("Không thể điểm danh cho buổi học đã bị hủy");
        }
        
        AttendanceStudent existing = attendanceStudentRepository
                .findBySessionAndStudent(session, student)
                .orElse(null);
        
        if (existing != null) {
            existing.setStatus(status);
            attendanceStudentRepository.save(existing);
        } else {
            AttendanceStudent newAttendance = new AttendanceStudent();
            newAttendance.setStudent(student);
            newAttendance.setSession(session);
            newAttendance.setStatus(status);
            attendanceStudentRepository.save(newAttendance);
        }
        
        updateSessionStatusBasedOnAttendance(session);
        
        return existing != null ? 
            "Cập nhật trạng thái điểm danh thành công" : 
            "Thêm mới trạng thái điểm danh thành công";
    }

    private void updateSessionStatusBasedOnAttendance(Session session) {
        if ("completed".equals(session.getStatus()) || "canceled".equals(session.getStatus())) {
            return;
        }
        
        List<AttendanceStudent> attendances = attendanceStudentRepository.findBySession(session);
        
        boolean hasPresentOrLate = attendances.stream()
                .anyMatch(a -> "present".equals(a.getStatus()) || "late".equals(a.getStatus()));
        
        if (hasPresentOrLate) {
            session.setStatus("completed");
            sessionRepository.save(session);
        }
    }

    // cập nhật ghi chú
    public String updateNote(Long studentId, Long sessionId, String note) {
    	  List<Student> allStudents = studentRepository.findAll();

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        AttendanceStudent existing = attendanceStudentRepository
                .findBySessionAndStudent(session, student)
                .orElse(null);

        if (existing != null) {
            existing.setNote(note);
            attendanceStudentRepository.save(existing);
            return "Cập nhật ghi chú thành công";
        }

        AttendanceStudent newAttendance = new AttendanceStudent();
        newAttendance.setStudent(student);
        newAttendance.setSession(session);
        newAttendance.setNote(note);
        attendanceStudentRepository.save(newAttendance);

        return "Thêm mới ghi chú thành công";
    }
    
    @Transactional(readOnly = true)
    public TodayAttendanceDTO getAttendanceByDate(Long subjectId, LocalDate date) {

        // 1. tìm session theo ngày truyền vào
        Session session = sessionRepository
                .findBySubjectIdAndSessionDate(subjectId, date)
                .orElse(null);

        if (session == null) {
            return null; // hoặc throw exception
        }

        // 2. teacher attendance
        String teacherStatus = session.getTeacherAttendances()
                .stream()
                .findFirst()
                .map(TeacherAttendance::getStatus)
                .orElse("not_marked");

        // 3. student attendance
        List<AttendanceStudent> list = session.getAttendanceStudents();

        long total = list.size();
        long present = list.stream()
                .filter(a -> "present".equals(a.getStatus()))
                .count();

        long absent = list.stream()
                .filter(a -> "absent".equals(a.getStatus()))
                .count();

        long late = list.stream()
                .filter(a -> "late".equals(a.getStatus()))
                .count();

        // 4. map DTO
        TodayAttendanceDTO dto = new TodayAttendanceDTO();
        dto.setSessionId(session.getId());
        dto.setDate(date.toString()); // dùng date truyền vào
        dto.setTeacherStatus(teacherStatus);

        dto.setTotalStudents(total);
        dto.setPresentStudents(present);
        dto.setAbsentStudents(absent);
        dto.setLateStudents(late);

        return dto;
    }
}
