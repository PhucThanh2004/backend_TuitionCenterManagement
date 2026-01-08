package com.management.student_center.service;

import com.management.student_center.dto.teachersubject.TeacherSubjectRequestDTO;
import com.management.student_center.dto.teachersubject.TeacherSubjectResponseDTO;
import com.management.student_center.entity.Subject;
import com.management.student_center.entity.Teacher;
import com.management.student_center.entity.TeacherSubject;
import com.management.student_center.repository.SubjectRepository;
import com.management.student_center.repository.TeacherRepository;
import com.management.student_center.repository.TeacherSubjectRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
@Service
public class TeacherSubjectService {

    private final TeacherSubjectRepository teacherSubjectRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository; // Bạn cần đảm bảo đã có Repo này

    // Lấy baseUrl từ application.properties (ví dụ: http://localhost:8088)
    // Bạn có thể set mặc định nếu không có trong file config
    @Value("${app.base-url:http://localhost:8088}") 
    private String baseUrl;

    public TeacherSubjectService(TeacherSubjectRepository teacherSubjectRepository,
                                 TeacherRepository teacherRepository,
                                 SubjectRepository subjectRepository) {
        this.teacherSubjectRepository = teacherSubjectRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<TeacherSubjectResponseDTO> searchTeacherSubjects(
            Integer grade,
            String teacherName,
            String subjectName
    ) {
        List<TeacherSubject> list = teacherSubjectRepository.findByCriteria(
                grade,
                teacherName,
                subjectName
        );

        return list.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 🧩 Tạo mới thỏa thuận lương
     */
    @Transactional
    public TeacherSubject createTeacherSubject(TeacherSubjectRequestDTO dto) {
        if (dto.getTeacherId() == null || dto.getSubjectId() == null || dto.getSalaryRate() == null) {
            throw new RuntimeException("Thiếu dữ liệu bắt buộc (teacherId, subjectId, salaryRate)");
        }

        // 1. Check trùng lặp
        if (teacherSubjectRepository.existsByTeacherIdAndSubjectId(dto.getTeacherId(), dto.getSubjectId())) {
            throw new RuntimeException("Thỏa thuận đã tồn tại! Giáo viên này đã được phân công dạy môn học này.");
        }

        // 2. Lấy Teacher và Subject từ DB để đảm bảo tồn tại
        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên ID: " + dto.getTeacherId()));
        
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học ID: " + dto.getSubjectId()));

        // 3. Tạo mới
        TeacherSubject record = new TeacherSubject();
        record.setTeacher(teacher);
        record.setSubject(subject);
        record.setSalaryRate(dto.getSalaryRate());
        record.setCreatedAt(java.time.LocalDateTime.now());

        return teacherSubjectRepository.save(record);
    }

    /**
     * 🧩 Cập nhật thỏa thuận lương
     */
    @Transactional
    public TeacherSubject updateTeacherSubject(Long id, TeacherSubjectRequestDTO newData) {
        TeacherSubject record = teacherSubjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thỏa thuận"));

        Long newTeacherId = newData.getTeacherId() != null ? newData.getTeacherId() : record.getTeacher().getId();
        Long newSubjectId = newData.getSubjectId() != null ? newData.getSubjectId() : record.getSubject().getId();

        // 1. Kiểm tra trùng lặp nếu thay đổi khóa (teacherId hoặc subjectId)
        boolean isKeyChanged = !newTeacherId.equals(record.getTeacher().getId()) || !newSubjectId.equals(record.getSubject().getId());
        
        if (isKeyChanged) {
            // Tìm bản ghi khác có cùng teacherId & subjectId
            Optional<TeacherSubject> duplicate = teacherSubjectRepository.findByTeacherIdAndSubjectId(newTeacherId, newSubjectId);
            
            // Nếu tìm thấy VÀ bản ghi tìm thấy KHÔNG PHẢI là bản ghi đang sửa -> Báo lỗi
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                throw new RuntimeException("Cặp Giáo viên - Môn học này đã tồn tại trong thỏa thuận khác.");
            }
            
            // Cập nhật quan hệ nếu thay đổi
            if (newData.getTeacherId() != null) {
                 Teacher newTeacher = teacherRepository.findById(newTeacherId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên"));
                 record.setTeacher(newTeacher);
            }
            if (newData.getSubjectId() != null) {
                Subject newSubject = subjectRepository.findById(newSubjectId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học"));
                record.setSubject(newSubject);
            }
        }

        // 2. Cập nhật lương
        if (newData.getSalaryRate() != null) {
            record.setSalaryRate(newData.getSalaryRate());
        }

        return teacherSubjectRepository.save(record);
    }

    /**
     * 🧩 Xóa thỏa thuận
     */
    @Transactional
    public void deleteTeacherSubject(Long id) {
        if (!teacherSubjectRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy thỏa thuận cần xóa.");
        }
        teacherSubjectRepository.deleteById(id);
    }

    /**
     * 🧩 Lấy danh sách tất cả
     */
    public List<TeacherSubjectResponseDTO> getAllTeacherSubjects() {
        List<TeacherSubject> list = teacherSubjectRepository.findAll();
        // Bạn có thể thêm Sort vào findAll() nếu muốn order by createdAt

        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * 🧩 Lấy chi tiết theo ID
     */
    public TeacherSubjectResponseDTO getTeacherSubjectById(Long id) {
        TeacherSubject record = teacherSubjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thỏa thuận"));
        return mapToDTO(record);
    }

    // === Helper Mapping ===
    private TeacherSubjectResponseDTO mapToDTO(TeacherSubject ts) {
        TeacherSubjectResponseDTO dto = new TeacherSubjectResponseDTO();
        dto.setId(ts.getId());
        
        if (ts.getTeacher() != null) {
            dto.setTeacherId(ts.getTeacher().getId());
            dto.setDateOfBirth(ts.getTeacher().getDateOfBirth());
            dto.setSpecialty(ts.getTeacher().getSpecialty());
            
            if (ts.getTeacher().getUserInfo() != null) {
                dto.setTeacherName(ts.getTeacher().getUserInfo().getFullName());
                dto.setEmail(ts.getTeacher().getUserInfo().getEmail());
                
                // Logic xử lý ảnh avatar
                String imagePath = ts.getTeacher().getUserInfo().getImage();
                if (imagePath != null && !imagePath.isEmpty()) {
                    String fullUrl = baseUrl + (imagePath.startsWith("/") ? "" : "/") + imagePath;
                    dto.setTeacherAvatar(fullUrl);
                }
            }
        } else {
            dto.setTeacherName("Không rõ");
        }

        if (ts.getSubject() != null) {
        	dto.setSubjectId(ts.getSubject().getId());

            dto.setSubjectName(ts.getSubject().getName());
            dto.setGrade(ts.getSubject().getGrade());
        }

        // Format tiền tệ Việt Nam
        if (ts.getSalaryRate() != null) {
            NumberFormat vnFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
            dto.setSalaryRate(vnFormat.format(ts.getSalaryRate()) + " VNĐ/giờ");
        }
        
        if (ts.getCreatedAt() != null) {
            // Format thành: 05/12/2025 14:30
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            dto.setCreatedAt(ts.getCreatedAt().format(formatter));
        } else {
            dto.setCreatedAt("—");
        }

        return dto;
    }
}