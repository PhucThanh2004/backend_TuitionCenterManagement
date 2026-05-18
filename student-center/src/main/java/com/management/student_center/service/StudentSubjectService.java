package com.management.student_center.service;

import com.management.student_center.dto.StudentDTO;
import com.management.student_center.entity.StudentSubject;
import com.management.student_center.entity.Student;
import com.management.student_center.entity.Subject;
import com.management.student_center.repository.StudentSubjectRepository;
import com.management.student_center.repository.StudentTuitionDetailRepository;
import com.management.student_center.repository.StudentRepository;
import com.management.student_center.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentSubjectService {

    private final StudentSubjectRepository studentSubjectRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    private final StudentTuitionDetailRepository studentTuitionDetailRepository;

    public StudentSubjectService(
            StudentSubjectRepository studentSubjectRepository,
            StudentRepository studentRepository,
            SubjectRepository subjectRepository,
            StudentTuitionDetailRepository studentTuitionDetailRepository
    ) {
        this.studentSubjectRepository = studentSubjectRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.studentTuitionDetailRepository = studentTuitionDetailRepository;
    }


    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsBySubjectId(Long subjectId) {
        List<StudentSubject> ssList = studentSubjectRepository.findActiveBySubjectId(subjectId);

        return ssList.stream().map(ss -> {
            Student s = ss.getStudent();
            return new StudentDTO(
                    s.getId(),
                    s.getUserInfo() != null ? s.getUserInfo().getFullName() : "Chưa có tên",
                    s.getUserInfo() != null ? s.getUserInfo().getGender() : null,
                    s.getDateOfBirth(),
                    s.getSchoolName(),
                    s.getGrade()
            );
        }).collect(Collectors.toList());
    }

    /*@Transactional
    public StudentSubject addStudentToSubject(Long studentId, Long subjectId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy học sinh"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy môn học"));

        if (studentSubjectRepository.findByStudentIdAndSubjectId(studentId, subjectId).isPresent()) {
            throw new IllegalArgumentException("Học sinh đã được thêm vào môn học này");
        }

        StudentSubject ss = new StudentSubject();
        ss.setStudent(student);
        ss.setSubject(subject);
        ss.setEnrollmentDate(java.time.LocalDate.now());

        return studentSubjectRepository.save(ss);
    }*/
    
    @Transactional
    public StudentSubject addStudentToSubject(Long studentId, Long subjectId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy học sinh"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy môn học"));

        // Kiểm tra xem đã có bản ghi nào chưa
        Optional<StudentSubject> existingRecord = studentSubjectRepository
                .findByStudentIdAndSubjectId(studentId, subjectId);

        if (existingRecord.isPresent()) {
            StudentSubject ssExisting = existingRecord.get();
            
            // Nếu bản ghi đã bị xóa mềm (deletedAt != null)
            if (ssExisting.getDeletedAt() != null) {
                // Tạo bản ghi mới thay vì tái sử dụng bản ghi cũ
                StudentSubject ss = new StudentSubject();
                ss.setStudent(student);
                ss.setSubject(subject);
                ss.setEnrollmentDate(java.time.LocalDate.now());
                ss.setDeletedAt(null); // Đảm bảo bản ghi mới chưa bị xóa
                return studentSubjectRepository.save(ss);
            } else {
                // Bản ghi đang active (chưa xóa)
                throw new IllegalArgumentException("Học sinh đã được thêm vào môn học này");
            }
        }

        // Chưa có bản ghi nào, tạo mới
        StudentSubject ss = new StudentSubject();
        ss.setStudent(student);
        ss.setSubject(subject);
        ss.setEnrollmentDate(java.time.LocalDate.now());
        ss.setDeletedAt(null);

        return studentSubjectRepository.save(ss);
    }

    /*@Transactional
    public void removeStudentFromSubject(Long studentId, Long subjectId) {

        // 1. Kiểm tra học sinh có còn nợ học phí môn này không
        long unpaidCount = studentTuitionDetailRepository
                .countUnpaidByStudentAndSubject(studentId, subjectId);

        if (unpaidCount > 0) {
            throw new IllegalStateException(
                    "Không thể xóa học sinh khỏi môn học vì còn nợ học phí"
            );
        }

        // 2. Kiểm tra tồn tại quan hệ Student - Subject
        StudentSubject ss = studentSubjectRepository
                .findByStudentIdAndSubjectId(studentId, subjectId)
                .orElseThrow(() ->
                        new NoSuchElementException("Không tìm thấy học sinh trong môn học này")
                );

        // 3. Xóa quan hệ
        studentSubjectRepository.delete(ss);
    }*/
    
    @Transactional
    public void removeStudentFromSubject(Long studentId, Long subjectId) {

        // Kiểm tra tồn tại quan hệ Student - Subject (chỉ lấy bản ghi chưa xóa)
        StudentSubject ss = studentSubjectRepository
                .findByStudentIdAndSubjectId(studentId, subjectId)
                .filter(record -> record.getDeletedAt() == null)
                .orElseThrow(() ->
                        new NoSuchElementException("Không tìm thấy học sinh trong môn học này hoặc học sinh đã bị xóa trước đó")
                );

        // Xóa mềm - chỉ cập nhật deletedAt
        ss.setDeletedAt(java.time.LocalDate.now());
        studentSubjectRepository.save(ss);
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsByGrade(String grade) {
        List<Student> students = studentRepository.findByGrade(grade);

        return students.stream().map(s -> new StudentDTO(
                s.getId(),
                s.getUserInfo() != null ? s.getUserInfo().getFullName() : "Chưa có tên",
                s.getUserInfo() != null ? s.getUserInfo().getGender() : null,
                s.getDateOfBirth(),
                s.getSchoolName(),
                s.getGrade()
        )).collect(Collectors.toList());
    }
}
