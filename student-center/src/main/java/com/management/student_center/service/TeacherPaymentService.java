package com.management.student_center.service;

import com.management.student_center.dto.payment.SalaryCalculationDTO;
import com.management.student_center.dto.payment.TeacherPaymentDetailUpdateRequest; // DTO mới
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

@Service
public class TeacherPaymentService {

    private final TeacherRepository teacherRepository;
    private final TeacherPaymentRepository teacherPaymentRepository;
    private final TeacherPaymentDetailRepository teacherPaymentDetailRepository;
    private final SessionRepository sessionRepository;
    private final SubjectRepository subjectRepository;

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

    // --- PHẦN 1: TÍNH TOÁN LOGIC (Giữ nguyên logic cũ nhưng trả về DTO) ---
    public List<SalaryCalculationDTO> calculateTeacherSalaryByMonth(int month, int year) {
        // ... (Giữ nguyên code tính toán của bạn ở đây)
        // Lưu ý: Đoạn logic tính toán của bạn đã ổn.
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        List<Teacher> teachers = teacherRepository.findAll();
        List<SalaryCalculationDTO> results = new ArrayList<>();

        for (Teacher teacher : teachers) {
            if (teacher.getUserInfo() == null || !"R1".equals(teacher.getUserInfo().getRoleId())) continue;

            SalaryCalculationDTO dto = new SalaryCalculationDTO();
            dto.setTeacherId(teacher.getId());
            dto.setFullName(teacher.getUserInfo().getFullName());
            dto.setEmail(teacher.getUserInfo().getEmail());
            dto.setPhoneNumber(teacher.getUserInfo().getPhoneNumber());

            BigDecimal totalAmountBD = BigDecimal.ZERO;

            for (TeacherSubject ts : teacher.getTeacherSubjects()) {
                List<Session> sessions = sessionRepository.findValidSessionsForSalary(
                        teacher.getId(), ts.getSubject().getId(), startOfMonth, endOfMonth);

                if (sessions.isEmpty()) continue;

                double totalHours = 0.0;
                for (Session s : sessions) {
                    Duration duration = Duration.between(s.getStartTime(), s.getEndTime());
                    totalHours += (double) duration.toMinutes() / 60.0;
                }

                BigDecimal hoursBD = BigDecimal.valueOf(totalHours);
                BigDecimal salaryRateBD = ts.getSalaryRate() != null ? ts.getSalaryRate() : BigDecimal.ZERO;
                BigDecimal totalMoneyBD = hoursBD.multiply(salaryRateBD).setScale(0, RoundingMode.HALF_UP);

                SalaryCalculationDTO.SubjectSalaryDTO subjDTO = new SalaryCalculationDTO.SubjectSalaryDTO();
                subjDTO.setSubjectId(ts.getSubject().getId());
                subjDTO.setSubjectName(ts.getSubject().getName());
                subjDTO.setSalaryRate(salaryRateBD);
                subjDTO.setTotalSessions(sessions.size());
                subjDTO.setTotalHours((float) totalHours);
                subjDTO.setTotalMoney(totalMoneyBD);

                dto.getSubjects().add(subjDTO);
                totalAmountBD = totalAmountBD.add(totalMoneyBD);
            }

            if (!dto.getSubjects().isEmpty()) {
                dto.setTotalAmount(totalAmountBD);
                results.add(dto);
            }
        }
        return results;
    }

    // --- PHẦN 2: TẠO BẢNG LƯƠNG (CREATE) ---
    @Transactional
    public List<TeacherPayment> createTeacherPayments(int month, int year, String notes) {
        String noteIdentifier = String.format("Lương tháng %d/%d", month, year);

        List<SalaryCalculationDTO> salaries = calculateTeacherSalaryByMonth(month, year);
        List<TeacherPayment> savedPayments = new ArrayList<>();

        for (SalaryCalculationDTO sal : salaries) {
            // Check trùng bằng month/year thay vì notes
            boolean exists = teacherPaymentRepository.existsByTeacherIdAndMonthAndYear(sal.getTeacherId(), month, year);
            if (exists) continue;

            TeacherPayment payment = new TeacherPayment();
            Teacher teacherRef = teacherRepository.findById(sal.getTeacherId()).orElse(null);

            payment.setTeacher(teacherRef);
            payment.setMonth(month); // SET MONTH
            payment.setYear(year);   // SET YEAR
            
            payment.setAmount(sal.getTotalAmount());
            payment.setPaidAmount(BigDecimal.ZERO); // Mặc định đã trả = 0
            
            payment.setPaymentDate(LocalDate.now());
            payment.setStatus("unpaid");
            payment.setNotes(noteIdentifier + (notes != null ? ". " + notes : ""));

            List<TeacherPaymentDetail> details = new ArrayList<>();
            for (SalaryCalculationDTO.SubjectSalaryDTO sub : sal.getSubjects()) {
                TeacherPaymentDetail detail = new TeacherPaymentDetail();
                detail.setPayment(payment);
                
                Subject s = new Subject(); s.setId(sub.getSubjectId());
                detail.setSubject(s);

                detail.setTotalHours(sub.getTotalHours());
                detail.setTotalSessions(sub.getTotalSessions());
                detail.setSalaryRate(sub.getSalaryRate());
                detail.setTotalMoney(sub.getTotalMoney());

                details.add(detail);
            }

            payment.setPaymentDetails(details);
            savedPayments.add(teacherPaymentRepository.save(payment));
        }
        return savedPayments;
    }

    // --- PHẦN 3: LẤY DANH SÁCH & LỌC (SEARCH/FILTER) ---
    public List<TeacherPayment> getPaymentsWithFilter(int month, int year, String name, String status) {
        return teacherPaymentRepository.searchPayments(month, year, name, status);
    }

    public TeacherPayment getTeacherSalaryDetail(Long teacherId, int month, int year) {
        return teacherPaymentRepository.findByTeacherIdAndMonthAndYear(teacherId, month, year)
                .orElse(null);
    }

    // --- PHẦN 4: THANH TOÁN (PAYMENT - PARTIAL) ---
    // Hỗ trợ trả từng phần hoặc trả hết 1 cục
    @Transactional
    public TeacherPayment payTeacherSalary(Long paymentId, BigDecimal paymentAmount) {
        TeacherPayment payment = teacherPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bảng lương!"));

        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền thanh toán phải lớn hơn 0");
        }

        // 1. Cộng dồn tiền đã trả
        BigDecimal currentPaid = payment.getPaidAmount() != null ? payment.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal newPaidTotal = currentPaid.add(paymentAmount);

        // 2. Không cho trả dư quá tổng lương (Optional logic)
        if (newPaidTotal.compareTo(payment.getAmount()) > 0) {
            newPaidTotal = payment.getAmount();
        }

        payment.setPaidAmount(newPaidTotal);
        payment.setPaymentDate(LocalDate.now());

        // 3. Cập nhật trạng thái
        BigDecimal remaining = payment.getAmount().subtract(newPaidTotal);

        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            payment.setStatus("paid"); // Hết nợ
        } else if (newPaidTotal.compareTo(BigDecimal.ZERO) > 0) {
            payment.setStatus("partial"); // Trả một phần
        } else {
            payment.setStatus("unpaid");
        }

        return teacherPaymentRepository.save(payment);
    }

    // --- PHẦN 5: CHỈNH SỬA CHI TIẾT (EDIT & AUTO-CALC) ---
    @Transactional
    public TeacherPayment updatePaymentDetail(TeacherPaymentDetailUpdateRequest request) {
        // 1. Tìm chi tiết
        TeacherPaymentDetail detail = teacherPaymentDetailRepository.findById(request.getDetailId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi tiết lương"));

        // A. Nếu sửa số buổi -> Tự động tính lại giờ và tiền
        if (request.getTotalSessions() != null) {
            int oldSessions = detail.getTotalSessions() != null ? detail.getTotalSessions() : 0;
            float oldTotalHours = detail.getTotalHours() != null ? detail.getTotalHours() : 0f;
            int newSessions = request.getTotalSessions();

            // Tính thời lượng trung bình 1 buổi (Hours per session)
            float hoursPerSession = 0;
            if (oldSessions > 0) {
                hoursPerSession = oldTotalHours / oldSessions;
            } else {
                hoursPerSession = 1.5f; // Mặc định nếu dữ liệu cũ bị lỗi
            }

            float newTotalHours = hoursPerSession * newSessions;
            
            detail.setTotalSessions(newSessions);
            detail.setTotalHours(newTotalHours);

            // Tự động tính tiền (New Hours * Rate)
            BigDecimal rate = detail.getSalaryRate() != null ? detail.getSalaryRate() : BigDecimal.ZERO;
            BigDecimal calculatedMoney = BigDecimal.valueOf(newTotalHours)
                                            .multiply(rate)
                                            .setScale(0, RoundingMode.HALF_UP);
            
            detail.setTotalMoney(calculatedMoney);
        }

        // B. Manual Override (Nếu người dùng nhập tiền tay -> Ghi đè)
        if (request.getTotalMoney() != null) {
            detail.setTotalMoney(request.getTotalMoney());
        }

        // C. Update note
        if (request.getNote() != null) {
            detail.setNote(request.getNote());
        }

        // Lưu chi tiết
        teacherPaymentDetailRepository.save(detail);

        // 2. Tính lại tổng tiền cho bảng lương cha (TeacherPayment)
        TeacherPayment parentPayment = detail.getPayment();
        BigDecimal newTotalAmount = BigDecimal.ZERO;

        for (TeacherPaymentDetail d : parentPayment.getPaymentDetails()) {
            if (d.getId().equals(detail.getId())) {
                newTotalAmount = newTotalAmount.add(detail.getTotalMoney());
            } else {
                newTotalAmount = newTotalAmount.add(d.getTotalMoney());
            }
        }
        parentPayment.setAmount(newTotalAmount);

        // 3. Cập nhật trạng thái thanh toán (Paid/Partial/Unpaid)
        BigDecimal paid = parentPayment.getPaidAmount() != null ? parentPayment.getPaidAmount() : BigDecimal.ZERO;
        
        if (paid.compareTo(newTotalAmount) >= 0) {
            parentPayment.setStatus("paid");
        } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
            parentPayment.setStatus("partial");
        } else {
            parentPayment.setStatus("unpaid");
        }

        return teacherPaymentRepository.save(parentPayment);
    }
}