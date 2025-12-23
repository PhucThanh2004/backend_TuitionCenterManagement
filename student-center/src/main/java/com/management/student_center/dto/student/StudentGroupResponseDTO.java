package com.management.student_center.dto.student;

import java.util.List;
import java.util.Map;

public class StudentGroupResponseDTO {

    private long totalStudents;

    // Cấp học -> Trường -> Danh sách học sinh
    private Map<String, Map<String, List<StudentDTO>>> studentsBySchool;

    // ✅ Cấp học -> Trường -> Tổng số học sinh
    private Map<String, Map<String, Long>> totalStudentsBySchool;

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Map<String, Map<String, List<StudentDTO>>> getStudentsBySchool() {
        return studentsBySchool;
    }

    public void setStudentsBySchool(
            Map<String, Map<String, List<StudentDTO>>> studentsBySchool) {
        this.studentsBySchool = studentsBySchool;
    }

    public Map<String, Map<String, Long>> getTotalStudentsBySchool() {
        return totalStudentsBySchool;
    }

    public void setTotalStudentsBySchool(
            Map<String, Map<String, Long>> totalStudentsBySchool) {
        this.totalStudentsBySchool = totalStudentsBySchool;
    }
}
