package com.permithub.dto.hod;

import java.time.LocalDate;
import java.util.Set;

import com.permithub.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultyRequestDTO {
    
    @NotBlank(message = "Employee ID is required")
    private String employeeId;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
    
    @NotNull(message = "Department ID is required")
    private Long departmentId;
    
    private String designation;
    private String qualification;
    private Integer experienceYears;
    private LocalDate joiningDate;
    
    // Roles to assign
    private Set<Role> roles;
    
    // Additional fields
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
    
    // Mentor specific
    private Integer maxMentees;
}
