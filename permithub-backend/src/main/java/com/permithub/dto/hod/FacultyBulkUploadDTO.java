package com.permithub.dto.hod;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultyBulkUploadDTO {
    
    @NotNull(message = "Excel file is required")
    private MultipartFile file;
    
    @NotNull(message = "Department ID is required")
    private Long departmentId;
    
    @Builder.Default
    private Boolean sendWelcomeEmail = true;
    @Builder.Default
    private Boolean passwordResetRequired = true;
    
    // Default values for all faculty in this upload
    private String defaultDesignation;
    private Integer defaultExperienceYears;
    @Builder.Default
    private String defaultPassword = "Welcome@123";
}