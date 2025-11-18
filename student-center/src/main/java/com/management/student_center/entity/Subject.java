package com.management.student_center.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String grade;

    private BigDecimal price; // per hour

    @Column(nullable = false)
    private String status = "active";

    @Column(nullable = false)
    private Integer maxStudents = 30;

    @Column(nullable = false)
    private Integer sessionsPerWeek = 1;

    private String image;

    @Column(columnDefinition = "TEXT")
    private String note;

    // Quan hệ với StudentSubject
    @OneToMany(mappedBy = "subject")
    @JsonIgnore
    private List<StudentSubject> studentSubjects;

    // Quan hệ với TeacherSubject
    @OneToMany(mappedBy = "subject")
    @JsonIgnore
    private List<TeacherSubject> teacherSubjects;

    // Constructor mặc định
    public Subject() {}

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getMaxStudents() { return maxStudents; }
    public void setMaxStudents(Integer maxStudents) { this.maxStudents = maxStudents; }

    public Integer getSessionsPerWeek() { return sessionsPerWeek; }
    public void setSessionsPerWeek(Integer sessionsPerWeek) { this.sessionsPerWeek = sessionsPerWeek; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public List<StudentSubject> getStudentSubjects() { return studentSubjects; }
    public void setStudentSubjects(List<StudentSubject> studentSubjects) { this.studentSubjects = studentSubjects; }

    public List<TeacherSubject> getTeacherSubjects() { return teacherSubjects; }
    public void setTeacherSubjects(List<TeacherSubject> teacherSubjects) { this.teacherSubjects = teacherSubjects; }
    
    @Transient   // ❗ Không lưu vào DB, nhưng cho phép set/get
    private Long currentStudents;

    // getter / setter
    public Long getCurrentStudents() {
        return currentStudents;
    }

    public void setCurrentStudents(Long currentStudents) {
        this.currentStudents = currentStudents;
    }
}
