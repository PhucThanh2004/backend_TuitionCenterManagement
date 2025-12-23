package com.management.student_center.service;

import com.management.student_center.dto.tuition.TuitionCalculationDTO;
import com.management.student_center.dto.tuition.TuitionDetailUpdateRequest; // Import DTO này
import com.management.student_center.entity.*;
import com.management.student_center.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentTuitionService {

    private final StudentRepository studentRepository;
    private final AttendanceStudentRepository attendanceRepository;
    private final StudentTuitionRepository tuitionRepository;
    private final StudentTuitionDetailRepository detailRepository; // Không bắt buộc nếu dùng Cascade

    public StudentTuitionService(StudentRepository studentRepository,
                                 AttendanceStudentRepository attendanceRepository,
                                 StudentTuitionRepository tuitionRepository,
                                 StudentTuitionDetailRepository detailRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.tuitionRepository = tuitionRepository;
        this.detailRepository = detailRepository;
    }

    /**
     * PHẦN 1: Tính toán logic (Trả về DTO để xem trước hoặc dùng để lưu)
     */
    public List<TuitionCalculationDTO> calculateTuitionByMonth(int month, int year) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        List<Student> students = studentRepository.findAll();
        List<TuitionCalculationDTO> results = new ArrayList<>();

        for (Student student : students) {
            if (student.getUserInfo() == null) continue;

            TuitionCalculationDTO dto = new TuitionCalculationDTO();
            dto.setStudentId(student.getId());
            dto.setFullName(student.getUserInfo().getFullName());
            dto.setPhoneNumber(student.getUserInfo().getPhoneNumber());
            dto.setGrade(student.getGrade());
            BigDecimal totalAmountBD = BigDecimal.ZERO; 

            if (student.getStudentSubjects() != null) {
                for (StudentSubject ss : student.getStudentSubjects()) {
                    Subject subject = ss.getSubject();

                    List<AttendanceStudent> attendances = attendanceRepository.findValidAttendanceForTuition(
                            student.getId(),
                            subject.getId(),
                            startOfMonth,
                            endOfMonth
                    );

                    if (attendances.isEmpty()) continue;

                    double totalHours = 0.0;
                    for (AttendanceStudent ats : attendances) {
                        Session s = ats.getSession();
                        if (s.getStartTime() != null && s.getEndTime() != null) {
                            Duration duration = Duration.between(s.getStartTime(), s.getEndTime());
                            totalHours += (double) duration.toMinutes() / 60.0;
                        }
                    }

                    BigDecimal hoursBD = BigDecimal.valueOf(totalHours);
                    BigDecimal priceBD = subject.getPrice();

                    if (priceBD == null) priceBD = BigDecimal.ZERO;

                    BigDecimal moneyBD = hoursBD.multiply(priceBD).setScale(0, RoundingMode.HALF_UP);

                    TuitionCalculationDTO.SubjectTuitionDTO subjDTO = new TuitionCalculationDTO.SubjectTuitionDTO();
                    subjDTO.setSubjectId(subject.getId());
                    subjDTO.setSubjectName(subject.getName());
                    subjDTO.setHourlyRate(priceBD);
                    subjDTO.setTotalSessions(attendances.size());
                    subjDTO.setTotalHours((float) totalHours);
                    subjDTO.setTotalMoney(moneyBD);

                    dto.getSubjects().add(subjDTO);
                    totalAmountBD = totalAmountBD.add(moneyBD);
                }
            }
            if (!dto.getSubjects().isEmpty()) {
                dto.setTotalAmount(totalAmountBD);
                results.add(dto);
            }
        }
        return results;
    }

    /**
     * PHẦN 2: Lưu vào Database (Create)
     */
    @Transactional
    public List<StudentTuition> createTuitions(int month, int year, String notes) {
        String noteIdentifier = String.format("Học phí tháng %d/%d", month, year);

        List<TuitionCalculationDTO> calculations = calculateTuitionByMonth(month, year);
        List<StudentTuition> savedList = new ArrayList<>();

        for (TuitionCalculationDTO calc : calculations) {
            boolean exists = tuitionRepository.existsByStudentIdAndMonthAndYear(calc.getStudentId(), month, year);
            if (exists) {
                continue;
            }

            StudentTuition tuition = new StudentTuition();
            Student studentRef = studentRepository.findById(calc.getStudentId()).orElse(null);

            tuition.setStudent(studentRef);
            tuition.setMonth(month);
            tuition.setYear(year);
            tuition.setTotalAmount(calc.getTotalAmount());
            tuition.setPaidAmount(BigDecimal.ZERO);
            tuition.setStatus("unpaid");
            tuition.setNotes(noteIdentifier + (notes != null ? ". " + notes : ""));

            List<StudentTuitionDetail> details = new ArrayList<>();
            for (TuitionCalculationDTO.SubjectTuitionDTO subDTO : calc.getSubjects()) {
                StudentTuitionDetail detail = new StudentTuitionDetail();
                
                detail.setStudentTuition(tuition);
                
                Subject subject = new Subject();
                subject.setId(subDTO.getSubjectId());
                detail.setSubject(subject);

                detail.setAttendedSessions(subDTO.getTotalSessions());
                detail.setTotalHours(subDTO.getTotalHours());
                detail.setHourlyRate(subDTO.getHourlyRate());
                detail.setTotalMoney(subDTO.getTotalMoney());

                details.add(detail);
            }

            tuition.setDetails(details);
            savedList.add(tuitionRepository.save(tuition));
        }
        
        return savedList;
    }
    
    /**
     * PHẦN 3 (MỚI): Lấy danh sách kèm Bộ lọc & Phân trang (Optional)
     * Dùng để hiển thị lên bảng quản lý
     */
    public List<TuitionCalculationDTO> getTuitionsWithFilter(int month, int year, String name, String grade, String status) {
        List<StudentTuition> entities = tuitionRepository.searchTuitions(month, year, name, grade, status);

        return entities.stream().map(entity -> {
            TuitionCalculationDTO dto = new TuitionCalculationDTO();
            
            dto.setTuitionId(entity.getId());
            
            dto.setStudentId(entity.getStudent().getId());
            dto.setFullName(entity.getStudent().getUserInfo().getFullName());
            dto.setPhoneNumber(entity.getStudent().getUserInfo().getPhoneNumber());
            dto.setGrade(entity.getStudent().getGrade());
            
            dto.setTotalAmount(entity.getTotalAmount());
            dto.setPaidAmount(entity.getPaidAmount());
            
            dto.setRemainingAmount(entity.getRemainingAmount());
            
            dto.setStatus(entity.getStatus());
            
            return dto;
        }).collect(Collectors.toList());
    }
    
    public List<StudentTuition> getTuitionsByMonth(int month, int year) {
        return tuitionRepository.findByMonthAndYear(month, year);
    }

    /**
     * 4. Lấy chi tiết hóa đơn 1 học sinh
     */
    public StudentTuition getTuitionDetail(Long studentId, int month, int year) {
        return tuitionRepository.findByStudentIdAndMonthAndYear(studentId, month, year)
                .orElse(null);
    }

    /**
     * PHẦN 5 (NÂNG CẤP): Thanh toán từng phần (Partial Payment)
     * @param tuitionId: ID hóa đơn
     * @param paymentAmount: Số tiền khách đóng lần này
     */
    @Transactional
    public StudentTuition payTuition(Long tuitionId, BigDecimal paymentAmount) {
        StudentTuition tuition = tuitionRepository.findById(tuitionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn học phí!"));

        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền đóng phải lớn hơn 0");
        }

        // 1. Tính tổng tiền đã đóng mới
        BigDecimal currentPaid = tuition.getPaidAmount() != null ? tuition.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newPaidTotal = currentPaid.add(paymentAmount);

        // 2. Kiểm tra nếu đóng dư (Optional: Có thể cho phép hoặc chặn)
        // Ở đây mình set trần là TotalAmount (không cho đóng dư hơn tổng nợ)
        if (newPaidTotal.compareTo(tuition.getTotalAmount()) > 0) {
            newPaidTotal = tuition.getTotalAmount(); 
        }

        tuition.setPaidAmount(newPaidTotal);

        // 3. Cập nhật trạng thái dựa trên số nợ còn lại
        BigDecimal remaining = tuition.getTotalAmount().subtract(newPaidTotal);
        
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            tuition.setStatus("paid"); // Hết nợ
        } else if (newPaidTotal.compareTo(BigDecimal.ZERO) > 0) {
            tuition.setStatus("partial"); // Nợ một phần
        } else {
            tuition.setStatus("unpaid");
        }
        
        return tuitionRepository.save(tuition);
    }
    
    
    @Transactional
    public StudentTuition updateTuitionDetail(TuitionDetailUpdateRequest request) {
        // 1. Tìm chi tiết cần sửa
        StudentTuitionDetail detail = detailRepository.findById(request.getDetailId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi tiết học phí"));

        // --- XỬ LÝ LOGIC CẬP NHẬT ---

        // A. Nếu có thay đổi Số buổi (AttendedSessions)
        if (request.getAttendedSessions() != null) {
            int oldSessions = detail.getAttendedSessions();
            float oldTotalHours = detail.getTotalHours();
            int newSessions = request.getAttendedSessions();

            // 1. Tính số giờ trung bình 1 buổi (dựa trên dữ liệu cũ)
            float hoursPerSession = 0;
            if (oldSessions > 0) {
                hoursPerSession = oldTotalHours / oldSessions;
            } else {
                // Nếu cũ = 0, mặc định 1 buổi = 1.5 giờ (hoặc 0 tùy nghiệp vụ)
                hoursPerSession = 1.5f; 
            }

            // 2. Cập nhật Số giờ mới
            float newTotalHours = hoursPerSession * newSessions;
            detail.setAttendedSessions(newSessions);
            detail.setTotalHours(newTotalHours);

            // 3. TỰ ĐỘNG CẬP NHẬT TIỀN (Bước quan trọng bị thiếu)
            // Lấy đơn giá hiện tại
            BigDecimal hourlyRate = detail.getHourlyRate() != null ? detail.getHourlyRate() : BigDecimal.ZERO;
            
            // Tính tiền: Giờ mới * Đơn giá
            BigDecimal calculatedMoney = BigDecimal.valueOf(newTotalHours)
                                            .multiply(hourlyRate)
                                            .setScale(0, RoundingMode.HALF_UP);
            
            // Gán tiền tự động vào trước
            detail.setTotalMoney(calculatedMoney);
        }

        // B. Nếu người dùng CÓ nhập số tiền cụ thể (Manual Override)
        // Logic: Nếu request có gửi totalMoney, ta sẽ dùng nó đè lên số tiền vừa tính tự động ở trên
        // Điều này cho phép trường hợp: Sửa số buổi thành 10, nhưng chốt giá tròn 2 triệu (thay vì 2.1 triệu)
        if (request.getTotalMoney() != null) {
            detail.setTotalMoney(request.getTotalMoney());
        }

        // C. Cập nhật ghi chú
        if (request.getNote() != null) {
            detail.setNote(request.getNote());
        }

        // --- LƯU VÀ TÍNH TỔNG LẠI CHO HÓA ĐƠN CHA ---
        detailRepository.save(detail);

        StudentTuition parentTuition = detail.getStudentTuition();
        BigDecimal newTotalAmount = BigDecimal.ZERO;
        
        // Cộng dồn lại toàn bộ chi tiết để ra tổng mới
        for (StudentTuitionDetail d : parentTuition.getDetails()) {
             // So sánh ID để lấy giá trị mới nhất (vì d trong list có thể là cache cũ)
             if (d.getId().equals(detail.getId())) {
                 newTotalAmount = newTotalAmount.add(detail.getTotalMoney());
             } else {
                 newTotalAmount = newTotalAmount.add(d.getTotalMoney());
             }
        }

        parentTuition.setTotalAmount(newTotalAmount);
        
        // Cập nhật trạng thái thanh toán (Paid/Partial/Unpaid)
        BigDecimal paid = parentTuition.getPaidAmount() != null ? parentTuition.getPaidAmount() : BigDecimal.ZERO;
        
        // Logic so sánh an toàn với BigDecimal
        if (paid.compareTo(newTotalAmount) >= 0) {
            parentTuition.setStatus("paid");
        } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
            parentTuition.setStatus("partial");
        } else {
            parentTuition.setStatus("unpaid");
        }

        return tuitionRepository.save(parentTuition);
    }
}