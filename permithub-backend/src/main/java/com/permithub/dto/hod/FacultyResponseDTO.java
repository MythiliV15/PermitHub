package com.permithub.dto.hod;

import com.permithub.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultyResponseDTO {
    private Long id;
    private String employeeId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String profilePicture;
    
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    
    private String designation;
    private String qualification;
    private Integer experienceYears;
    private LocalDate joiningDate;
    
    private Set<Role> roles;
    private Boolean isMentor;
    private Boolean isClassAdvisor;
    private Boolean isEventCoordinator;
    
    private Integer maxMentees;
    private Integer currentMentees;
    private Long menteesCount;
    private Long advisedClassCount;
    
    private String specialization;
    private String cabinNumber;
    private String officePhone;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String bloodGroup;
    private LocalDate dateOfBirth;
    private String address;
    private String city;
    private String state;
    private String pincode;
    
    private Boolean isActive;
    private Boolean isFirstLogin;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    
    // Statistics
    private Long pendingApprovalsCount;
    private Long totalApprovalsCount;
}