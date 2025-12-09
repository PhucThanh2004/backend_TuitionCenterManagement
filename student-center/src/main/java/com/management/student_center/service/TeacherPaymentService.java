package com.management.student_center.service;

import com.management.student_center.dto.payment.SalaryCalculationDTO;
import com.management.student_center.entity.*;
import com.management.student_center.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherPaymentService {

    private final TeacherRepository teacherRepository;
    private final TeacherPaymentRepository teacherPaymentRepository;
    private final TeacherPaymentDetailRepository teacherPaymentDetailRepository;
    private final SessionRepository sessionRepository;
    private final SubjectRepository subjectRepository; // Cần để map entity detail

    public TeacherPaymentService(TeacherRepository teacherRepository,
                                 TeacherPaymentRepository teacherPaymentRepository,
                                 TeacherPaymentDetailRepository teacherPaymentDetailRepository,
                                 SessionRepository sessionRepository,
                                 SubjectRepository subjectRepository) {
        this.teacherRepository = teacherRepository;
        this.teacherPaymentRepository = teacherPaymentRepository;
        this.teacherPaymentDetailRepository = teacherPaymentDetailRepository;
        this.sessionRepository = sessionRepository;
        this.subjectRepository = subjectRepository;
    }

    /**
     * 🧩 Tính lương giáo viên trong 1 tháng (Logic nội bộ)
     */
    public List<SalaryCalculationDTO> calculateTeacherSalaryByMonth(int month, int year) {
        // 1. Xác định ngày đầu và cuối tháng
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        // 2. Lấy giáo viên (Role R1) và các môn dạy
        // Lưu ý: Trong TeacherService ta đã có logic lấy TeacherSubject, ở đây dùng lại Entity Teacher
        List<Teacher> teachers = teacherRepository.findAll(); 
        // Cần lọc lại chỉ lấy R1 trong code hoặc viết Query riêng trong Repo nếu muốn tối ưu
        // Ở đây giả định Teacher entity đã liên kết đúng User

        List<SalaryCalculationDTO> results = new ArrayList<>();

        for (Teacher teacher : teachers) {
            // Kiểm tra role (nếu query findAll lấy hết)
            if (teacher.getUserInfo() == null || !"R1".equals(teacher.getUserInfo().getRoleId())) {
                continue;
            }

            SalaryCalculationDTO dto = new SalaryCalculationDTO();
            dto.setTeacherId(teacher.getId());
            dto.setFullName(teacher.getUserInfo().getFullName());
            dto.setEmail(teacher.getUserInfo().getEmail());
            dto.setPhoneNumber(teacher.getUserInfo().getPhoneNumber());
            
            double totalAmountDouble = 0.0;

            // Duyệt từng môn giáo viên dạy
            for (TeacherSubject ts : teacher.getTeacherSubjects()) {
                // Bỏ qua môn không active nếu cần (tùy logic Entity Subject)
                
                // 3. Tìm các session hợp lệ
                List<Session> sessions = sessionRepository.findValidSessionsForSalary(
                        teacher.getId(),
                        ts.getSubject().getId(),
                        startOfMonth,
                        endOfMonth
                );

                if (sessions.isEmpty()) continue;

                double totalHours = 0.0;
                for (Session s : sessions) {
                    // Tính giờ: (EndTime - StartTime)
                    Duration duration = Duration.between(s.getStartTime(), s.getEndTime());
                    totalHours += (double) duration.toMinutes() / 60.0;
                }

                // Tính tiền: giờ * lương
                // Lưu ý: Entity TeacherSubject dùng BigDecimal cho salaryRate, PaymentDetail dùng Float
                // Cần ép kiểu cẩn thận
                double salaryRate = ts.getSalaryRate().doubleValue();
                double totalMoney = totalHours * salaryRate;

                SalaryCalculationDTO.SubjectSalaryDTO subjDTO = new SalaryCalculationDTO.SubjectSalaryDTO();
                subjDTO.setSubjectId(ts.getSubject().getId());
                subjDTO.setSubjectName(ts.getSubject().getName());
                subjDTO.setSalaryRate((float) salaryRate);
                subjDTO.setTotalSessions(sessions.size());
                subjDTO.setTotalHours((float) totalHours);
                subjDTO.setTotalMoney((float) totalMoney);

                dto.getSubjects().add(subjDTO);
                totalAmountDouble += totalMoney;
            }

            // Chỉ thêm vào danh sách nếu có dạy môn nào đó (hoặc tổng tiền > 0)
            if (!dto.getSubjects().isEmpty()) {
                dto.setTotalAmount(BigDecimal.valueOf(totalAmountDouble));
                results.add(dto);
            }
        }
        return results;
    }

    /**
     * 🧩 Tạo bảng lương (Lưu vào DB)
     */
    @Transactional
    public List<TeacherPayment> createTeacherPayments(int month, int year, String notes) {
        String noteIdentifier = String.format("Lương tháng %d/%d", month, year);

        // 1. Kiểm tra tồn tại
        List<TeacherPayment> existing = teacherPaymentRepository.findByNotesContaining(noteIdentifier);
        if (!existing.isEmpty()) {
            throw new RuntimeException("Bảng lương cho tháng " + month + "/" + year + " đã được tạo trước đó.");
        }

        // 2. Tính lương
        List<SalaryCalculationDTO> salaries = calculateTeacherSalaryByMonth(month, year);
        List<TeacherPayment> savedPayments = new ArrayList<>();

        for (SalaryCalculationDTO sal : salaries) {
            // Tạo Payment Header
            TeacherPayment payment = new TeacherPayment();
            Teacher teacherRef = teacherRepository.findById(sal.getTeacherId()).orElse(null);
            
            payment.setTeacher(teacherRef);
            payment.setAmount(sal.getTotalAmount());
            payment.setPaymentDate(LocalDate.now());
            payment.setStatus("unpaid");
            payment.setNotes(noteIdentifier + ". " + (notes != null ? notes : ""));
            
            TeacherPayment savedPayment = teacherPaymentRepository.save(payment);

            // Tạo Payment Details
            for (SalaryCalculationDTO.SubjectSalaryDTO sub : sal.getSubjects()) {
                TeacherPaymentDetail detail = new TeacherPaymentDetail();
                detail.setPayment(savedPayment);
                detail.setSubject(subjectRepository.findById(sub.getSubjectId()).orElse(null));
                detail.setTotalHours(sub.getTotalHours());
                detail.setTotalSessions(sub.getTotalSessions());
                detail.setSalaryRate(sub.getSalaryRate());
                detail.setTotalMoney(sub.getTotalMoney());
                
                teacherPaymentDetailRepository.save(detail);
            }
            savedPayments.add(savedPayment);
        }
        return savedPayments;
    }

    /**
     * 🧩 Thanh toán lương
     */
    @Transactional
    public TeacherPayment payTeacherSalary(Long teacherId, int month, int year) {
        String noteIdentifier = String.format("Lương tháng %d/%d", month, year);
        
        TeacherPayment payment = teacherPaymentRepository.findByTeacherIdAndNotesContaining(teacherId, noteIdentifier)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bảng lương!"));

        if ("paid".equals(payment.getStatus())) {
            throw new RuntimeException("Bảng lương đã được thanh toán!");
        }

        payment.setStatus("paid");
        payment.setPaymentDate(LocalDate.now());
        return teacherPaymentRepository.save(payment);
    }

    /**
     * 🧩 Lấy chi tiết lương (cho Frontend xem)
     */
    public TeacherPayment getTeacherSalaryDetail(Long teacherId, int month, int year) {
        String noteIdentifier = String.format("Lương tháng %d/%d", month, year);
        
        // JPA tự động fetch Eager hoặc Lazy tùy cấu hình, 
        // nhưng ta trả về Entity thì Jackson sẽ lo việc serialize
        return teacherPaymentRepository.findByTeacherIdAndNotesContaining(teacherId, noteIdentifier)
                .orElse(null); // Hoặc throw exception tùy ý
    }
    
    public List<TeacherPayment> getPaymentsByMonth(int month, int year) {
        String noteIdentifier = String.format("Lương tháng %d/%d", month, year);
        
        // Sử dụng lại hàm repository đã có: findByNotesContaining
        return teacherPaymentRepository.findByNotesContaining(noteIdentifier);
    }
}