package com.permithub.dto.hod;

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
    private Long userId;
    private String employeeId;
    private String name;
    private String fullName; // For frontend compatibility
    private String email;
    private String phone;
    private String profilePicPath;
    private String profilePicture; // For frontend compatibility
    
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    
    private String designation;
    private LocalDate joiningDate;
    
    // Role information
    private Set<String> roles;
    private java.util.List<FacultyRoleAssignmentDTO> roleAssignments;
    
    private Boolean isActive;
    private Boolean isFirstLogin;
    private LocalDateTime createdAt;
}