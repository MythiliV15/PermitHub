package com.permithub.dto.hod;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemesterDTO {
    
    private Long id;
    
    @NotBlank(message = "Semester name is required")
    private String name;
    
    @NotNull(message = "Year is required")
    private Integer year;
    
    @NotNull(message = "Semester number is required")
    private Integer semesterNumber;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    private Boolean isActive;
    
    @NotNull(message = "Default leave balance is required")
    private Integer defaultLeaveBalance;
    
    @NotNull(message = "Department ID is required")
    private Long departmentId;
    
    // Optional fields
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private LocalDate examStartDate;
    private LocalDate examEndDate;
    private LocalDate resultDate;
    
    private String academicYear;
    private String semesterType; // ODD, EVEN, SUMMER
    
    private Boolean isRegistrationOpen;
    private Boolean isExamPeriod;
    private Boolean isResultDeclared;
    
    private Long createdById;
    private String createdByName;
}