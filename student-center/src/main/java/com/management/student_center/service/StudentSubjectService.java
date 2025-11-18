package com.management.student_center.service;

import com.management.student_center.dto.StudentDTO;
import com.management.student_center.entity.StudentSubject;
import com.management.student_center.entity.Student;
import com.management.student_center.entity.Subject;
import com.management.student_center.repository.StudentSubjectRepository;
import com.management.student_center.repository.StudentRepository;
import com.management.student_center.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class StudentSubjectService {

    private final StudentSubjectRepository studentSubjectRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public StudentSubjectService(StudentSubjectRepository studentSubjectRepository,
                                 StudentRepository studentRepository,
                                 SubjectRepository subjectRepository) {
        this.studentSubjectRepository = studentSubjectRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentDTO> getStudentsBySubjectId(Long subjectId) {
        List<StudentSubject> ssList = studentSubjectRepository.findBySubjectId(subjectId);

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

    @Transactional
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
    }

    @Transactional
    public void removeStudentFromSubject(Long studentId, Long subjectId) {
        StudentSubject ss = studentSubjectRepository.findByStudentIdAndSubjectId(studentId, subjectId)
                .orElseThrow(() -> new NoSuchElementException("Không tìm thấy học sinh trong môn học này"));
        studentSubjectRepository.delete(ss);
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
