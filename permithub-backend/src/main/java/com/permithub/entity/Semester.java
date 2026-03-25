package com.permithub.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Entity
@Table(name = "semesters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Semester extends BaseEntity {

    @Column(name = "departmentId", nullable = false)
    private Long departmentId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "year")
    private Integer year;

    @Column(name = "semesterNumber")
    private Integer semesterNumber;

    @Column(name = "academicYear", nullable = false, length = 20)
    private String academicYear;

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "registrationStartDate")
    private LocalDate registrationStartDate;

    @Column(name = "registrationEndDate")
    private LocalDate registrationEndDate;

    @Column(name = "examStartDate")
    private LocalDate examStartDate;

    @Column(name = "examEndDate")
    private LocalDate examEndDate;

    @Column(name = "resultDate")
    private LocalDate resultDate;

    @Column(name = "semesterType", length = 20)
    private String semesterType; // ODD, EVEN, SUMMER

    @Column(name = "defaultLeaveBalance", nullable = false)
    @Builder.Default
    private Integer defaultLeaveBalance = 20;

    @Column(name = "isActive", nullable = false)
    @Builder.Default
    private Boolean isActive = false;
}
