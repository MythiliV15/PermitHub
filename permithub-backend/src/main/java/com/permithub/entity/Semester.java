package com.permithub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Entity
@Table(name = "semesters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Semester extends BaseEntity {

    @Column(nullable = false)
    private String name;  // e.g., "Fall 2024", "Spring 2024"

    @Column(nullable = false)
    private Integer year;

    @Column(name = "semester_number")
    private Integer semesterNumber;  // 1-8

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = false;

    @Column(name = "default_leave_balance")
    @Builder.Default
    private Integer defaultLeaveBalance = 20;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // Additional semester management fields
    @Column(name = "registration_start_date")
    private LocalDate registrationStartDate;

    @Column(name = "registration_end_date")
    private LocalDate registrationEndDate;

    @Column(name = "exam_start_date")
    private LocalDate examStartDate;

    @Column(name = "exam_end_date")
    private LocalDate examEndDate;

    @Column(name = "result_date")
    private LocalDate resultDate;

    @Column(name = "academic_year")
    private String academicYear; // e.g., "2024-2025"

    @Column(name = "semester_type")
    @Builder.Default
    private String semesterType = "ODD"; // ODD, EVEN, SUMMER

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "is_registration_open")
    @Builder.Default
    private Boolean isRegistrationOpen = false;

    @Column(name = "is_exam_period")
    @Builder.Default
    private Boolean isExamPeriod = false;

    @Column(name = "is_result_declared")
    @Builder.Default
    private Boolean isResultDeclared = false;
}
