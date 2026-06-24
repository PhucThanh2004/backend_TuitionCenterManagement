package com.management.student_center.service;

import com.management.student_center.dto.teacherSuggestion.TeacherSuggestionDTO;
import com.management.student_center.dto.teacherSuggestion.TeacherSuggestionRequest;
import com.management.student_center.dto.teacherSuggestion.TimeSlot;
import com.management.student_center.entity.*;
import com.management.student_center.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherSuggestionService {

    private static final Logger log = LoggerFactory.getLogger(TeacherSuggestionService.class);
    
    private final TeacherRepository teacherRepository;
    private final TeacherSubjectRepository teacherSubjectRepository;
    private final SessionRepository sessionRepository;
    private final StudentSubjectRepository studentSubjectRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public TeacherSuggestionService(
            TeacherRepository teacherRepository,
            TeacherSubjectRepository teacherSubjectRepository,
            SessionRepository sessionRepository,
            StudentSubjectRepository studentSubjectRepository) {
        this.teacherRepository = teacherRepository;
        this.teacherSubjectRepository = teacherSubjectRepository;
        this.sessionRepository = sessionRepository;
        this.studentSubjectRepository = studentSubjectRepository;
    }

    /**
     * Gợi ý giáo viên phù hợp nhất dựa trên các tiêu chí
     */
    @Transactional(readOnly = true)
    public List<TeacherSuggestionDTO> suggestTeachers(TeacherSuggestionRequest request) {
        log.info("Bắt đầu gợi ý giáo viên cho môn: {}, khối: {}", request.getSubjectName(), request.getGrade());
        
        // 1. Lấy tất cả giáo viên
        List<Teacher> allTeachers = teacherRepository.findAll();
        
        if (allTeachers.isEmpty()) {
            log.warn("Không có giáo viên nào trong hệ thống");
            return new ArrayList<>();
        }
        
        // 2. Tính điểm cho từng giáo viên
        List<TeacherSuggestionDTO> suggestions = new ArrayList<>();
        
        for (Teacher teacher : allTeachers) {
            try {
                TeacherSuggestionDTO dto = calculateTeacherScore(teacher, request);
                if (dto.getMatchScore() > 0) {
                    suggestions.add(dto);
                }
            } catch (Exception e) {
                log.error("Lỗi khi tính điểm cho giáo viên {}: {}", teacher.getId(), e.getMessage());
            }
        }
        
        // 3. Sắp xếp theo điểm giảm dần và giới hạn số lượng
        List<TeacherSuggestionDTO> result = suggestions.stream()
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .limit(request.getLimit())
                .collect(Collectors.toList());
        
        log.info("Đã gợi ý {} giáo viên", result.size());
        return result;
    }

    /**
     * Tính điểm phù hợp cho một giáo viên
     */
    private TeacherSuggestionDTO calculateTeacherScore(Teacher teacher, TeacherSuggestionRequest request) {
        TeacherSuggestionDTO dto = new TeacherSuggestionDTO();
        
        // === THÔNG TIN CƠ BẢN ===
        dto.setTeacherId(teacher.getId());
        dto.setSpecialty(teacher.getSpecialty() != null ? teacher.getSpecialty() : "Chưa cập nhật");
        
        if (teacher.getUserInfo() != null) {
            dto.setFullName(teacher.getUserInfo().getFullName());
            dto.setGender(teacher.getUserInfo().getGender());
            dto.setImage(teacher.getUserInfo().getImage());
            dto.setPhoneNumber(teacher.getUserInfo().getPhoneNumber());
            dto.setEmail(teacher.getUserInfo().getEmail());
        } else {
            dto.setFullName("Giáo viên " + teacher.getId());
        }
        
        double totalScore = 0;
        List<String> strengths = new ArrayList<>();
        
        // === TIÊU CHÍ 1: Chuyên môn phù hợp (tối đa 40 điểm) ===
        double specialtyScore = calculateSpecialtyScore(teacher, request);
        totalScore += specialtyScore;
        if (specialtyScore >= 35) {
            strengths.add("✓ Chuyên môn xuất sắc: " + teacher.getSpecialty());
        } else if (specialtyScore >= 25) {
            strengths.add("✓ Chuyên môn phù hợp với môn " + request.getSubjectName());
        }
        
        // === TIÊU CHÍ 2: Kinh nghiệm dạy khối lớp (tối đa 20 điểm) ===
        double experienceScore = calculateGradeExperienceScore(teacher, request);
        totalScore += experienceScore;
        if (experienceScore >= 15) {
            strengths.add("✓ Có nhiều kinh nghiệm dạy khối " + request.getGrade());
        } else if (experienceScore >= 10) {
            strengths.add("✓ Có kinh nghiệm dạy khối " + request.getGrade());
        }
        
        // === TIÊU CHÍ 3: Lịch trống (tối đa 25 điểm) ===
        AvailabilityInfo availabilityInfo = calculateAvailabilityScore(teacher, request);
        totalScore += availabilityInfo.getScore();
        dto.setAvailableSlots(availabilityInfo.getAvailableSlots());
        if (availabilityInfo.getScore() >= 20) {
            strengths.add("✓ Lịch dạy rất linh hoạt, nhiều khung giờ trống");
        } else if (availabilityInfo.getScore() >= 15) {
            strengths.add("✓ Lịch dạy linh hoạt");
        }
        
        // === TIÊU CHÍ 4: Số lượng học sinh đã dạy (tối đa 10 điểm) ===
        Integer totalStudents = getTeacherTotalStudents(teacher);
        dto.setTotalStudents(totalStudents);
        double studentCountScore = calculateStudentCountScore(teacher);
        totalScore += studentCountScore;
        if (studentCountScore >= 8 && totalStudents != null) {
            strengths.add("✓ Đã dạy " + totalStudents + " học sinh, giàu kinh nghiệm thực tế");
        } else if (studentCountScore >= 5 && totalStudents != null) {
            strengths.add("✓ Đã dạy " + totalStudents + " học sinh");
        }
        
        // === TIÊU CHÍ 5: Độ đa dạng môn học (tối đa 5 điểm) ===
        double diversityScore = calculateSubjectDiversityScore(teacher);
        totalScore += diversityScore;
        if (diversityScore >= 4) {
            strengths.add("✓ Có khả năng dạy đa dạng các môn học");
        }
        
        // === TÍNH ĐIỂM CUỐI CÙNG ===
        dto.setMatchScore((double) Math.min(Math.round(totalScore), 100));
        dto.setStrengths(strengths);
        dto.setReason(generateReason(dto, request));
        dto.setTotalSubjects(getTeacherTotalSubjects(teacher));
        
        log.debug("Giáo viên {}: điểm={}/100", dto.getFullName(), dto.getMatchScore());
        
        return dto;
    }
    
    // ==================== CÁC HÀM TÍNH ĐIỂM CHI TIẾT ====================
    
    /**
     * 1. Tính điểm chuyên môn (40 điểm)
     */
    private double calculateSpecialtyScore(Teacher teacher, TeacherSuggestionRequest request) {
        if (teacher.getSpecialty() == null || request.getSubjectName() == null) {
            return 0;
        }
        
        String specialty = teacher.getSpecialty().toLowerCase().trim();
        String subjectName = request.getSubjectName().toLowerCase().trim();
        
        // Danh sách từ khóa và điểm tương ứng
        Map<String, Integer> keywordScores = new HashMap<>();
        keywordScores.put("toán", 40);
        keywordScores.put("toan", 40);
        keywordScores.put("math", 40);
        keywordScores.put("mathematics", 40);
        keywordScores.put("lý", 35);
        keywordScores.put("ly", 35);
        keywordScores.put("physics", 35);
        keywordScores.put("hóa", 35);
        keywordScores.put("hoa", 35);
        keywordScores.put("chemistry", 35);
        keywordScores.put("văn", 35);
        keywordScores.put("van", 35);
        keywordScores.put("literature", 35);
        keywordScores.put("anh", 35);
        keywordScores.put("english", 35);
        keywordScores.put("sinh", 30);
        keywordScores.put("biology", 30);
        keywordScores.put("sử", 30);
        keywordScores.put("su", 30);
        keywordScores.put("history", 30);
        keywordScores.put("địa", 30);
        keywordScores.put("dia", 30);
        keywordScores.put("geography", 30);
        
        // Kiểm tra khớp chính xác
        if (specialty.contains(subjectName)) {
            return 40;
        }
        
        // Kiểm tra từ khóa
        for (Map.Entry<String, Integer> entry : keywordScores.entrySet()) {
            if (specialty.contains(entry.getKey()) && subjectName.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return 15;
    }
    
    /**
     * 2. Tính điểm kinh nghiệm dạy khối lớp (20 điểm)
     */
    private double calculateGradeExperienceScore(Teacher teacher, TeacherSuggestionRequest request) {
        List<TeacherSubject> teacherSubjects = teacherSubjectRepository.findByTeacherId(teacher.getId());
        
        if (teacherSubjects.isEmpty()) {
            return 0;
        }
        
        int gradeMatchCount = 0;
        String targetGrade = request.getGrade();
        
        for (TeacherSubject ts : teacherSubjects) {
            Subject subject = ts.getSubject();
            if (subject != null && subject.getGrade() != null) {
                String taughtGrade = subject.getGrade();
                
                if (taughtGrade.equals(targetGrade)) {
                    gradeMatchCount += 2;
                } else if (isAdjacentGrade(taughtGrade, targetGrade)) {
                    gradeMatchCount += 1;
                }
            }
        }
        
        return Math.min(gradeMatchCount * 2, 20);
    }
    
    private boolean isAdjacentGrade(String grade1, String grade2) {
        try {
            if (isNumeric(grade1) && isNumeric(grade2)) {
                int g1 = Integer.parseInt(grade1);
                int g2 = Integer.parseInt(grade2);
                return Math.abs(g1 - g2) == 1;
            }
        } catch (NumberFormatException e) {
            // Không xử lý
        }
        return false;
    }
    
    private boolean isNumeric(String str) {
        if (str == null) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 3. Tính điểm dựa trên lịch trống (25 điểm)
     */
    private AvailabilityInfo calculateAvailabilityScore(Teacher teacher, TeacherSuggestionRequest request) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusWeeks(2);
        
        List<Session> teacherSessions = sessionRepository.findTeacherSessionsInDateRange(
            teacher.getId(), startDate, endDate
        );
        
        AvailabilityInfo info = new AvailabilityInfo();
        
        if (request.getPreferredTimeSlots() != null && !request.getPreferredTimeSlots().isEmpty()) {
            int matchedSlots = 0;
            
            for (TimeSlot preferredSlot : request.getPreferredTimeSlots()) {
                boolean isFree = isTeacherFreeAtTimeSlot(teacherSessions, preferredSlot, startDate, endDate);
                if (isFree) {
                    matchedSlots++;
                }
            }
            
            info.setAvailableSlots(matchedSlots);
            double matchRate = (double) matchedSlots / request.getPreferredTimeSlots().size();
            info.setScore(matchRate * 25);
        } else {
            int totalFreeSlots = calculateTotalFreeSlots(teacherSessions, startDate, endDate);
            info.setAvailableSlots(totalFreeSlots);
            info.setScore(Math.min((double) totalFreeSlots / 20 * 25, 25));
        }
        
        return info;
    }
    
    private boolean isTeacherFreeAtTimeSlot(List<Session> sessions, TimeSlot slot, LocalDate startDate, LocalDate endDate) {
        LocalTime start = LocalTime.parse(slot.getStartTime(), TIME_FORMATTER);
        LocalTime end = LocalTime.parse(slot.getEndTime(), TIME_FORMATTER);
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            int dayOfWeek = currentDate.getDayOfWeek().getValue();
            
            if (dayOfWeek == slot.getDayOfWeek()) {
                // Tạo biến final để dùng trong lambda
                final LocalDate checkDate = currentDate;
                boolean hasConflict = sessions.stream().anyMatch(session -> 
                    session.getSessionDate().equals(checkDate) &&
                    isTimeOverlap(session.getStartTime(), session.getEndTime(), start, end)
                );
                
                if (!hasConflict) {
                    return true;
                }
            }
            currentDate = currentDate.plusDays(1);
        }
        return false;
    }
    
    private boolean isTimeOverlap(LocalTime t1Start, LocalTime t1End, LocalTime t2Start, LocalTime t2End) {
        return !(t1End.isBefore(t2Start) || t1Start.isAfter(t2End));
    }
    
    private int calculateTotalFreeSlots(List<Session> sessions, LocalDate startDate, LocalDate endDate) {
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        int totalPossibleSlots = (int) totalDays * 3;
        int occupiedSlots = sessions.size();
        return Math.max(0, totalPossibleSlots - occupiedSlots);
    }
    
    /**
     * 4. Tính điểm dựa trên số lượng học sinh đã dạy (10 điểm)
     */
    private double calculateStudentCountScore(Teacher teacher) {
        Integer totalStudents = getTeacherTotalStudents(teacher);
        
        if (totalStudents == null || totalStudents == 0) return 0;
        if (totalStudents >= 100) return 10;
        if (totalStudents >= 50) return 8;
        if (totalStudents >= 30) return 6;
        if (totalStudents >= 15) return 4;
        if (totalStudents >= 5) return 2;
        return 1;
    }
    
    /**
     * 5. Tính điểm đa dạng môn học (5 điểm)
     */
    private double calculateSubjectDiversityScore(Teacher teacher) {
        List<TeacherSubject> teacherSubjects = teacherSubjectRepository.findByTeacherId(teacher.getId());
        
        if (teacherSubjects.isEmpty()) return 0;
        
        long uniqueSubjects = teacherSubjects.stream()
                .map(ts -> ts.getSubject())
                .filter(Objects::nonNull)
                .map(Subject::getId)
                .distinct()
                .count();
        
        if (uniqueSubjects >= 5) return 5;
        if (uniqueSubjects >= 3) return 3;
        if (uniqueSubjects >= 2) return 2;
        return 1;
    }
    
    // ==================== CÁC HÀM LẤY DỮ LIỆU THỐNG KÊ ====================
    
    private Integer getTeacherTotalSubjects(Teacher teacher) {
        List<TeacherSubject> teacherSubjects = teacherSubjectRepository.findByTeacherId(teacher.getId());
        return (int) teacherSubjects.stream()
                .map(TeacherSubject::getSubject)
                .filter(Objects::nonNull)
                .count();
    }
    
    private Integer getTeacherTotalStudents(Teacher teacher) {
        List<TeacherSubject> teacherSubjects = teacherSubjectRepository.findByTeacherId(teacher.getId());
        
        Set<Long> uniqueStudents = new HashSet<>();
        for (TeacherSubject ts : teacherSubjects) {
            Subject subject = ts.getSubject();
            if (subject != null && subject.getStudentSubjects() != null) {
                for (StudentSubject ss : subject.getStudentSubjects()) {
                    if (ss != null && ss.getStudent() != null && ss.getStudent().getId() != null) {
                        uniqueStudents.add(ss.getStudent().getId());
                    }
                }
            }
        }
        
        return uniqueStudents.size();
    }
    
    private String generateReason(TeacherSuggestionDTO dto, TeacherSuggestionRequest request) {
        List<String> reasons = new ArrayList<>();
        
        if (dto.getMatchScore() >= 90) {
            reasons.add("Rất phù hợp với yêu cầu của lớp học");
        } else if (dto.getMatchScore() >= 70) {
            reasons.add("Phù hợp tốt với yêu cầu của lớp học");
        } else if (dto.getMatchScore() >= 50) {
            reasons.add("Có thể đáp ứng được yêu cầu cơ bản");
        } else {
            reasons.add("Phù hợp ở mức độ trung bình");
        }
        
        if (dto.getAvailableSlots() != null && dto.getAvailableSlots() > 0) {
            if (request.getPreferredTimeSlots() != null && !request.getPreferredTimeSlots().isEmpty()) {
                reasons.add("Có " + dto.getAvailableSlots() + "/" + request.getPreferredTimeSlots().size() + 
                           " khung giờ mong muốn còn trống trong 2 tuần");
            } else {
                reasons.add("Còn " + dto.getAvailableSlots() + " khung giờ trống trong 2 tuần tới");
            }
        }
        
        if (dto.getTotalStudents() != null && dto.getTotalStudents() > 0) {
            reasons.add("Đã từng dạy " + dto.getTotalStudents() + " học sinh");
        }
        
        if (dto.getTotalSubjects() != null && dto.getTotalSubjects() > 0) {
            reasons.add("Kinh nghiệm dạy " + dto.getTotalSubjects() + " môn học khác nhau");
        }
        
        return String.join(". ", reasons);
    }
    
    // ==================== INNER CLASS ====================
    
    private static class AvailabilityInfo {
        private double score;
        private int availableSlots;
        
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        public int getAvailableSlots() { return availableSlots; }
        public void setAvailableSlots(int availableSlots) { this.availableSlots = availableSlots; }
    }
}