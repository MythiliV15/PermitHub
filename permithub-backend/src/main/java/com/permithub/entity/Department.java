package com.permithub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false, length = 10)
    private String code;
    
    private String description;
    
    @OneToOne
    @JoinColumn(name = "hod_id")
    private HOD hod;
    
    @Column(name = "total_students")
    @Builder.Default
    private Integer totalStudents = 0;
    
    @Column(name = "total_faculty")
    @Builder.Default
    private Integer totalFaculty = 0;
    
    @OneToMany(mappedBy = "department")
    @Builder.Default
    private List<Faculty> facultyMembers = new ArrayList<>();
    
    @OneToMany(mappedBy = "department")
    @Builder.Default
    private List<Student> students = new ArrayList<>();
}