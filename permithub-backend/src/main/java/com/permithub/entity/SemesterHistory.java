package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "semester_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "studentId", nullable = false)
    private Long studentId; // FK -> student_profiles.id
    
    @Column(name = "semesterId", nullable = false)
    private Long semesterId; // FK -> semesters.id
    
    @Column(nullable = false)
    private Integer year;
    
    @Column(length = 10)
    private String section;
    
    @Column(name = "attendancePercentage")
    private BigDecimal attendancePercentage;
    
    private BigDecimal gpa;
    
    @Column(name = "arrearsCount")
    @Builder.Default
    private Integer arrearsCount = 0;
    
    @Column(length = 50)
    @Builder.Default
    private String status = "COMPLETED"; // ONGOING, COMPLETED, DROPPED
    
    @Column(name = "createdAt")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updatedAt")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
