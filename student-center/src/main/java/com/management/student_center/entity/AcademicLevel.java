package com.management.student_center.entity;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "academic_levels")
public class AcademicLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Tiểu học, THCS, THPT

    @Column(name = "order_index")
    private Integer orderIndex;

    @OneToMany(mappedBy = "academicLevel")
    @JsonIgnore
    private List<SubjectType> subjectTypes;

    // ===== Getter & Setter =====
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public List<SubjectType> getSubjectTypes() { return subjectTypes; }
    public void setSubjectTypes(List<SubjectType> subjectTypes) { this.subjectTypes = subjectTypes; }
}